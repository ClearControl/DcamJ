package dcamj;

import dcamapi.DCAMCAP_TRANSFERINFO;
import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj.utils.StopWatch;

class DcamAquisitionRunnable implements Runnable
{

	/**
	 * 
	 */
	private final DcamAcquisition mDcamAcquisition;
	volatile boolean mTrueIfStarted = false;
	volatile boolean mStopContinousIfFalse = true;
	volatile boolean mStopIfFalse = true;
	volatile boolean mTrueIfStopped = false;
	volatile boolean mTrueIfError = false;

	private final long mNumberOfFramesToCapture;
	private final boolean mContinuousAcquisition;
	private final boolean mStackAcquisition;

	public DcamAquisitionRunnable(DcamAcquisition pDcamAcquisition, final long pNumberOfFramesToCapture,
																final boolean pContinuousAcquisition,
																final boolean pStackAcquisition)
	{
		mDcamAcquisition = pDcamAcquisition;
		mNumberOfFramesToCapture = pNumberOfFramesToCapture;
		mContinuousAcquisition = pContinuousAcquisition;
		mStackAcquisition = pStackAcquisition;
	}

	@SuppressWarnings("unused")
	@Override
	public void run()
	{

		try
		{
			System.out.println("DcamJ: Starting acquisition:");

			mTrueIfStarted = true;
			mDcamAcquisition.mFrameIndex = 0;

			if (mStackAcquisition && mContinuousAcquisition)
				while (mStopContinousIfFalse)
				{
					runOnce();
				}
			else
			{
				runOnce();
			}

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			mTrueIfError = true;
		}
		finally
		{
			if (!mStackAcquisition && mContinuousAcquisition)
				mDcamAcquisition.mDcamDevice.stop();
			mTrueIfStopped = true;
			System.out.println("DcamJ: stopping acquisition:");
			mDcamAcquisition.mAcquisitionFinishedSignal.countDown();
		}

	}

