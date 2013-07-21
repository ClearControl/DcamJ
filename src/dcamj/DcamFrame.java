package dcamj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

import org.bridj.Pointer;

import dcamj.utils.BufferUtils;

public class DcamFrame
{

	public static LinkedBlockingQueue<DcamFrame> mAvailableFramesQueue = new LinkedBlockingQueue<DcamFrame>(1000);

	public static DcamFrame requestFrame(	final int pBytesPerPixel,
																				final int pWidth,
																				final int pHeight,
																				final int pDepth)
	{
		DcamFrame lDcamFrame;
		do
		{
			lDcamFrame = mAvailableFramesQueue.poll();
		}
		while (lDcamFrame != null && (lDcamFrame.getPixelSizeInBytes() != pBytesPerPixel || lDcamFrame.getWidth() != pWidth
																	|| lDcamFrame.getHeight() != pHeight || lDcamFrame.getDepth() != pDepth));

		if (lDcamFrame == null)
		{
			lDcamFrame = new DcamFrame(	pBytesPerPixel,
																	pWidth,
																	pHeight,
																	pDepth);
		}

		return lDcamFrame;
	}

	public static void releaseFrame(final DcamFrame pDcamFrame)
	{
		mAvailableFramesQueue.offer(pDcamFrame);
	}

	public static void clearFrames()
	{
		final DcamFrame lDcamFrame;
		mAvailableFramesQueue.clear();
	}

	public static void preallocateFrames(	final int pNumberOfFramesToAllocate,
																				final int pBytesPerPixel,
																				final int pWidth,
																				final int pHeight,
																				final int pDepth)
	{
		clearFrames();
		for (int i = 0; i < pNumberOfFramesToAllocate; i++)
		{
			final DcamFrame lRequestedFrame = requestFrame(	pBytesPerPixel,
																											pWidth,
																											pHeight,
																											pDepth);
			lRequestedFrame.release();
		}
	}

	/****************************************************/

	private final ByteBuffer[] mByteBufferArray;
	private final DcamFrame[] mSinglePlaneDcamFrameArray;

	private final int mBytesPerPixel, mWidth, mHeight, mDepth;
	private long mIndex, mTimeStampInNs;

	public DcamFrame(	final int pBytesPerPixel,
										final int pWidth,
										final int pHeight,
										final int pDepth)
	{
		mBytesPerPixel = pBytesPerPixel;
		mWidth = pWidth;
		mHeight = pHeight;
		mDepth = pDepth;
		mByteBufferArray = new ByteBuffer[pDepth];
		mSinglePlaneDcamFrameArray = new DcamFrame[pDepth];

		for (int i = 0; i < pDepth; i++)
		{
			mByteBufferArray[i] = ByteBuffer.allocateDirect(pBytesPerPixel * pWidth
																											* pHeight)
																			.order(ByteOrder.nativeOrder());
			mSinglePlaneDcamFrameArray[i] = new DcamFrame(getSinglePlaneByteBuffer(i),
																										getPixelSizeInBytes(),
																										getWidth(),
																										getHeight());
		}
	}

	public DcamFrame(	final ByteBuffer pSinglePlaneByteBuffer,
										final int pBytesPerPixel,
										final int pWidth,
										final int pHeight)
	{
		mBytesPerPixel = pBytesPerPixel;
		mWidth = pWidth;
		mHeight = pHeight;
		mDepth = 1;
		mByteBufferArray = new ByteBuffer[1];
		mByteBufferArray[0] = pSinglePlaneByteBuffer;
		mSinglePlaneDcamFrameArray = null;
	}

	public final int getWidth()
	{
		return mWidth;
	}

	public final int getHeight()
	{
		return mHeight;
	}

	public final int getDepth()
	{
		return mDepth;
	}

	public final long getIndex()
	{
		return mIndex;
	}

	public void setIndex(final long pIndex)
	{
		mIndex = pIndex;
	}

	public void setTimeStampInNs(final long pTimeStampInNs)
	{
		mTimeStampInNs = pTimeStampInNs;
	}

	public final long getFrameTimeStampInNs()
	{
		return mTimeStampInNs;
	}

	public final int getPixelSizeInBytes()
	{
		return mBytesPerPixel;
	}

	public ByteBuffer getSinglePlaneByteBuffer()
	{
		return getSinglePlaneByteBuffer(0);
	}

	public ByteBuffer getSinglePlaneByteBuffer(final int pIndex)
	{
		return mByteBufferArray[pIndex];
	}

	public long getTotalSizeInBytesForAllPlanes()
	{
		long size = 0;
		for (final ByteBuffer lByteBuffer : mByteBufferArray)
		{
			size += lByteBuffer.capacity();
		}
		return size;
	}

	public boolean getSingleByteBufferForAllPlanes(final ByteBuffer pByteBuffer)
	{
		return getSingleByteBufferForPlanes(pByteBuffer,
																				mByteBufferArray.length);
	}

	public boolean getSingleByteBufferForPlanes(final ByteBuffer pByteBuffer,
																							final int pMaxNumberOfPlanes)
	{
		final long lTotalSizeInBytes = getTotalSizeInBytesForAllPlanes();
		if (pByteBuffer.capacity() != lTotalSizeInBytes)
			return false;

		pByteBuffer.clear();

		for (int i = 0; i < Math.min(	mByteBufferArray.length,
																	pMaxNumberOfPlanes); i++)
		{
			final ByteBuffer lPlaneByteBuffer = mByteBufferArray[i];
			lPlaneByteBuffer.rewind();
			pByteBuffer.put(lPlaneByteBuffer);
		}

		return true;
	}

	public DcamFrame getSinglePlaneDcamFrame(final int pIndex)
	{
		if (mSinglePlaneDcamFrameArray == null)
			return this;
		else
			return mSinglePlaneDcamFrameArray[pIndex];
	}

	public Pointer<Byte> getSinglePlanePointer(final int pIndex)
	{
		return Pointer.pointerToBytes(getSinglePlaneByteBuffer(pIndex));
	}

	public final long getSinglePlaneBufferLengthInBytes(final int pIndex)
	{
		return getPixelSizeInBytes() * getWidth() * getHeight();
	}

	public final void release()
	{
		releaseFrame(this);
	}

	public final void destroy()
	{
		try
		{
			for (final ByteBuffer lByteBuffer : mByteBufferArray)
				if (lByteBuffer != null)
					BufferUtils.destroyDirectByteBuffer(lByteBuffer);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		return String.format(	"DcamFrame [mBytesPerPixel=%s, mWidth=%s, mHeight=%s, mDepth=%s, mIndex=%s, mTimeStampInNs=%s]",
													mBytesPerPixel,
													mWidth,
													mHeight,
													mDepth,
													mIndex,
													mTimeStampInNs);
	}
}
