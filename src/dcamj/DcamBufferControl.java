package dcamj;

import static org.bridj.Pointer.pointerTo;
import static org.bridj.Pointer.pointerToBytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import dcamapi.DCAMBUF_ATTACH;
import dcamapi.DCAM_FRAME;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;
import dcamapi.DcamapiLibrary.DCAMIDPROP;

public class DcamBufferControl extends DcamBase
{

	private final DcamDevice mDcamDevice;
	private DcamAcquisition mDcamAcquisition;

	private final ArrayList<ByteBuffer> mByteBufferList = new ArrayList<ByteBuffer>();
	private final ArrayList<Pointer<Byte>> mPointerToByteBufferList = new ArrayList<Pointer<Byte>>();
	private final ArrayList<DcamFrame> mDcamFrameList = new ArrayList<DcamFrame>();

	private ByteBuffer mExternalProvidedByteBuffer;

	private final DcamFrame mDcamFrameForInternalBuffer = new DcamFrame();


	public DcamBufferControl(	final DcamDevice pDcamDevice,
														final DcamAcquisition pDcamAcquisition)
	{
		mDcamDevice = pDcamDevice;
		mDcamAcquisition = pDcamAcquisition;
	}

	public final boolean allocateInternalBuffers(final int pNumberOfBuffers)
	{
		if (pNumberOfBuffers < 1)
		{
			return false;
		}

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufAlloc(mDcamDevice.getHDCAMPointer(),
																																			pNumberOfBuffers);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		return lSuccess;
	}

	public final DcamFrame lockFrame()
	{
		final Pointer<DCAM_FRAME> lPointer = mDcamFrameForInternalBuffer.getPointer();

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufLockframe(mDcamDevice.getHDCAMPointer(),
																																					lPointer);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		if (!lSuccess)
		{
			return null;
		}
		return mDcamFrameForInternalBuffer;
	}

	public final DcamFrame copyFrame()
	{
		final Pointer<DCAM_FRAME> lPointer = mDcamFrameForInternalBuffer.getPointer();

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufCopyframe(mDcamDevice.getHDCAMPointer(),
																																					lPointer);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		if (!lSuccess)
		{
			return null;
		}
		return mDcamFrameForInternalBuffer;
	}

	public final boolean allocateExternalBuffers(final int pNumberOfBuffers)
	{

		final int lImageSizeInBytes = (int) mDcamDevice.getProperties()
																										.getPropertyValue(DCAMIDPROP.DCAM_IDPROP_BUFFER_FRAMEBYTES);

		if (mByteBufferList.size() >= pNumberOfBuffers && mDcamFrameList.get(0)
																																		.getBufferLengthInBytes() == mDcamAcquisition.getFrameSizeInBytes())
			return true;

		final Pointer<Pointer<?>> lPointerToPointerArray = Pointer.allocatePointers(pNumberOfBuffers);

		// TODO: should maybe do something less wasteful here:
		mByteBufferList.clear();
		mPointerToByteBufferList.clear();
		mPointerToByteBufferList.ensureCapacity(pNumberOfBuffers);
		mDcamFrameList.clear();
		mDcamFrameList.ensureCapacity(pNumberOfBuffers);
		for (int i = 0; i < pNumberOfBuffers; i++)
		{
			final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(lImageSizeInBytes)
																								.order(ByteOrder.nativeOrder());
			final Pointer<Byte> lPointerToBytes = pointerToBytes(lByteBuffer);
			mByteBufferList.add(lByteBuffer);
			mPointerToByteBufferList.add(lPointerToBytes);
			lPointerToPointerArray.set(i, lPointerToBytes);

			final DcamFrame lDcamFrame = new DcamFrame();
			lDcamFrame.setRawBufferFromByteBuffer(lByteBuffer);
			mDcamFrameList.add(lDcamFrame);
		}

		final DCAMBUF_ATTACH lDCAMBUF_ATTACH = new DCAMBUF_ATTACH();
		lDCAMBUF_ATTACH.size(BridJ.sizeOf(DCAMBUF_ATTACH.class));
		lDCAMBUF_ATTACH.buffercount(pNumberOfBuffers);
		lDCAMBUF_ATTACH.buffer(lPointerToPointerArray);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufAttach(	mDcamDevice.getHDCAMPointer(),
																																				pointerTo(lDCAMBUF_ATTACH));
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		return lSuccess;
	}

