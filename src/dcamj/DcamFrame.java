package dcamj;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.bridj.BridJ;
import org.bridj.Pointer;

import dcamapi.DCAM_FRAME;
import dcamapi.DcamapiLibrary;

public class DcamFrame
{
	private final DCAM_FRAME mFrame;

	public DcamFrame()
	{
		super();
		mFrame = new DCAM_FRAME();
		mFrame.size(BridJ.sizeOf(DCAM_FRAME.class));
		mFrame.iFrame(-1);
	}

	public final int getFrameIndex()
	{
		return (int) mFrame.iFrame();
	}

	public Pointer<DCAM_FRAME> getPointer()
	{
		return Pointer.pointerTo(mFrame);
	}

	public Pointer<?> getRawBuffer()
	{
		return mFrame.buf();
	}

	public final int getWidth()
	{
		return (int) mFrame.width();
	}

	public final int getHeight()
	{
		return (int) mFrame.height();
	}

	public final int getPixelSizeInBytes()
	{
		final int lMono8bit = (int) DcamapiLibrary.DCAM_PIXELTYPE.DCAM_PIXELTYPE_MONO8.value;
		final int lMono16bit = (int) DcamapiLibrary.DCAM_PIXELTYPE.DCAM_PIXELTYPE_MONO16.value;

		final int lPixelType = (int) mFrame.type().value();
		if (lPixelType == lMono8bit)
			return 1;
		if (lPixelType == lMono16bit)
			return 2;
		return 0;
	}

	public final int getBufferLengthInBytes()
	{
		return getWidth() * getHeight() * getPixelSizeInBytes();
	}

	public short[] getShortsArray()
	{
		final int lBufferLength =  getBufferLengthInBytes();
		short[] lShortsArray = getRawBuffer().getShorts(lBufferLength);
		return lShortsArray;
	}
	
	public ShortBuffer getShortsDirectBuffer()
	{
		final int lBufferLength =  getBufferLengthInBytes();
		ShortBuffer lShortBuffer = getRawBuffer().getShortBuffer(lBufferLength);
		return lShortBuffer;
	}
	
	public ByteBuffer getBytesDirectBuffer()
	{
		final int lBufferLength =  getBufferLengthInBytes();
		ByteBuffer lByteBuffer = getRawBuffer().getByteBuffer(lBufferLength);
		return lByteBuffer;
	}
}
