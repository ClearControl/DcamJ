package dcamj;

public interface DcamAcquisitionListener
{

	void frameArrived(DcamAcquisition pDcamAquisition,
										long pAbsoluteFrameIndex,
										long pArrivalTimeStampInNanoseconds,
										int pFrameIndexInBufferList,
										DcamFrame pDcamFrame);

}
