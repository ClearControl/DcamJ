package dcamj;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import dcamj.utils.StopWatch;

public class DcamRecorder implements Closeable
{

	private FileChannel mFileChannel;
	protected boolean mActive = false;

	private final LinkedBlockingQueue<DcamFrame> mFrameQueue;
	private volatile long mElapsedTimeForWritingLastFrame;

	public boolean mDebug = true;

	public DcamRecorder(final int pMaxQueueSize)
	{
		super();
		mFrameQueue = new LinkedBlockingQueue<DcamFrame>(pMaxQueueSize);
	}

	public final boolean open(final File pFile)
	{
		try
		{
			mFileChannel = new FileOutputStream(pFile).getChannel();
			return true;
		}
		catch (final FileNotFoundException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}

	public final boolean startDeamon()
	{
		final Runnable lRunnable = new Runnable()
		{

			@Override
			public void run()
			{
				mActive = true;
				while (mActive)
				{
					try
					{
						final DcamFrame lTake = mFrameQueue.take();
						writeToFile(lTake);
					}
					catch (final InterruptedException e)
					{
					}
					catch (final Throwable e)
					{
						System.out.println(e.getLocalizedMessage());
					}
				}
				try
				{
					mFileChannel.close();
				}
				catch (final IOException e)
				{
					System.err.println(e.getLocalizedMessage());
				}

			}
		};
		final Thread lThread = new Thread(lRunnable);
		lThread.setDaemon(true);
		lThread.setName("DcamRecorderDeamon");
		lThread.start();
		return true;

	}

	public boolean asynchronousWrite(final DcamFrame pDcamFrame)
	{
		try
		{
			mFrameQueue.put(pDcamFrame);
			return true;
		}
		catch (final InterruptedException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}

	}

	public int getQueueLength()
	{
		return mFrameQueue.size();
	}

	public int getRemainingCapacity()
	{
		return mFrameQueue.remainingCapacity();
	}

	public boolean writeToFile(final DcamFrame pDcamFrame)
	{
		try
		{
			final StopWatch lStopWatch = StopWatch.start();
			for (int i = 0; i < pDcamFrame.getDepth(); i++)
			{
				final ByteBuffer lByteBuffer = pDcamFrame.getBytesDirectBuffer(i);
				mFileChannel.write(lByteBuffer);
			}
			mFileChannel.force(false);
			mElapsedTimeForWritingLastFrame = lStopWatch.time(TimeUnit.MILLISECONDS);
			// System.out.format("Writing one frame to disk required: %d milliseconds \n",lElapsedTime);
			return true;
		}
		catch (final IOException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}

	@Override
	public void close()
	{
		mActive = false;
		mFrameQueue.clear();
	}

	public long getElapsedTimeForWritingLastFrame()
	{
		return mElapsedTimeForWritingLastFrame;
	}

}
