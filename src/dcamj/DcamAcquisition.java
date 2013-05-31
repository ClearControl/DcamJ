package dcamj;

import static org.bridj.Pointer.pointerTo;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bridj.BridJ;
import org.bridj.IntValuedEnum;

import dcamapi.DCAMCAP_TRANSFERINFO;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;
import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj.utils.StopWatch;

public class DcamAcquisition implements Closeable
{

	private final int mDeviceIndex;
	private int mWidth = 2048;
	private int mHeight = 2048;
	private final int mNumberOfInternalBuffers = 256;
	private double mExposure = 0.001;

	public enum TriggerType
	{
		Internal, Software, ExternalEdge, ExternalFastEdge, ExternalLevel
	};

	private TriggerType mTriggerType = TriggerType.Internal;

	private final boolean mShowErrors = false;
	private final boolean mDebug = false;

	private DcamDevice mDcamDevice;
	private DcamBufferControl mBufferControl;
	private boolean mCaptureNFrames;

	private Thread mAcquisitionThread;
	private DcamAquisitionRunnable mDcamAquisitionRunnable;

	public volatile long mFrameIndex = 0;

	private final ArrayList<DcamAcquisitionListener> mListenersList = new ArrayList<DcamAcquisitionListener>();
	private DcamProperties mProperties;

	public DcamAcquisition(final int pDeviceIndex)
	{
		super();
		mDeviceIndex = pDeviceIndex;
	}

	public double setExposureInSeconds(final double exposure)
	{
		mExposure = exposure;
		if (mProperties != null)
			mExposure = mProperties.setAndGetExposure(mExposure);
		return mExposure;
	}

	public double getExposureInSeconds()
	{
		return mExposure;
	}

	public int setFrameWidth(final int pWidth)
	{
		return mWidth = DcamProperties.roundto4(pWidth);
	}

	public int setFrameHeight(final int pHeight)
	{
		return mHeight = DcamProperties.roundto4(pHeight);
	}

	public int getFrameWidth()
	{
		return mWidth;
	}

	public int getFrameHeight()
	{
		return mHeight;
	}

	public int getFrameBytesPerPixel()
	{
		return 2;
	}

	public int getFrameSizeInBytes()
	{
		return mWidth * mHeight * 2;
	}

	public void setTriggerType(final TriggerType pTriggerType)
	{
		mTriggerType = pTriggerType;
		if (mProperties != null)
		{
			if (mTriggerType == TriggerType.ExternalEdge)
			{
				mProperties.setInputTriggerToExternalEdge();
			}
			if (mTriggerType == TriggerType.ExternalFastEdge)
			{
				mProperties.setInputTriggerToExternalFastEdge();
			}
			else if (mTriggerType == TriggerType.ExternalLevel)
			{
				mProperties.setInputTriggerToExternalLevel();
			}
			else if (mTriggerType == TriggerType.Software)
			{
				mProperties.setInputTriggerToSoftware();
			}
			else if (mTriggerType == TriggerType.Internal)
			{
				mProperties.setInputTriggerToInternal();
			}
		}
	}

	public boolean isExternalTriggering()
	{
		return mTriggerType == TriggerType.ExternalEdge || mTriggerType == TriggerType.ExternalFastEdge
						|| mTriggerType == TriggerType.ExternalLevel;
	}

	public void addListener(final DcamAcquisitionListener pDcamAcquisitionListener)
	{
		if (!mListenersList.contains(pDcamAcquisitionListener))
			mListenersList.add(pDcamAcquisitionListener);
	}

	public boolean open()
	{
		System.out.println("DcamJ: Initializing the DCAM library:");
		DcamLibrary.initialize();

		final int lNumberOfDevices = DcamLibrary.getNumberOfDevices();
		System.out.format("DcamJ: Number of devices connected: %d \n",
											lNumberOfDevices);

		if (mDeviceIndex >= lNumberOfDevices)
		{
			System.out.format("DcamJ: device not found for index %d \n",
												mDeviceIndex);
			return false;
		}

		mDcamDevice = DcamLibrary.getDeviceForId(mDeviceIndex);
		mDcamDevice.mShowErrors = mShowErrors;
		mDcamDevice.mDebug = mDebug;

		mDcamDevice.displayDeviceInfo();

		// lProperties.listAllProperties();

		setCurrentProperties();

		return true;
	}

