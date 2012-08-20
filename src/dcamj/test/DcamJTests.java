package dcamj.test;

import static org.bridj.Pointer.allocateBytes;
import static org.bridj.Pointer.pointerTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.junit.Test;

import dcamapi.DCAMAPI_INIT;
import dcamapi.DCAMDEV_OPEN;
import dcamapi.DCAMDEV_STRING;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;
import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamapi.DcamapiLibrary.DCAM_IDSTR;
import dcamj.DcamBufferControl;
import dcamj.DcamDevice;
import dcamj.DcamFrame;
import dcamj.DcamLibrary;
import dcamj.utils.StopWatch;

public class DcamJTests
{

	@Test
	public void testSimpleRecording() throws InterruptedException, IOException
	{
		System.out.println("Initializing the DCAM library:");
		DcamLibrary.initialize();

		final int numberOfDevices = DcamLibrary.getNumberOfDevices();
		assertTrue(numberOfDevices >= 1);

		DcamDevice lDcamDevice = DcamLibrary.getDeviceForId(0);
		lDcamDevice.mShowErrors = true;
		lDcamDevice.mDebug = false;

		lDcamDevice.displayDeviceInfo();
		DcamBufferControl lBufferControl = lDcamDevice.getBufferControl();
		lBufferControl.mShowErrors = true;
		lBufferControl.mDebug = false;
		lBufferControl.allocateInternalBuffers(100);
		
		System.out.println("starting sequence acquisition:");
		lDcamDevice.startSequence();

		
		FileChannel lFileChannel = new FileOutputStream("D:/Temp/test.raw").getChannel();
		
		
		final int lNumberOfFrames = 10000;
		StopWatch lStopWatch = StopWatch.start();
		for (int i = 0; i < lNumberOfFrames; i++)
		{
			assertTrue(lDcamDevice.getDcamWait()
														.waitForEvent(DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY,
																					1000));
			//System.out.println("received frame!");
			DcamFrame lDcamFrame = lBufferControl.lockFrame();
			assertNotNull(lDcamFrame);

			ByteBuffer lByteBuffer = lDcamFrame.getBytesDirectBuffer();
			assertTrue(lByteBuffer.isDirect());
			
			lFileChannel.write(lByteBuffer);

			//System.out.println(lShortsDirectBuffer.capacity());
			if(i>0 && i%100==0) getTime(i+1, lStopWatch);
		}
		getTime(lNumberOfFrames, lStopWatch);

		
		
		
		lFileChannel.close();
		
		Thread.sleep(2000);
		
		lDcamDevice.stop();
		lBufferControl.releaseBuffers();
		lDcamDevice.close();
		// System.out.println(lDcamDevice.getStatus());
		
		

		DcamLibrary.uninitialize();
	}

	private void getTime(final int pFramesAcquiredUntilNow, StopWatch lStopWatch)
	{
		long lElapsedTimeInSeconds = lStopWatch.time(TimeUnit.SECONDS);
		final double lFramerate = (double) pFramesAcquiredUntilNow / lElapsedTimeInSeconds;
		System.out.format("Framerate: %g \n", lFramerate);
	}

}
