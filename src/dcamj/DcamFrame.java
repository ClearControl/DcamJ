package dcamj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import org.bridj.BridJ;
import org.bridj.Pointer;

import dcamapi.DCAM_FRAME;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAM_PIXELTYPE;

public class DcamFrame
{
	private final ByteBuffer[] mByteBufferArray;
	private final DcamFrame[] mSinglePlaneDcamFrameArray;

	private int mBytesPerPixel, mWidth, mHeight, mDepth;

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

	public final int getPixelSizeInBytes()
	{
		return mBytesPerPixel;
	}

	public ByteBuffer getSinglePlaneByteBuffer(final int pIndex)
	{
		return mByteBufferArray[pIndex];
	}
	
	public DcamFrame getSinglePlaneDcamFrame(final int pIndex)
	{
		if(mSinglePlaneDcamFrameArray==null)
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

}