	public void reopen()
	{
		if (mBufferControl != null)
			mBufferControl.releaseBuffers();
		if (mDcamDevice != null)
			mDcamDevice.close();
		mDcamDevice = DcamLibrary.getDeviceForId(mDeviceIndex);
		setCurrentProperties();

	}

	private void allocateBuffers(final int pNumberOfBuffers)
	{
		if (mCaptureNFrames)
			allocateExternalBuffers(pNumberOfBuffers);
		else
			allocateInternalBuffers();
	}

	private void allocateExternalBuffers(final int pNumberOfBuffers)
	{
		System.out.format("DcamJ: allocate %d external buffers \n",
											pNumberOfBuffers);
		mBufferControl = getBufferControl();
		mBufferControl.mShowErrors = true;
		mBufferControl.mDebug = false;
		mBufferControl.allocateExternalBuffers(pNumberOfBuffers);
	}

	private void allocateInternalBuffers()
	{
		System.out.format("DcamJ: allocate %d internal buffers \n",
											mNumberOfInternalBuffers);
		mBufferControl = getBufferControl();
		mBufferControl.mShowErrors = true;
		mBufferControl.mDebug = false;
		mBufferControl.allocateInternalBuffers(mNumberOfInternalBuffers);
	}

	private void setCurrentProperties()
	{
		mProperties = mDcamDevice.getProperties();
		mProperties.mShowErrors = mShowErrors;
		mProperties.mDebug = mDebug;

		setExposureInSeconds(mExposure);
		System.out.format("DcamJ: exposure set at: %g \n", mExposure);

		if (mProperties != null)
			mProperties.setCenteredROI(mWidth, mHeight);
		System.out.format("DcamJ: Centered ROI set cwidth=%d cheight=%d \n",
											mWidth,
											mHeight);

		setTriggerType(mTriggerType);
	}

	public final DcamBufferControl getBufferControl()
	{
		if (mBufferControl == null)
		{
			mBufferControl = new DcamBufferControl(mDcamDevice, this);
		}
		return mBufferControl;
	}

	public final void startAcquisition()
	{
		startAcquisition(-1);
	}

