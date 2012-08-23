package dcamj;

import static org.bridj.Pointer.pointerTo;

import org.bridj.BridJ;
import org.bridj.IntValuedEnum;

import dcamapi.DCAMAPI_INIT;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;

public class DcamLibrary
{

	// Prevents instantiation
	private DcamLibrary()
	{
		super();
	}

	private static boolean sInitialized = false;
	private static long sNumberOfDevices = -1;

	public static final boolean initialize()
	{
		final DCAMAPI_INIT lDCAMAPI_INIT = new DCAMAPI_INIT();
		lDCAMAPI_INIT.size(BridJ.sizeOf(DCAMAPI_INIT.class));
		final IntValuedEnum<DCAMERR> dcamapiInit = DcamapiLibrary.dcamapiInit(pointerTo(lDCAMAPI_INIT));

		final boolean lSuccess = hasSucceeded(dcamapiInit);

		if (lSuccess)
		{
			sInitialized = true;

			sNumberOfDevices = lDCAMAPI_INIT.iDeviceCount();

			return true;
		}
		else
		{
			return false;
		}
	}

	public static final boolean isInitialized()
	{
		return sInitialized;
	}

	public static final int getNumberOfDevices()
	{
		return (int) sNumberOfDevices;
	}

	public static final DcamDevice getDeviceForId(final int pDeviceId)
	{
		if (!isInitialized())
		{
			return null;
		}
		final DcamDevice lDcamDevice = new DcamDevice(pDeviceId);
		return lDcamDevice;
	}

	public static final boolean uninitialize()
	{
		if (!isInitialized())
		{
			return false;
		}
		final IntValuedEnum<DCAMERR> lDcamapiUninit = DcamapiLibrary.dcamapiUninit();
		final boolean lSuccess = hasSucceeded(lDcamapiUninit);
		return lSuccess;
	}

	public static boolean hasSucceeded(final IntValuedEnum<DCAMERR> dcamapiInit)
	{
		return dcamapiInit.toString().contains("DCAMERR_SUCCESS");
	}

}
