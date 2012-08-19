package dcamj;

import org.bridj.BridJ;

import dcamapi.DCAM_FRAME;

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

}
