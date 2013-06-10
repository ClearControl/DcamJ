package dcamj;

import static org.bridj.Pointer.pointerTo;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
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
	private double mExposureInSeconds = 0.001;

	public enum TriggerType
	{
		Internal, Software, ExternalEdge, ExternalFastEdge, ExternalLevel
	};

	private TriggerType mTriggerType = TriggerType.Internal;

	private final boolean mShowErrors = false;
	private final boolean mDebug = false;

	private DcamDevice mDcamDevice;
	private DcamBufferControl mBufferControl;
	private int mNumberOfFramesToCapture;

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
		mExposureInSeconds = exposure;
		if (mProperties != null)
			mExposureInSeconds = mProperties.setAndGetExposure(mExposureInSeconds);
		return mExposureInSeconds;
	}

	public double getExposureInSeconds()
	{
		return mExposureInSeconds;
	}

	public int setFrameWidth(final int pWidth)
	{
		return mWidth = DcamProperties.roundto4(pWidth);
	}

	public int setFrameHeight(final int pHeight)
	{
		return mHeight = DcamProperties.roundto4(pHeight);
	}

	public void setFrameWidthAndHeight(	final int pWidth,
																			final int pHeight)
	{
		mWidth = DcamProperties.roundto4(pWidth);
		mHeight = DcamProperties.roundto4(pHeight);
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

	private boolean allocateBuffers(final int pNumberOfBuffers)
	{
		if (isSequenceAcquisition())
			return allocateExternalBuffers(pNumberOfBuffers);
		else
			return allocateInternalBuffers();
	}

	private boolean allocateExternalBuffers(final int pNumberOfBuffers)
	{
		System.out.format("DcamJ: allocate %d external buffers \n",
											pNumberOfBuffers);
		mBufferControl = getBufferControl();
		mBufferControl.mShowErrors = true;
		mBufferControl.mDebug = false;
		return mBufferControl.allocateExternalBuffers(pNumberOfBuffers);
	}

	private boolean provideExternalBuffers(	final int pNumberOfBuffers,
																					final ByteBuffer pBuffer)
	{
		System.out.format("DcamJ: provide %d external buffers for a total buffer capacity of %d\n",
											pNumberOfBuffers,
											pBuffer.capacity());
		mBufferControl = getBufferControl();
		mBufferControl.mShowErrors = true;
		mBufferControl.mDebug = false;
		return mBufferControl.provideExternalBuffers(	pNumberOfBuffers,
																									pBuffer);
	}

	private boolean allocateInternalBuffers()
	{
		System.out.format("DcamJ: allocate %d internal buffers \n",
											mNumberOfInternalBuffers);
		mBufferControl = getBufferControl();
		mBufferControl.mShowErrors = true;
		mBufferControl.mDebug = false;
		return mBufferControl.allocateInternalBuffers(mNumberOfInternalBuffers);
	}

	public DcamProperties getProperties()
	{
		return mDcamDevice.getProperties();
	}

	private void setCurrentProperties()
	{
		mProperties = mDcamDevice.getProperties();
		mProperties.mShowErrors = mShowErrors;
		mProperties.mDebug = mDebug;

		setExposureInSeconds(mExposureInSeconds);
		System.out.format("DcamJ: exposure set at: %g \n",
											mExposureInSeconds);

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
		startAcquisition(-1, null);
	}

	public final void startAcquisition(final int pNumberOfFramesToCapture)
	{
		startAcquisition(pNumberOfFramesToCapture, null);
	}

	public final boolean startAcquisition(final int pNumberOfFramesToCapture,
																				ByteBuffer pByteBuffer)
	{
		mNumberOfFramesToCapture = pNumberOfFramesToCapture;
		boolean mBufferAllocationSuccess = false;
		if (pByteBuffer != null)
			mBufferAllocationSuccess = provideExternalBuffers(pNumberOfFramesToCapture,
																												pByteBuffer);
		else
			mBufferAllocationSuccess = allocateBuffers(pNumberOfFramesToCapture);

		if (!mBufferAllocationSuccess)
			return false;

		mAcquisitionFinishedSignal = new CountDownLatch(1);
		mDcamAquisitionRunnable = new DcamAquisitionRunnable();
		mAcquisitionThread = new Thread(mDcamAquisitionRunnable);
		mAcquisitionThread.setName("DcamAcquisitionThread");
		mAcquisitionThread.setDaemon(true);
		mAcquisitionThread.setPriority(Thread.MAX_PRIORITY);
		mAcquisitionThread.start();

		if (pNumberOfFramesToCapture > 0)
			try
			{
				mAcquisitionFinishedSignal.await();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		return !mDcamAquisitionRunnable.mTrueIfError;
	}

	private class DcamAquisitionRunnable implements Runnable
	{

		private StopWatch mStopWatch;
		private volatile boolean mTrueIfStarted = false;
		private volatile boolean mStopIfFalse = true;
		private volatile boolean mTrueIfStopped = false;
		private volatile boolean mTrueIfError = false;

		@SuppressWarnings("unused")
		@Override
		public void run()
		{

			try
			{
				System.out.println("DcamJ: starting acquisition:");
				if (isSequenceAcquisition())
				{
					System.out.format("DcamJ: Starting acquisition sequence of %d frames \n",
														mNumberOfFramesToCapture);
					mDcamDevice.startSequence();
				}
				else
				{
					System.out.format("DcamJ: Starting continuous acquisition \n");
					mDcamDevice.startContinuous();
				}

				mTrueIfStarted = true;

				mStopWatch = StopWatch.start();
				mFrameIndex = 0;
				mStopIfFalse = true;

				final int lWaitTimeout = isSequenceAcquisition() ? (int) (3 * 1000 * mNumberOfFramesToCapture * mExposureInSeconds)
																												: (isExternalTriggering()	? 5000
																																									: 1000);
				System.out.format("DcamJ: DcamWait timeout set to %d ms \n",
													lWaitTimeout);

				while (mStopIfFalse)
				{

					// DCAMCAP_EVENT_FRAMEREADYORSTOPPED(2|16),
					final DCAMWAIT_EVENT lDcamcapEventToWaitFor = DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADYORSTOPPED;

					final boolean lWaitSuccess = (mDcamDevice.getDcamWait().waitForEvent(	lDcamcapEventToWaitFor,
																																								lWaitTimeout));

					long lDcamWaitEvent = mDcamDevice.getDcamWait().getEvent();

					final boolean lReceivedStopEvent = lDcamWaitEvent == DCAMWAIT_EVENT.DCAMCAP_EVENT_STOPPED.value;
					final boolean lReceivedFrameReadyEvent = lDcamWaitEvent == DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY.value;

					if (lReceivedStopEvent)
					{
						System.out.println("DcamJ: Received Stop Event");
						break;
					}

					final long lArrivalTimeStampInNanoseconds = mStopWatch.timeInNanoseconds();

					if (!lWaitSuccess)
					{
						System.err.println("DcamJ: waiting for event failed!!!!");
						if (!isExternalTriggering())
						{
							System.err.println("DcamJ: timeout waiting for frame!");
							break;
						}
						continue;
					}

					DcamFrame lDcamFrame = null;
					if (isSequenceAcquisition())
					{

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

					notifyListeners(mFrameIndex,
													lArrivalTimeStampInNanoseconds,
													lDcamFrame);

					if (mDebug && mFrameIndex > 0 && mFrameIndex % 100 == 0)
					{
						printFramerate(mFrameIndex + 1, mStopWatch);
						/*System.out.format("%d ms to process one %dx%d frame. \n",
															lDcamFrame.getWidth(),
															lDcamFrame.getHeight());/**/
					}

					mFrameIndex++;
				}

				if (isSequenceAcquisition())
				{
					final DCAMCAP_TRANSFERINFO lTransferinfo = getTransferinfo();

					final int lNumberOfFramesWrittentoExternalBuffer = (int) lTransferinfo.nFrameCount();

					System.out.format("DcamJ: Wrote %d frames into external buffers (index=%d) \n",
														lNumberOfFramesWrittentoExternalBuffer,
														mFrameIndex);

					mTrueIfError = lNumberOfFramesWrittentoExternalBuffer != mNumberOfFramesToCapture;
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
			finally
			{
				mAcquisitionFinishedSignal.countDown();
			}

		}

	};

	private boolean isSequenceAcquisition()
	{
		return mNumberOfFramesToCapture > 0;
	}

	private void notifyListeners(	final long pFrameCounter,
																final long pArrivalTimeStampInNanoseconds,
																final DcamFrame pDcamFrame)
	{
		for (final DcamAcquisitionListener lDcamAcquisitionListener : mListenersList)
		{
			lDcamAcquisitionListener.frameArrived(this,
																						pFrameCounter,
																						pArrivalTimeStampInNanoseconds,
																						pDcamFrame);
		}

	}

	public final void stopAcquisition()
	{
		mDcamAquisitionRunnable.mStopIfFalse = false;

		try
		{
			mAcquisitionFinishedSignal.await();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	private CountDownLatch mAcquisitionFinishedSignal;

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
