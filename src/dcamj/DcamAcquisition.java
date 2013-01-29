package dcamj;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj.utils.StopWatch;

public class DcamAcquisition implements Closeable
{

	private int mDeviceIndex;
	private int mWidth = 2048;
	private int mHeight = 2048;
	private int mNumberOfBuffers = 1000;
	private double mExposure = 0.001;

	public enum TriggerType
	{
		Internal, Software, External
	};

	private TriggerType mTriggerType = TriggerType.Internal;

	private boolean mShowErrors = true;
	private boolean mDebug = true;

	private DcamDevice mDcamDevice;
	private DcamBufferControl mBufferControl;

	private Thread mAcquisitionThread;
	private DcamAquisitionRunnable mDcamAquisitionRunnable;

	public volatile long mFrameIndex = 0;

	private ArrayList<DcamAcquisitionListener> mListenersList = new ArrayList<DcamAcquisitionListener>();
	private DcamProperties mProperties;

	public DcamAcquisition(final int pDeviceIndex)
	{
		super();
		mDeviceIndex = pDeviceIndex;
	}

	public void setExposure(double exposure)
	{
		mExposure = exposure;
		if (mProperties != null)
			mProperties.setAndGetExposure(mExposure);
	}

	public double getExposure()
	{
		return mExposure;
	}

	public void setCenteredRoi(final int pWidth, final int pHeight)
	{
		mWidth = pWidth;
		mHeight = pHeight;
		if (mProperties != null)
			mProperties.setCenteredROI(mWidth, mHeight);
	}

	public void setExternalTrigger(final TriggerType pTriggerType)
	{
		mTriggerType = pTriggerType;
		if (mProperties != null)
		{
			if (mTriggerType == TriggerType.External)
			{
				mProperties.setInputTriggerToExternal();
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

	public void addListener(DcamAcquisitionListener pDcamAcquisitionListener)
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

		mProperties = mDcamDevice.getProperties();
		mProperties.mShowErrors = mShowErrors;
		mProperties.mDebug = mDebug;
		// lProperties.listAllProperties();

		setExposure(mExposure);
		System.out.format("DcamJ: exposure set at: %g \n ", mExposure);

		setCenteredRoi(mWidth, mHeight);
		System.out.format("DcamJ: Centered ROI width=%d height=%d \n",
											mWidth,
											mHeight);

		setExternalTrigger(TriggerType.Internal);

		System.out.format("DcamJ: allocate %d internal buffers \n",
											mNumberOfBuffers);
		mBufferControl = mDcamDevice.getBufferControl();
		mBufferControl.mShowErrors = true;
		mBufferControl.mDebug = false;
		mBufferControl.allocateInternalBuffers(mNumberOfBuffers);

		return true;
	}

	private class DcamAquisitionRunnable implements Runnable
	{

		private StopWatch mStopWatch;
		private volatile boolean mTrueIfStarted = false;
		private volatile boolean mStopIfFalse = true;
		private volatile boolean mTrueIfStopped = false;

		@Override
		public void run()
		{

			System.out.println("DcamJ: starting acquisition:");
			mDcamDevice.startSequence();
			mTrueIfStarted = true;

			mStopWatch = StopWatch.start();
			mFrameIndex = 0;
			mStopIfFalse = true;
			while (mStopIfFalse)
			{
				boolean lWaitSuccess = (mDcamDevice.getDcamWait().waitForEvent(	DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY,
																																				1000));
				final long lArrivalTimeStamp = mStopWatch.time();

				if (!lWaitSuccess)
					break;

				final DcamFrame lDcamFrame = mBufferControl.lockFrame();
				if (lDcamFrame == null)
					break;

				notifyListeners(mFrameIndex, lArrivalTimeStamp, lDcamFrame);

				// System.out.println(lShortsDirectBuffer.capacity());
				if (mDebug && mFrameIndex > 0 && mFrameIndex % 100 == 0)
				{
					printFramerate(mFrameIndex + 1, mStopWatch);
					/*System.out.format("%d ms to write one %dx%d frame. \n",
														lDcamFrame.getWidth(),
														lDcamFrame.getHeight());/**/

				}

				mFrameIndex++;
			}
			System.out.println("DcamJ: stoping acquisition:");
			mDcamDevice.stop();
			mTrueIfStopped = true;

		}

	};

	public final void startAcquisition()
	{
		mDcamAquisitionRunnable = new DcamAquisitionRunnable();
		mAcquisitionThread = new Thread(mDcamAquisitionRunnable);
		mAcquisitionThread.setName("DcamAcquisitionThread");
		mAcquisitionThread.setDaemon(true);
		mAcquisitionThread.setPriority(Thread.MAX_PRIORITY - 1);
		mAcquisitionThread.start();
		while (!mDcamAquisitionRunnable.mTrueIfStarted)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}

	}

	private void notifyListeners(	long pFrameCounter,
																long pArrivalTimeStamp,
																DcamFrame pDcamFrame)
	{
		for (DcamAcquisitionListener lDcamAcquisitionListener : mListenersList)
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
			catch (InterruptedException e)
			{
			}
		}
	}

	public final boolean isAcquiring()
	{
		return mDcamAquisitionRunnable.mTrueIfStarted && !mDcamAquisitionRunnable.mTrueIfStopped;
	}

	public final void close()
	{
		System.out.println("mBufferControl.releaseBuffers();");
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mBufferControl.releaseBuffers();
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("mDcamDevice.close();");
		mDcamDevice.close();
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
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
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printFramerate(final long pL,
															final StopWatch lStopWatch)
	{
		final long lElapsedTimeInSeconds = lStopWatch.time(TimeUnit.SECONDS);
		final double lFramerate = (double) pL / lElapsedTimeInSeconds;
		System.out.format("Framerate: %g \n", lFramerate);
	}

}