	public final void startAcquisition(final int pNumberOfFramesToCapture)
	{
		mCaptureNFrames = pNumberOfFramesToCapture > 0;
		allocateBuffers(pNumberOfFramesToCapture);
		mDcamAquisitionRunnable = new DcamAquisitionRunnable();
		mAcquisitionThread = new Thread(mDcamAquisitionRunnable);
		mAcquisitionThread.setName("DcamAcquisitionThread");
		mAcquisitionThread.setDaemon(true);
		mAcquisitionThread.setPriority(Thread.MAX_PRIORITY);
		mAcquisitionThread.start();
		while (!mDcamAquisitionRunnable.mTrueIfStarted && !mDcamAquisitionRunnable.mTrueIfError)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (final InterruptedException e)
			{
			}
		}
	}

	private class DcamAquisitionRunnable implements Runnable
	{

		private StopWatch mStopWatch;
		private volatile boolean mTrueIfStarted = false;
		private volatile boolean mStopIfFalse = true;
		private volatile boolean mTrueIfStopped = false;
		private volatile boolean mTrueIfError = false;

		@Override
		public void run()
		{

			try
			{
				System.out.println("DcamJ: starting acquisition:");
				if (mCaptureNFrames)
					mDcamDevice.startSnap();
				else
					mDcamDevice.startSequence();

				mTrueIfStarted = true;

				mStopWatch = StopWatch.start();
				mFrameIndex = 0;
				mStopIfFalse = true;

				final int lWaitTimeout = isExternalTriggering() ? 5000 : 1000;

				while (mStopIfFalse)
				{

					// DCAMCAP_EVENT_FRAMEREADYORSTOPPED(2|16),
					final DCAMWAIT_EVENT lDcamcapEventToWaitFor = DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADYORSTOPPED;

					final boolean lWaitSuccess = (mDcamDevice.getDcamWait().waitForEvent(	lDcamcapEventToWaitFor,
																																								lWaitTimeout));
					final boolean lReceivedStopEvent = mDcamDevice.getDcamWait()
																												.getEvent() == DCAMWAIT_EVENT.DCAMCAP_EVENT_STOPPED.value;
					if (lReceivedStopEvent)
					{
						break;
					}

					final long lArrivalTimeStamp = mStopWatch.time();

					if (!lWaitSuccess)
					{

						if (!isExternalTriggering())
						{
							System.err.println("DcamJ: timeout waiting for frame!");
							break;
						}
						continue;
					}

					DcamFrame lDcamFrame;
					if (mCaptureNFrames)
					{
						lDcamFrame = mBufferControl.getDcamFrameForIndex((int) mFrameIndex);

					}
					else
					{
						lDcamFrame = mBufferControl.lockFrame();
						if (lDcamFrame == null)
						{
							System.err.println("DcamJ: Could not lock frame!");
							break;
						}
					}

					notifyListeners(mFrameIndex, lArrivalTimeStamp, lDcamFrame);

					// System.out.println(lShortsDirectBuffer.capacity());
					if (mDebug && mFrameIndex > 0 && mFrameIndex % 100 == 0)
					{
						printFramerate(mFrameIndex + 1, mStopWatch);
						/*System.out.format("%d ms to process one %dx%d frame. \n",
															lDcamFrame.getWidth(),
															lDcamFrame.getHeight());/**/

					}

					mFrameIndex++;
				}

				if (mCaptureNFrames)
				{
					final DCAMCAP_TRANSFERINFO lTransferinfo = getTransferinfo();

					final int lNumberOfFramesWrittentoExternalBuffer = (int) lTransferinfo.nFrameCount();

					System.out.format("DcamJ: Wrote %d frames into external buffers \n",
														lNumberOfFramesWrittentoExternalBuffer);
				}

				System.out.println("DcamJ: stoping acquisition:");
				mDcamDevice.stop();
				mTrueIfStopped = true;
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				mTrueIfError = true;
			}

		}
	};

	private void notifyListeners(	final long pFrameCounter,
																final long pArrivalTimeStamp,
																final DcamFrame pDcamFrame)
	{
		for (final DcamAcquisitionListener lDcamAcquisitionListener : mListenersList)
		{
			lDcamAcquisitionListener.frameArrived(this,
																						pFrameCounter,
																						pArrivalTimeStamp,
																						pDcamFrame);
		}

	}

	public final void stopAcquisition()
	{
		mDcamAquisitionRunnable.mStopIfFalse = false;
		while (!mDcamAquisitionRunnable.mTrueIfStopped)
		{
			try
			{
				Thread.sleep(200);
			}
			catch (final InterruptedException e)
			{
			}
		}
	}

	public final boolean isAcquiring()
	{
		return mDcamAquisitionRunnable.mTrueIfStarted && !mDcamAquisitionRunnable.mTrueIfStopped;
	}

	@Override
	public final void close()
	{
		System.out.println("mBufferControl.releaseBuffers();");
		try
		{
			Thread.sleep(2000);
		}
		catch (final InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mBufferControl != null)
			mBufferControl.releaseBuffers();
		try
		{
			Thread.sleep(2000);
		}
		catch (final InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("mDcamDevice.close();");

		if (mDcamDevice != null)
			mDcamDevice.close();
		try
		{
			Thread.sleep(2000);
		}
		catch (final InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("DcamLibrary.uninitialize();");
		DcamLibrary.uninitialize();
		try
		{
			Thread.sleep(2000);
		}
		catch (final InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	DCAMCAP_TRANSFERINFO mDcamCapTransfertInfo;

	private DCAMCAP_TRANSFERINFO getTransferinfo()
	{
		mDcamCapTransfertInfo = new DCAMCAP_TRANSFERINFO();
		mDcamCapTransfertInfo.size(BridJ.sizeOf(DCAMCAP_TRANSFERINFO.class));
		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcamcapTransferinfo(	mDcamDevice.getHDCAMPointer(),
																																							pointerTo(mDcamCapTransfertInfo));

		return mDcamCapTransfertInfo;
	}

	private void printFramerate(final long pL,
															final StopWatch lStopWatch)
	{
		final long lElapsedTimeInSeconds = lStopWatch.time(TimeUnit.SECONDS);
		final double lFramerate = (double) pL / lElapsedTimeInSeconds;
		System.out.format("Framerate: %g \n", lFramerate);
	}

}
