package dcamj;

import static org.bridj.Pointer.pointerTo;
import static org.junit.Assert.assertTrue;

import org.bridj.BridJ;
import org.bridj.IntValuedEnum;

import dcamapi.DCAMAPI_INIT;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;

public class DcamLibrary
{
	private static boolean sInitialized = false;
	private static long sNumberOfDevices = -1;

	static final boolean initialize()
	{
		DCAMAPI_INIT lDCAMAPI_INIT = new DCAMAPI_INIT();
		lDCAMAPI_INIT.size(BridJ.sizeOf(DCAMAPI_INIT.class));
		IntValuedEnum<DCAMERR> dcamapiInit = DcamapiLibrary.dcamapiInit(pointerTo(lDCAMAPI_INIT));

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

	static final boolean isInitialized()
	{
		return sInitialized;
	}

	static final int getNumberOfDevices()
	{
		return (int) sNumberOfDevices;
	}

	static final boolean uninitialize()
	{
		IntValuedEnum<DCAMERR> lDcamapiUninit = DcamapiLibrary.dcamapiUninit();
		final boolean lSuccess = hasSucceeded(lDcamapiUninit);
		return lSuccess;
	}

	public static boolean hasSucceeded(IntValuedEnum<DCAMERR> dcamapiInit)
	{
		return dcamapiInit.toString().contains("DCAMERR_SUCCESS");
	}

}
