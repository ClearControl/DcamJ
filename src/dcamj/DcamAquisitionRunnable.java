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

	public DcamAquisitionRunnable(DcamAcquisition pDcamAcquisition,
																final long pNumberOfFramesToCapture,
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
			if (mDcamAcquisition.mDebug)
			System.out.println("DcamJ: Starting acquisition:");

			mTrueIfStarted = true;
			mDcamAcquisition.mAcquiredFrameIndex = 0;

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
			if (mDcamAcquisition.mDebug)
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

		mDcamAcquisition.mAcquisitionStartedSignal.countDown();



		/*if (mStackAcquisition)
			lWaitTimeout = 3000; // + (int) (10 * 1000 * mNumberOfFramesToCapture *
														// mExposureInSeconds)
		/*else
		{
			if (mDcamAcquisition.isExternalTriggering())
				lWaitTimeout = 5000;
			else
				lWaitTimeout = 3000;
		}/**/


		final long lNumberOfBuffers = mDcamAcquisition.getBufferControl()
																									.getNumberOfSinglePlaneBuffers();

		DCAMCAP_TRANSFERINFO lTransferinfo = mDcamAcquisition.getTransferinfo();
		mDcamAcquisition.mAcquiredFrameIndex = lTransferinfo.nFrameCount();

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


			if (mDcamAcquisition.mDebug)
				System.out.print("waitForEvent.before... ");

			int lWaitTimeout = 5;
			boolean lWaitSuccess = false;
			while (!lWaitSuccess && mStopIfFalse)
			{
				lWaitSuccess = (mDcamAcquisition.mDcamDevice.getDcamWait().waitForEvent(lDcamcapEventToWaitFor,
																																														lWaitTimeout));
			}
			if (mDcamAcquisition.mDebug)
				System.out.println(" ...after.");
			final long lAcquisitionTimeStampInNanoseconds = StopWatch.absoluteTimeInNanoseconds();
			// System.out.println(System.nanoTime());

			lTransferinfo = mDcamAcquisition.getTransferinfo();

			final long lNumberOfFramesWrittenByDrivertoBuffers = lTransferinfo.nFrameCount();
			final long lDriversFrameIndex = lNumberOfFramesWrittenByDrivertoBuffers - 1;
			final long lReceivedFrameIndexInBufferList = lTransferinfo.nNewestFrameIndex();

			if (mDcamAcquisition.mDebug)
			{
				System.out.println("lDriversFrameIndex=" + lDriversFrameIndex);
				System.out.println("lReceivedFrameIndexInBufferList=" + lReceivedFrameIndexInBufferList);
			}

			if (!lWaitSuccess)
			{
				if (!mDcamAcquisition.isExternalTriggering() && !mDcamAcquisition.isSoftwareTriggering())
				{
					System.err.println("DcamJ: waiting for event failed!!!!");
					System.err.format("DcamJ: frame index = %d (local index = %d) out of %d frames to capture (%s acquisition)  \n",
														mDcamAcquisition.mAcquiredFrameIndex,
														lReceivedFrameIndexInBufferList,
														mNumberOfFramesToCapture,
														mStackAcquisition	? "stack"
																							: "single plane");
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
					lDcamFrame = mDcamAcquisition.getBufferControl()
																				.getStackDcamFrame();
					lDcamFrame.setIndex(lDriversFrameIndex);
					lDcamFrame.setTimeStampInNs(lAcquisitionTimeStampInNanoseconds);
					mDcamAcquisition.notifyListeners(	mDcamAcquisition.mAcquiredFrameIndex,
																						lAcquisitionTimeStampInNanoseconds,
																						0,
																						lDcamFrame);
					mDcamAcquisition.mAcquiredFrameIndex++;
					mStopIfFalse = false;
				}
			}

			if (!mStackAcquisition && lReceivedFrameReadyEvent)
			{
				long lFirstFrameNotYetAcquired = mDcamAcquisition.mAcquiredFrameIndex;
				long lNumberOfFramesToAcquire = lDriversFrameIndex - lFirstFrameNotYetAcquired;
				long lRingBufferFrameIndex = (lReceivedFrameIndexInBufferList - lNumberOfFramesToAcquire);
				while (lRingBufferFrameIndex < 0)
					lRingBufferFrameIndex += lNumberOfBuffers;
				lRingBufferFrameIndex %= lNumberOfBuffers;
				for (long lFrameIndex = lFirstFrameNotYetAcquired; lFrameIndex <= lDriversFrameIndex; lFrameIndex++)
				{
					lDcamFrame = mDcamAcquisition.getBufferControl()
																				.getDcamFrameForIndex(lRingBufferFrameIndex);
					lDcamFrame.setIndex(lFrameIndex);
					lDcamFrame.setTimeStampInNs(lAcquisitionTimeStampInNanoseconds);
					mDcamAcquisition.notifyListeners(	lFrameIndex,
																						lAcquisitionTimeStampInNanoseconds,
																						lRingBufferFrameIndex,
																						lDcamFrame);
					mDcamAcquisition.mAcquiredFrameIndex++;
					lRingBufferFrameIndex = (lRingBufferFrameIndex + 1) % lNumberOfBuffers;
				}
			}

			if (lDcamFrame != null)
			{
				if (mDcamAcquisition.mDebug)
					System.out.format("DcamJ: true frame index = %d, acquired frame index = %d (local index = %d) \n",
														lDriversFrameIndex,
														mDcamAcquisition.mAcquiredFrameIndex,
														lReceivedFrameIndexInBufferList);

				lDcamFrame.setIndex(lDriversFrameIndex);
				lDcamFrame.setTimeStampInNs(lAcquisitionTimeStampInNanoseconds);
			}

			if (lReceivedFrameReadyEvent)
			{
				if (mDcamAcquisition.mDebug)
					System.out.println("DcamJ: Received frame ready Event");

				if (!mContinuousAcquisition && !mStackAcquisition
						&& lReceivedFrameIndexInBufferList >= mNumberOfFramesToCapture - 1)
				{
					mStopIfFalse = false;
				}

			}

		}

		// if (!mContinuousAcquisition)
		{
			// System.out.println("getTransferinfo.before");
			lTransferinfo = mDcamAcquisition.getTransferinfo();
			// System.out.println("getTransferinfo.after");

			final int lNumberOfFramesWrittenByDrivertoBuffers = (int) lTransferinfo.nFrameCount();

			final long lReceivedFrameIndexInBufferList = lTransferinfo.nNewestFrameIndex();

			if (mDcamAcquisition.mDebug)
			{
				System.out.println("lNumberOfFramesWrittenByDrivertoBuffers=" + lNumberOfFramesWrittenByDrivertoBuffers);
				System.out.println("lReceivedFrameIndexInBufferList=" + lReceivedFrameIndexInBufferList);
				System.out.format("DcamJ: Wrote %d frames into external buffers (local frame index=%d) \n",
													lNumberOfFramesWrittenByDrivertoBuffers,
													lReceivedFrameIndexInBufferList);/**/
			}

			final boolean lWrongNumberofFramesAcquired = lNumberOfFramesWrittenByDrivertoBuffers != mNumberOfFramesToCapture;
			if (!mContinuousAcquisition && lWrongNumberofFramesAcquired)
			{
				System.err.format("DcamJ: Wrong number of frames acquired!\n");
				mTrueIfError = true;
			}

		}
	}

}