	public final boolean provideExternalBuffers(final int pNumberOfBuffers,
																							final ByteBuffer pByteBuffer)
	{
		final int lImageSizeInBytes = (int) mDcamDevice.getProperties()
																										.getPropertyValue(DCAMIDPROP.DCAM_IDPROP_BUFFER_FRAMEBYTES);

		final boolean lBufferisOfCorrectSize = pNumberOfBuffers * lImageSizeInBytes == pByteBuffer.capacity();

		if (!lBufferisOfCorrectSize)
			return false;

		if (mExternalProvidedByteBuffer == pByteBuffer && lBufferisOfCorrectSize)
			return true;

		mExternalProvidedByteBuffer = pByteBuffer;

		Pointer<Byte> lPointerToByteBuffer = pointerToBytes(mExternalProvidedByteBuffer);

		final Pointer<Pointer<?>> lPointerToPointerArray = Pointer.allocatePointers(pNumberOfBuffers);

		for (long i = 0; i < pNumberOfBuffers; i++)
		{
			final long lIndividualBufferOffset = i * ((long) lImageSizeInBytes);
			@SuppressWarnings("unchecked")
			Pointer<Byte> lPointerToIndividualBuffer = lPointerToByteBuffer.offset(lIndividualBufferOffset);
			mPointerToByteBufferList.add(lPointerToIndividualBuffer);
			lPointerToPointerArray.set(i, lPointerToIndividualBuffer);
		}

		Pointer.release(lPointerToByteBuffer);

		final DCAMBUF_ATTACH lDCAMBUF_ATTACH = new DCAMBUF_ATTACH();
		lDCAMBUF_ATTACH.size(BridJ.sizeOf(DCAMBUF_ATTACH.class));
		lDCAMBUF_ATTACH.buffercount(pNumberOfBuffers);
		lDCAMBUF_ATTACH.buffer(lPointerToPointerArray);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufAttach(	mDcamDevice.getHDCAMPointer(),
																																				pointerTo(lDCAMBUF_ATTACH));
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		return lSuccess;
	}

	public int computeBufferSize(int pNumberOfBuffers)
	{
		final int lImageSizeInBytes = (int) mDcamDevice.getProperties()
																										.getPropertyValue(DCAMIDPROP.DCAM_IDPROP_BUFFER_FRAMEBYTES);

		final int lExternallyProvidedbufferSize = pNumberOfBuffers * lImageSizeInBytes;

		return lExternallyProvidedbufferSize;
	}

	public ByteBuffer getExternalBufferForIndex(int pBufferIndex)
	{
		return mByteBufferList.get(pBufferIndex);
	}

	public DcamFrame getDcamFrameForIndex(int pFrameIndex)
	{
		DcamFrame lDcamFrame = mDcamFrameList.get(pFrameIndex);
		lDcamFrame.setWidth(mDcamAcquisition.getFrameWidth());
		lDcamFrame.setHeight(mDcamAcquisition.getFrameHeight());
		lDcamFrame.setPixelSizeInBytes(mDcamAcquisition.getFrameBytesPerPixel());
		return lDcamFrame;
	}

	public final boolean releaseBuffers()
	{
		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufRelease(mDcamDevice.getHDCAMPointer(),
																																				0);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		mByteBufferList.clear();
		mPointerToByteBufferList.clear();

		return lSuccess;
	}

	// DCAMERR DCAMAPI dcambuf_alloc ( HDCAM h, long framecount ); // call
	// dcambuf_release() to free.
	// DCAMERR DCAMAPI dcambuf_attach ( HDCAM h, const DCAMBUF_ATTACH* param );
	// DCAMERR DCAMAPI dcambuf_release ( HDCAM h, long iKind DCAM_DEFAULT_ARG );
	// DCAMERR DCAMAPI dcambuf_lockframe ( HDCAM h, DCAM_FRAME* pFrame );
	// DCAMERR DCAMAPI dcambuf_copyframe ( HDCAM h, DCAM_FRAME* pFrame );
	// DCAMERR DCAMAPI dcambuf_copymetadata ( HDCAM h, DCAM_METADATAHDR* hdr );

}
