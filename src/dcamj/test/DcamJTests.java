package dcamj.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj.DcamAcquisition;
import dcamj.DcamAcquisitionListener;
import dcamj.DcamBufferControl;
import dcamj.DcamDevice;
import dcamj.DcamFrame;
import dcamj.DcamLibrary;
import dcamj.DcamProperties;
import dcamj.DcamRecorder;
import dcamj.DcamAcquisition.TriggerType;
import dcamj.utils.StopWatch;

public class DcamJTests
{

	final static int cNumberOfBuffers = 1000;

	@Test
	public void testSimpleRecording()	throws InterruptedException,
																		IOException
	{
		System.out.println("Initializing the DCAM library:");
		DcamLibrary.initialize();

		final int numberOfDevices = DcamLibrary.getNumberOfDevices();
		assertTrue(numberOfDevices >= 1);

		final DcamDevice lDcamDevice = DcamLibrary.getDeviceForId(0);
		lDcamDevice.mShowErrors = true;
		lDcamDevice.mDebug = false;

		final DcamProperties lProperties = lDcamDevice.getProperties();
		lProperties.mShowErrors = true;
		lProperties.mDebug = true;
		// lProperties.updatePropertyList();

		lProperties.listAllProperties();

		final double lExposure = lProperties.setAndGetExposure(0.001);
		System.out.format("Exposure=%g \n ", lExposure);

		lProperties.setCenteredROI(512, 512);

		lDcamDevice.displayDeviceInfo();
		final DcamBufferControl lBufferControl = lDcamDevice.getBufferControl();
		lBufferControl.mShowErrors = true;
		lBufferControl.mDebug = false;
		lBufferControl.allocateInternalBuffers(cNumberOfBuffers);

		System.out.println("starting sequence acquisition:");
		lDcamDevice.startContinuous();

		final DcamRecorder lDcamRecorder = new DcamRecorder(cNumberOfBuffers);
		lDcamRecorder.open(new File("D:/Temp/test.raw"));
		lDcamRecorder.startDeamon();

		final int lNumberOfFrames = 10000;
		final StopWatch lStopWatch = StopWatch.start();
		for (int i = 0; i < lNumberOfFrames; i++)
		{
			assertTrue(lDcamDevice.getDcamWait()
														.waitForEvent(DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY,
																					1000));
			// System.out.println("received frame!");
			final DcamFrame lDcamFrame = lBufferControl.lockFrame();
			assertNotNull(lDcamFrame);

			lDcamRecorder.asynchronousWrite(lDcamFrame);

			// System.out.println(lShortsDirectBuffer.capacity());
			if (i > 0 && i % 100 == 0)
			{
				getTime(i + 1, lStopWatch);
				System.out.format("Queue length= %d,  %d ms to write one %dx%d frame. \n",
													lDcamRecorder.getQueueLength(),
													lDcamRecorder.getElapsedTimeForWritingLastFrame(),
													lDcamFrame.getWidth(),
													lDcamFrame.getHeight());

			}
		}
		getTime(lNumberOfFrames, lStopWatch);

		lDcamRecorder.close();

		Thread.sleep(2000);

		lDcamDevice.stop();
		lBufferControl.releaseBuffers();
		lDcamDevice.close();
		// System.out.println(lDcamDevice.getStatus());

		DcamLibrary.uninitialize();
	}

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

		DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	DcamAcquisition pDcamAquisition,
																long pFrameIndex,
																long pArrivalTimeStamp,
																DcamFrame pDcamFrame)
			{
				System.out.format("Frame %d arrived at %d \n",
													pFrameIndex,
													pArrivalTimeStamp);
			}
		});

		lDcamAcquisition.open();
		lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();
		lDcamAcquisition.startAcquisition();
		Thread.sleep(10000);
		lDcamAcquisition.stopAcquisition();
		lDcamAcquisition.close();

	}

	@Test
	public void testDcamAcquisitionWithExternalTriggering()	throws InterruptedException,
																													IOException
	{

		DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);
		lDcamAcquisition.setTriggerType(TriggerType.ExternalEdge);
		lDcamAcquisition.setExposureInSeconds(0.001);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	DcamAcquisition pDcamAquisition,
																long pFrameIndex,
																long pArrivalTimeStamp,
																DcamFrame pDcamFrame)
			{
				System.out.format("Frame %d arrived at %d \n",
													pFrameIndex,
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

	@Test
	public void testDcamSequenceAcquisition()	throws InterruptedException,
																						IOException
	{

		DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	DcamAcquisition pDcamAquisition,
																long pFrameIndex,
																long pArrivalTimeStamp,
																DcamFrame pDcamFrame)
			{
				System.out.format("Frame %d arrived at %d \n",
													pFrameIndex,
													pArrivalTimeStamp);
				assertTrue(pDcamFrame==null);
			}
		});

		final int lNumberOfIterations = 10;
		final int lNumberOfFramesToCapture = 1000;
		final int lImageResolution = 512;

		lDcamAcquisition.setFrameWidthAndHeight(lImageResolution,
																						lImageResolution);
		lDcamAcquisition.setExposureInSeconds(0.0001);
		lDcamAcquisition.open();

		lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();

		final int lBufferCapacity = lDcamAcquisition.getBufferControl()
																								.computeBufferSize(lNumberOfFramesToCapture);
		System.out.format("Buffer capacity is: %d \n", lBufferCapacity);
		ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(lBufferCapacity)
																				.order(ByteOrder.nativeOrder());

		StopWatch lStopWatch = StopWatch.start();
		for (int i = 0; i < lNumberOfIterations; i++)
		{

			assertTrue(lDcamAcquisition.startAcquisition(lNumberOfFramesToCapture,
																									lByteBuffer));
		}
		long lTimeInSeconds = lStopWatch.time(TimeUnit.SECONDS);
		final double lSpeed = lNumberOfIterations * lNumberOfFramesToCapture
													/ (lTimeInSeconds);
		System.out.format("acquisition speed: %g frames/s \n", lSpeed);

		while (lDcamAcquisition.isAcquiring())
		{
			Thread.sleep(100);
		}

		lDcamAcquisition.stopAcquisition();
		lDcamAcquisition.close();
		
		final double average = computeAverageInBuffer(lByteBuffer);
		System.out.format("avg=%g", average);
		assertTrue(average != 0);



	}

	private double computeAverageInBuffer(ByteBuffer pByteBuffer)
	{
		double average = 0;
		int lCapacity = pByteBuffer.capacity();
		double lInverse = 1/(double)lCapacity;
		for (int i = 0; i < lCapacity / 2; i++)
		{
			int lShort = pByteBuffer.getShort(i);
			//System.out.println(lShort);
			average = average + lShort*lInverse;
		}
		return average;
	}

}
