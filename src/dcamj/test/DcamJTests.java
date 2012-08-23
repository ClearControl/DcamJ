package dcamj.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj.DcamBufferControl;
import dcamj.DcamDevice;
import dcamj.DcamFrame;
import dcamj.DcamLibrary;
import dcamj.DcamProperties;
import dcamj.DcamRecorder;
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
		lDcamDevice.startSequence();

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

}
