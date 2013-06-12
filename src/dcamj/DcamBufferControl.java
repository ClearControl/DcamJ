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

	private final ArrayList<Pointer<Byte>> mPointerToByteBufferList = new ArrayList<Pointer<Byte>>();
	private final ArrayList<DcamFrame> mDcamFrameList = new ArrayList<DcamFrame>();

	private DcamFrame mStackDcamFrame;
	private DCAMBUF_ATTACH mDCAMBUF_ATTACH;
	private Pointer<Pointer<?>> mPointerToPointerArray;

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

	/*
	public final DcamFrame lockFrame()
	{
		final Pointer<DCAM_FRAME> lPointer = mDcamFrameForInternalBuffer.getInternalFramePointer();

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
		final Pointer<DCAM_FRAME> lPointer = mDcamFrameForInternalBuffer.getInternalFramePointer(0);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufCopyframe(mDcamDevice.getHDCAMPointer(),
																																					lPointer);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		if (!lSuccess)
		{
			return null;
		}
		return mDcamFrameForInternalBuffer;
	}
	/**/

	public final boolean provideExternalBuffers(DcamFrame pDcamFrame)
	{
		if (mStackDcamFrame == pDcamFrame)
			return true;

		mStackDcamFrame = pDcamFrame;

		final int lNumberOfBuffers = pDcamFrame.getDepth();

		mPointerToByteBufferList.clear();
		mPointerToByteBufferList.ensureCapacity(lNumberOfBuffers);

		mDcamFrameList.clear();
		mDcamFrameList.ensureCapacity(lNumberOfBuffers);

		mPointerToPointerArray = Pointer.allocatePointers(lNumberOfBuffers);

		for (int i = 0; i < lNumberOfBuffers; i++)
		{
			@SuppressWarnings("unchecked")
			Pointer<Byte> lPointerToIndividualBuffer = (Pointer<Byte>) pDcamFrame.getSinglePlanePointer(i);
			mPointerToByteBufferList.add(lPointerToIndividualBuffer);
			mPointerToPointerArray.set(i, lPointerToIndividualBuffer);

			final DcamFrame lDcamFrame = new DcamFrame(	pDcamFrame.getSinglePlaneByteBuffer(i),
																									pDcamFrame.getPixelSizeInBytes(),
																									pDcamFrame.getWidth(),
																									pDcamFrame.getHeight());
			mDcamFrameList.add(lDcamFrame);
		}

		// Pointer.release(lPointerToByteBuffer);

		if (mDCAMBUF_ATTACH == null)
		{
			mDCAMBUF_ATTACH = new DCAMBUF_ATTACH();
			mDCAMBUF_ATTACH.size(BridJ.sizeOf(DCAMBUF_ATTACH.class));
		}
		mDCAMBUF_ATTACH.buffercount(lNumberOfBuffers);
		mDCAMBUF_ATTACH.buffer(mPointerToPointerArray);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufAttach(	mDcamDevice.getHDCAMPointer(),
																																				pointerTo(mDCAMBUF_ATTACH));
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		return lSuccess;
	}

	/*
	 * public final boolean provideExternalBuffers(DcamFrame pDcamFrame)
	{
		if (mStackDcamFrame == pDcamFrame)
			return true;

		mStackDcamFrame = pDcamFrame;

		final int lNumberOfBuffers = pDcamFrame.getDepth();
		
		
		final ByteBuffer lByteBuffer = pDcamFrame.getBytesDirectBuffer();
		
		if(computeBufferSize(lNumberOfBuffers)!=lByteBuffer.capacity())
			System.err.println("WRONG BUFFER SIZE!!!");;

		mExternalyProvidedByteBuffer = lByteBuffer;

		Pointer<Byte> lPointerToByteBuffer = pointerToBytes(mExternalyProvidedByteBuffer);

		final Pointer<Pointer<?>> lPointerToPointerArray = Pointer.allocatePointers(lNumberOfBuffers);

		mDcamFrameList.clear();
		mDcamFrameList.ensureCapacity(lNumberOfBuffers);

		mPointerToByteBufferList.clear();
		mPointerToByteBufferList.ensureCapacity(lNumberOfBuffers);

		for (long i = 0; i < lNumberOfBuffers; i++)
		{
			final long lIndividualBufferOffset = i * pDcamFrame.getSinglePlaneBufferLengthInBytes();
			@SuppressWarnings("unchecked")
			Pointer<Byte> lPointerToIndividualBuffer = lPointerToByteBuffer.offset(lIndividualBufferOffset);
			mPointerToByteBufferList.add(lPointerToIndividualBuffer);
			lPointerToPointerArray.set(i, lPointerToIndividualBuffer);

			final DcamFrame lDcamFrame = new DcamFrame(	lPointerToIndividualBuffer,
																									pDcamFrame.getPixelSizeInBytes(),
																									pDcamFrame.getWidth(),
																									pDcamFrame.getHeight(),
																									1);
			mDcamFrameList.add(lDcamFrame);
		}

		//Pointer.release(lPointerToByteBuffer);

		if (mDCAMBUF_ATTACH == null)
		{
			mDCAMBUF_ATTACH = new DCAMBUF_ATTACH();
			mDCAMBUF_ATTACH.size(BridJ.sizeOf(DCAMBUF_ATTACH.class));
		}
		mDCAMBUF_ATTACH.buffercount(lNumberOfBuffers);
		mDCAMBUF_ATTACH.buffer(lPointerToPointerArray);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufAttach(	mDcamDevice.getHDCAMPointer(),
																																				pointerTo(mDCAMBUF_ATTACH));
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		return lSuccess;
	}

	 */

	public int computeBufferSize(int pNumberOfBuffers)
	{
		final int lImageSizeInBytes = (int) mDcamDevice.getProperties()
																										.getPropertyValue(DCAMIDPROP.DCAM_IDPROP_BUFFER_FRAMEBYTES);

		final int lExternallyProvidedbufferSize = pNumberOfBuffers * lImageSizeInBytes;

		return lExternallyProvidedbufferSize;
	}

	public DcamFrame getDcamFrameForIndex(int pFrameIndex)
	{
		DcamFrame lDcamFrame = mDcamFrameList.get(pFrameIndex);
		return lDcamFrame;
	}

	public int getNumberOfSinglePlaneBuffers()
	{
		return mDcamFrameList.size();
	}

	public final boolean releaseBuffers()
	{
		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufRelease(mDcamDevice.getHDCAMPointer(),
																																				0);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		for (Pointer<?> lPointer : mPointerToByteBufferList)
			Pointer.release(lPointer);
		mPointerToByteBufferList.clear();

		mDcamFrameList.clear();
		mStackDcamFrame = null;
		return lSuccess;
	}

	public DcamFrame getStackDcamFrame()
	{
		return mStackDcamFrame;
	}

	// DCAMERR DCAMAPI dcambuf_alloc ( HDCAM h, long framecount ); // call
	// dcambuf_release() to free.
	// DCAMERR DCAMAPI dcambuf_attach ( HDCAM h, const DCAMBUF_ATTACH* param );
	// DCAMERR DCAMAPI dcambuf_release ( HDCAM h, long iKind DCAM_DEFAULT_ARG );
	// DCAMERR DCAMAPI dcambuf_lockframe ( HDCAM h, DCAM_FRAME* pFrame );
	// DCAMERR DCAMAPI dcambuf_copyframe ( HDCAM h, DCAM_FRAME* pFrame );
	// DCAMERR DCAMAPI dcambuf_copymetadata ( HDCAM h, DCAM_METADATAHDR* hdr );

}
