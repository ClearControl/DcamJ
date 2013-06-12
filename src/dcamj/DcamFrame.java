package dcamj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import org.bridj.BridJ;
import org.bridj.Pointer;

import dcamapi.DCAM_FRAME;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAM_PIXELTYPE;

public class DcamFrame
{
	private final DCAM_FRAME mFrame;

	private int mDepth = 1;

	public DcamFrame()
	{
		mFrame = new DCAM_FRAME();
		mFrame.size(BridJ.sizeOf(DCAM_FRAME.class));
		mFrame.iFrame(-1);
	}

	public DcamFrame(	final Pointer<?> pPointer,
										final int pPixelSizeInBytes,
										final int pWidth,
										final int pHeight,
										final int pDepth)
	{
		this();
		setRawBufferFromPointer(pPointer);
		setPixelSizeInBytes(pPixelSizeInBytes);
		setWidth(pWidth);
		setHeight(pHeight);
		setDepth(pDepth);
	}

	public DcamFrame(	final ByteBuffer pByteBuffer,
										final int pPixelSizeInBytes,
										final int pWidth,
										final int pHeight,
										final int pDepth)
	{
		this(	Pointer.pointerToBuffer(pByteBuffer),
					pPixelSizeInBytes,
					pWidth,
					pHeight,
					pDepth);
	}

	public DcamFrame(	final int pPixelSizeInBytes,
										final int pWidth,
										final int pHeight,
										final int pDepth)
	{
		this(	ByteBuffer.allocateDirect(pWidth * pHeight
																		* pDepth
																		* pPixelSizeInBytes)
										.order(ByteOrder.nativeOrder()),
					pPixelSizeInBytes,
					pWidth,
					pHeight,
					pDepth);

	}

	public final int getFrameIndex()
	{
		return (int) mFrame.iFrame();
	}

	public Pointer<DCAM_FRAME> getPointer()
	{
		return Pointer.pointerTo(mFrame);
	}

	public void setRawBufferFromPointer(Pointer<?> pPointer)
	{
		mFrame.buf(pPointer);
	}

	public void setRawBufferFromByteBuffer(ByteBuffer pByteBuffer)
	{
		setRawBufferFromPointer(Pointer.pointerToBuffer(pByteBuffer));
	}

	public Pointer<?> getRawBufferPointer()
	{
		return mFrame.buf();
	}

	public final void setWidth(int pWidth)
	{
		mFrame.width(pWidth);
	}

	public final int getWidth()
	{
		return (int) mFrame.width();
	}

	public final void setHeight(int pHeight)
	{
		mFrame.height(pHeight);
	}

	public final int getHeight()
	{
		return (int) mFrame.height();
	}

	public final void setDepth(int pDepth)
	{
		mDepth = pDepth;
	}

	public final int getDepth()
	{
		return mDepth;
	}

	public final void setPixelSizeInBytes(final int pNumberOfBytesPerPixel)
	{
		if (pNumberOfBytesPerPixel == 1)
		{
			mFrame.type(DCAM_PIXELTYPE.DCAM_PIXELTYPE_MONO8);
		}
		else if (pNumberOfBytesPerPixel == 2)
		{
			mFrame.type(DCAM_PIXELTYPE.DCAM_PIXELTYPE_MONO16);
		}
	}

	public final int getPixelSizeInBytes()
	{
		final int lMono8bit = (int) DcamapiLibrary.DCAM_PIXELTYPE.DCAM_PIXELTYPE_MONO8.value;
		final int lMono16bit = (int) DcamapiLibrary.DCAM_PIXELTYPE.DCAM_PIXELTYPE_MONO16.value;

		final int lPixelType = (int) mFrame.type().value();
		if (lPixelType == lMono8bit)
		{
			return 1;
		}
		if (lPixelType == lMono16bit)
		{
			return 2;
		}
		return 0;
	}

	public final long getBufferLengthInBytes()
	{
		return getSinglePlaneBufferLengthInBytes() * getDepth();
	}

	public final long getSinglePlaneBufferLengthInBytes()
	{
		return getPixelSizeInBytes() * getWidth() * getHeight();
	}

	public short[] getShortsArray()
	{
		final int lBufferLength = (int) getBufferLengthInBytes();
		final short[] lShortsArray = getRawBufferPointer().getShorts(lBufferLength);
		return lShortsArray;
	}

	public ShortBuffer getShortsDirectBuffer()
	{
		final int lBufferLength = (int) getBufferLengthInBytes();
		final ShortBuffer lShortBuffer = getRawBufferPointer().getShortBuffer(lBufferLength);
		return lShortBuffer;
	}

	public ByteBuffer getBytesDirectBuffer()
	{
		final int lBufferLength = (int) getBufferLengthInBytes();
		final ByteBuffer lByteBuffer = getRawBufferPointer().getByteBuffer(lBufferLength);
		return lByteBuffer;
	}

}
