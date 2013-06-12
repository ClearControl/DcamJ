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
																long pAbsoluteFrameIndex,
																long pArrivalTimeStamp,
																int pFrameIndexInBufferList,
																DcamFrame pDcamFrame)
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

		DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);
		lDcamAcquisition.setTriggerType(TriggerType.ExternalEdge);
		lDcamAcquisition.setExposureInSeconds(0.001);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	DcamAcquisition pDcamAquisition,
																long pAbsoluteFrameIndex,
																long pArrivalTimeStamp,
																int pFrameIndexInBuffer,
																DcamFrame pDcamFrame)
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

	@Test
	public void testDcamSequenceAcquisition()	throws InterruptedException,
																						IOException
	{

		DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);

		lDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	DcamAcquisition pDcamAquisition,
																long pAbsoluteFrameIndex,
																long pArrivalTimeStamp,
																int pFrameIndexInBuffer,
																DcamFrame pDcamFrame)
			{
				/*System.out.format("Frame %d in buffer %d arrived at %d \n",
													pAbsoluteFrameIndex,
													pFrameIndexInBuffer,
													pArrivalTimeStamp);/**/
				assertTrue(pDcamFrame.getDepth() != 1);
			}
		});

		final int lNumberOfIterations = 300;
		final int lNumberOfFramesToCapture = 300;
		final int lImageResolution = 320;

		lDcamAcquisition.setFrameWidthAndHeight(lImageResolution,
																						lImageResolution);
		lDcamAcquisition.setExposureInSeconds(0.0001);
		if (!lDcamAcquisition.open())
		{
			lDcamAcquisition.close();
			return;
		}

		lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();

		final int lBufferCapacity = lDcamAcquisition.getBufferControl()
																								.computeBufferSize(lNumberOfFramesToCapture);
		System.out.format("Buffer capacity is: %d \n", lBufferCapacity);

		DcamFrame lDcamFrame = new DcamFrame(	2,
																					lImageResolution,
																					lImageResolution,
																					lNumberOfFramesToCapture);

		System.gc();
		StopWatch lStopWatch = StopWatch.start();
		for (int i = 0; i < lNumberOfIterations; i++)
		{
			//System.out.println("ITERATION=" + i);
			assertTrue(lDcamAcquisition.startAcquisition(	lNumberOfFramesToCapture,
																										false,
																										true,
																										lDcamFrame));
			//Thread.sleep(100);
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
		
		final double average = computeAverageInBuffer(lDcamFrame.getBytesDirectBuffer());
		System.out.format("avg=%g \n", average);
		assertTrue(average != 0);
		
		lDcamAcquisition.close();




	}

	private double computeAverageInBuffer(ByteBuffer pByteBuffer)
	{
		double average = 0;
		
		pByteBuffer.clear();
		int lCapacity = pByteBuffer.capacity();
		double lInverse = 1 / (double) lCapacity;
		while(pByteBuffer.hasRemaining())
		{
			int lShort = pByteBuffer.getShort();
			average = average + lShort * lInverse;
		}
		return average;
	}

}
