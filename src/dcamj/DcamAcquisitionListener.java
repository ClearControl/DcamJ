package dcamj;

public interface DcamAcquisitionListener
{

	void frameArrived(DcamAcquisition pDcamAquisition,
	                  long pFrameIndex,
										long pArrivalTimeStamp,
										DcamFrame pDcamFrame);

}
