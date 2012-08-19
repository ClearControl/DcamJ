package dcamj;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import sun.misc.Unsafe;

import dcamapi.DCAMBUF_ATTACH;
import dcamapi.DCAMDEV_STRING;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;

import static org.bridj.Pointer.pointerTo;
import static org.bridj.Pointer.pointerToBytes;

public class DcamBufferControl extends DcamBase
{

	private DcamDevice mDcamDevice;

	private ArrayList<ByteBuffer> mByteBufferList = new  ArrayList<ByteBuffer>();

	private ArrayList<Pointer<Byte>> mPointerToByteBufferList =new  ArrayList<Pointer<Byte>>();
	
	public DcamBufferControl(DcamDevice pDcamDevice)
	{
		mDcamDevice = pDcamDevice;
	}

	public final boolean allocateInternalBuffers(final int pNumberOfBuffers)
	{
		if (pNumberOfBuffers < 1)
			return false;

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcambufAlloc(mDcamDevice.getHDCAMPointer(),
																																			pNumberOfBuffers);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);
		return lSuccess;
	}

	public final boolean attachExternalBuffers(final int pNumberOfBuffers)
	{
		
		/*
		 * 		long	number_of_buffer = 10;
			DCAMBUF_ATTACH	paramattach;
			memset( &paramattach, 0, sizeof(paramattach) );
			paramattach.size		= sizeof(paramattach);
			paramattach.buffer		= new void*[number_of_buffer];
			paramattach.buffercount	= number_of_buffer;

			long	bufferbytes;
			my_dcamprop_getvalue( hdcam, DCAM_IDPROP_BUFFER_FRAMEBYTES, bufferbytes );
			long	i;
			for( i = 0; i < number_of_buffer; i++ )
				paramattach.buffer[i] = new char[bufferbytes];

		 */
		Pointer<Pointer<?>> lPointerToPointerArray = Pointer.allocatePointers(pNumberOfBuffers);
		
		//TODO: should maybe do something les wastefull here:
		mByteBufferList.clear();
		mPointerToByteBufferList.clear();
		for(int i=0; i<pNumberOfBuffers; i++)
		{
			ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(10);
			Pointer<Byte> lPointerToBytes = pointerToBytes(lByteBuffer); 
			mByteBufferList.add(lByteBuffer);
			mPointerToByteBufferList.add(lPointerToBytes);
			lPointerToPointerArray.set(0, lPointerToBytes);
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

	// DCAMERR DCAMAPI dcambuf_alloc ( HDCAM h, long framecount ); // call
	// dcambuf_release() to free.
	// DCAMERR DCAMAPI dcambuf_attach ( HDCAM h, const DCAMBUF_ATTACH* param );
	// DCAMERR DCAMAPI dcambuf_release ( HDCAM h, long iKind DCAM_DEFAULT_ARG );
	// DCAMERR DCAMAPI dcambuf_lockframe ( HDCAM h, DCAM_FRAME* pFrame );
	// DCAMERR DCAMAPI dcambuf_copyframe ( HDCAM h, DCAM_FRAME* pFrame );
	// DCAMERR DCAMAPI dcambuf_copymetadata ( HDCAM h, DCAM_METADATAHDR* hdr );

}