	private void runOnce()
	{
		mStopIfFalse = true;

		if (mContinuousAcquisition && !mStackAcquisition)
		{
			if (mDcamAcquisition.mDebug)
				System.out.format("DcamJ: Starting continuous acquisition \n");
			mDcamAcquisition.mDcamDevice.startContinuous();
		}
		else
		{
			if (mDcamAcquisition.mDebug)
				System.out.format("DcamJ: Starting acquisition sequence of %d frames \n",
													mNumberOfFramesToCapture);/**/
			mDcamAcquisition.mDcamDevice.startSequence();
		}

		int lWaitTimeout;

		if (mStackAcquisition)
			lWaitTimeout = 3000; // + (int) (10 * 1000 * mNumberOfFramesToCapture *
														// mExposureInSeconds)
		else
		{
			if (mDcamAcquisition.isExternalTriggering())
				lWaitTimeout = 5000;
			else
				lWaitTimeout = 3000;
		}

		if (mDcamAcquisition.mDebug)
			System.out.format("DcamJ: DcamWait timeout set to %d ms \n",
												lWaitTimeout);/**/

		final long lNumberOfBuffers = mDcamAcquisition.getBufferControl().getNumberOfSinglePlaneBuffers();
		long lLocalFrameIndex = 0;

		while (mStopIfFalse)
		{

			// DCAMCAP_EVENT_FRAMEREADYORSTOPPED(2|16),
			final DCAMWAIT_EVENT lDcamcapEventToWaitFor;

			if (mContinuousAcquisition && !mStackAcquisition)
				lDcamcapEventToWaitFor = DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADYORSTOPPED;
			else if (mStackAcquisition)
				lDcamcapEventToWaitFor = DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADYORSTOPPED;
			else
				lDcamcapEventToWaitFor = DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY;

			mDcamAcquisition.mAcquisitionStartedSignal.countDown();
			if (mDcamAcquisition.mDebug)
				System.out.print("waitForEvent.before... ");
			final boolean lWaitSuccess = (mDcamAcquisition.mDcamDevice.getDcamWait().waitForEvent(	lDcamcapEventToWaitFor,
																																						lWaitTimeout));
			if (mDcamAcquisition.mDebug)
				System.out.println(" ...after.");
			final long lArrivalTimeStampInNanoseconds = StopWatch.absoluteTimeInNanoseconds();
			//System.out.println(System.nanoTime());

			if (!lWaitSuccess)
			{
				System.err.println("DcamJ: waiting for event failed!!!!");
				System.err.format("DcamJ: frame index = %d (local index = %d) out of %d frames to capture (%s acquisition)  \n",
													mDcamAcquisition.mFrameIndex,
													lLocalFrameIndex,
													mNumberOfFramesToCapture,
													mStackAcquisition	? "stack"
																						: "single plane");
				if (!mDcamAcquisition.isExternalTriggering())
				{
					System.err.println("DcamJ: timeout waiting for frame!");
					break;
				}
				continue;
			}

			final long lDcamWaitEvent = mDcamAcquisition.mDcamDevice.getDcamWait()
																							.getEvent();
			final boolean lReceivedStopEvent = lDcamWaitEvent == DCAMWAIT_EVENT.DCAMCAP_EVENT_STOPPED.value;
			final boolean lReceivedFrameReadyEvent = lDcamWaitEvent == DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY.value;

			DcamFrame lDcamFrame = null;

			if (mStackAcquisition && lReceivedStopEvent)
			{
				if (mDcamAcquisition.mDebug)
					System.out.println("DcamJ: Received Stop Event");
				if (mStackAcquisition)
				{
					lDcamFrame = mDcamAcquisition.getBufferControl().getStackDcamFrame();
					lDcamFrame.setIndex(mDcamAcquisition.mFrameIndex);
					lDcamFrame.setTimeStampInNs(lArrivalTimeStampInNanoseconds);
					mDcamAcquisition.notifyListeners(mDcamAcquisition.mFrameIndex,
													lArrivalTimeStampInNanoseconds,
													0,
													lDcamFrame);
					mDcamAcquisition.mFrameIndex++;
					mStopIfFalse = false;
				}
			}

			if (!mStackAcquisition && lReceivedFrameReadyEvent)
			{
				lDcamFrame = mDcamAcquisition.getBufferControl().getDcamFrameForIndex(lLocalFrameIndex);
				lDcamFrame.setIndex(mDcamAcquisition.mFrameIndex);
				lDcamFrame.setTimeStampInNs(lArrivalTimeStampInNanoseconds);
				mDcamAcquisition.notifyListeners(mDcamAcquisition.mFrameIndex,
												lArrivalTimeStampInNanoseconds,
												lLocalFrameIndex,
												lDcamFrame);
				mDcamAcquisition.mFrameIndex++;
			}

			if (lDcamFrame != null)
			{
				if (mDcamAcquisition.mDebug)
					System.out.format("DcamJ: frame index = %d (local index = %d) \n",
														mDcamAcquisition.mFrameIndex,
														lLocalFrameIndex);

				lDcamFrame.setIndex(mDcamAcquisition.mFrameIndex);
				lDcamFrame.setTimeStampInNs(lArrivalTimeStampInNanoseconds);
			}

			if (lReceivedFrameReadyEvent)
			{
				if (mDcamAcquisition.mDebug)
					System.out.println("DcamJ: Received frame ready Event");

				if (!mContinuousAcquisition && !mStackAcquisition
						&& lLocalFrameIndex >= mNumberOfFramesToCapture - 1)
				{
					mStopIfFalse = false;
				}

				lLocalFrameIndex = (lLocalFrameIndex + 1) % lNumberOfBuffers;
			}

		}

		// if (!mContinuousAcquisition)
		{
			// System.out.println("getTransferinfo.before");
			final DCAMCAP_TRANSFERINFO lTransferinfo = mDcamAcquisition.getTransferinfo();
			// System.out.println("getTransferinfo.after");

			final int lNumberOfFramesWrittentoExternalBuffer = (int) lTransferinfo.nFrameCount();

			if (mDcamAcquisition.mDebug)
				System.out.format("DcamJ: Wrote %d frames into external buffers (local frame index=%d) \n",
													lNumberOfFramesWrittentoExternalBuffer,
													lLocalFrameIndex);/**/

			final boolean lWrongNumberofFramesAcquired = lNumberOfFramesWrittentoExternalBuffer != mNumberOfFramesToCapture;
			if (lWrongNumberofFramesAcquired)
			{
				System.err.format("DcamJ: Wrong number of frames acquired!\n");
				mTrueIfError = true;
			}

		}
	}

}