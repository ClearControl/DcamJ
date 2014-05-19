package dcamj.demo;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import dcamj.DcamAcquisition;
import dcamj.DcamAcquisition.TriggerType;
import dcamj.DcamAcquisitionListener;
import dcamj.DcamFrame;
import dcamj.utils.StopWatch;

public class DcamJDemo
{

	final static int cNumberOfBuffers = 1000;

	private void getTime(	final int pFramesAcquiredUntilNow,
												final StopWatch lStopWatch)
	{
		final long lElapsedTimeInSeconds = lStopWatch.time(TimeUnit.SECONDS);
		final double lFramerate = (double) pFramesAcquiredUntilNow / lElapsedTimeInSeconds;
		System.out.format("Framerate: %g \n", lFramerate);
	}

	@Test
	public void testDcamAcquisition()	throws InterruptedException,
																		IOException
	{

		final DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	final DcamAcquisition pDcamAquisition,
																final long pAbsoluteFrameIndex,
																final long pArrivalTimeStamp,
																final long pFrameIndexInBufferList,
																final DcamFrame pDcamFrame)
			{
				System.out.format("Frame %d in buffer %d arrived at %d \n",
													pAbsoluteFrameIndex,
													pFrameIndexInBufferList,
													pArrivalTimeStamp);
			}
		});

		lDcamAcquisition.open();
		lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();
		lDcamAcquisition.startAcquisition();
		Thread.sleep(100000);
		lDcamAcquisition.stopAcquisition();
		lDcamAcquisition.close();

	}

	@Test
	public void testDcamAcquisitionWithExternalTriggering()	throws InterruptedException,
																													IOException
	{

		final DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);
		lDcamAcquisition.setTriggerType(TriggerType.ExternalEdge);
		lDcamAcquisition.setExposureInSeconds(0.001);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	final DcamAcquisition pDcamAquisition,
																final long pAbsoluteFrameIndex,
																final long pArrivalTimeStamp,
																final long pFrameIndexInBuffer,
																final DcamFrame pDcamFrame)
			{
				System.out.format("Frame %d in buffer %d arrived at %d \n",
													pAbsoluteFrameIndex,
													pFrameIndexInBuffer,
													pArrivalTimeStamp);
			}
		});

		lDcamAcquisition.open();

		System.out.format("Effective exposure is: %g s \n",
											lDcamAcquisition.getExposureInSeconds());

		lDcamAcquisition.startAcquisition();

		Thread.sleep(5000);
		lDcamAcquisition.stopAcquisition();
		lDcamAcquisition.close();

	}

	static int lDcamFrameCounter;

	@Test
	public void testDcamSequenceAcquisition()	throws InterruptedException,
																						IOException
	{

		final DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);

		final int lNumerOfDcamFrames = 4;
		final int lNumberOfIterations = 10;
		final int lNumberOfFramesToCapture = 512;
		final int lImageResolution = 512;

		lDcamAcquisition.setFrameWidthAndHeight(lImageResolution,
																						lImageResolution);
		lDcamAcquisition.setExposureInSeconds(0.0001);
		if (!lDcamAcquisition.open())
		{
			lDcamAcquisition.close();
			return;
		}

		lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();

		final long lBufferCapacity = lDcamAcquisition.getBufferControl()
																									.computeTotalRequiredmemoryInBytes(lNumberOfFramesToCapture);
		System.out.format("RequiredMemory is: %d MB \n",
											lBufferCapacity / 1000000);

		final DcamFrame[] lDcamFrameArray = new DcamFrame[lNumerOfDcamFrames];
		for (int i = 0; i < lNumerOfDcamFrames; i++)
			lDcamFrameArray[i] = new DcamFrame(	2,
																					lImageResolution,
																					lImageResolution,
																					lNumberOfFramesToCapture);

		lDcamFrameCounter = 0;

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	final DcamAcquisition pDcamAquisition,
																final long pAbsoluteFrameIndex,
																final long pArrivalTimeStamp,
																final long pFrameIndexInBuffer,
																final DcamFrame pDcamFrame)
			{
				System.out.format("Frame %d in buffer %d arrived at %d \n",
													pAbsoluteFrameIndex,
													pFrameIndexInBuffer,
													pArrivalTimeStamp);/**/
				assertTrue(pDcamFrame.getDepth() != 1);

			}
		});

		System.gc();
		final StopWatch lStopWatch = StopWatch.start();
		for (int i = 0; i < lNumberOfIterations; i++)
		{
			// System.out.println("ITERATION=" + i);
			assertTrue(lDcamAcquisition.startAcquisition(	false,
																										true,
																										lDcamFrameArray[lDcamFrameCounter]));
			// lDcamAcquisition.stopAcquisition();

			// Thread.sleep(1000);
			lDcamFrameCounter = (lDcamFrameCounter + 1) % lNumerOfDcamFrames;
			final DcamFrame lNewDcamFrame = lDcamFrameArray[lDcamFrameCounter];
			lDcamAcquisition.getBufferControl()
											.attachExternalBuffers(lNewDcamFrame);

		}
		final long lTimeInSeconds = lStopWatch.time(TimeUnit.SECONDS);
		final double lSpeed = lNumberOfIterations * lNumberOfFramesToCapture
													/ (lTimeInSeconds);
		System.out.format("acquisition speed: %g frames/s \n", lSpeed);

		while (lDcamAcquisition.isAcquiring())
		{
			Thread.sleep(100);
		}

		for (int j = 0; j < lNumerOfDcamFrames; j++)
			for (int i = 0; i < lDcamFrameArray[j].getDepth(); i++)
			{
				final double average = computeAverageInBuffer(lDcamFrameArray[j].getSinglePlaneByteBuffer(i));
				System.out.format("avg=%g \n", average);
				assertTrue(average != 0);
			}

		lDcamAcquisition.close();

	}

	private double computeAverageInBuffer(final ByteBuffer pByteBuffer)
	{
		double average = 0;

		pByteBuffer.clear();
		final int lCapacity = pByteBuffer.capacity();
		final double lInverse = 1 / (double) lCapacity;
		while (pByteBuffer.hasRemaining())
		{
			final int lShort = pByteBuffer.getShort();
			average = average + lShort * lInverse;
		}
		return average;
	}

}
