package dcamj2;

import dcamapi.DCAMCAP_TRANSFERINFO;
import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj2.utils.StopWatch;

/**
 * Dcam sequence acquisition
 *
 * @author royer
 */
public class DcamSequenceAcquisition
{

  DcamDevice mDcamDevice;
  private long mAcquiredFrameIndex;

  /**
   * Instantiates a Dcam sequence acquisition given a Dcam device
   * 
   * @param pDcamDevice
   *          dcam device
   */
  public DcamSequenceAcquisition(DcamDevice pDcamDevice)
  {
    super();
    mDcamDevice = pDcamDevice;

  }

  /**
   * Acquires a sequence of images
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   */
  public void acquireSequence(long pWidth, long pHeight, long pDepth)
  {
    System.out.println("DcamJ(Runnable): mDcamDevice.getStatus()="
                       + mDcamDevice.getStatus());

    mDcamDevice.getBufferControl();

    mBufferControl.mShowErrors = true;

    mDcamDevice.startSequence();

    final long lNumberOfBuffers =
                                mDcamDevice.getBufferControl()
                                           .getNumberOfSinglePlaneBuffers();

    DCAMCAP_TRANSFERINFO lTransferinfo =
                                       mDcamDevice.getTransferInfo();

    long lFrameCount = lTransferinfo.nFrameCount();

    final DCAMWAIT_EVENT lDcamcapEventToWaitFor =
                                                DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADYORSTOPPED;

    int lWaitTimeout = 5;

    boolean lWaitSuccess = (mDcamDevice.getDcamWait()
                                       .waitForEvent(lDcamcapEventToWaitFor,
                                                     lWaitTimeout));

    final long lAcquisitionTimeStampInNanoseconds =
                                                  StopWatch.absoluteTimeInNanoseconds();
    // System.out.println(System.nanoTime());

    lTransferinfo = mDcamDevice.getTransferInfo();

    final long lNumberOfFramesWrittenByDrivertoBuffers =
                                                       lTransferinfo.nFrameCount();
    final long lDriversFrameIndex =
                                  lNumberOfFramesWrittenByDrivertoBuffers
                                    - 1;
    final long lReceivedFrameIndexInBufferList =
                                               lTransferinfo.nNewestFrameIndex();

    System.out.println("DcamJ(Runnable): lDriversFrameIndex="
                       + lDriversFrameIndex);
    System.out.println("DcamJ(Runnable): lReceivedFrameIndexInBufferList="
                       + lReceivedFrameIndexInBufferList);

    final long lDcamWaitEvent = mDcamDevice.getDcamWait().getEvent();
    final boolean lReceivedStopEvent =
                                     lDcamWaitEvent == DCAMWAIT_EVENT.DCAMCAP_EVENT_STOPPED.value;
    final boolean lReceivedFrameReadyEvent =
                                           lDcamWaitEvent == DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADY.value;

    DcamFrame lDcamFrame = mDcamDevice.getBufferControl()
                                      .getStackDcamFrame();
    lDcamFrame.setIndex(mAcquiredFrameIndex);
    lDcamFrame.setTimeStampInNs(lAcquisitionTimeStampInNanoseconds);

    System.out.println("DcamJ(Runnable):lNumberOfFramesWrittenByDrivertoBuffers="
                       + lNumberOfFramesWrittenByDrivertoBuffers);
    System.out.println("DcamJ(Runnable):lReceivedFrameIndexInBufferList="
                       + lReceivedFrameIndexInBufferList);
    System.out.format("DcamJ(Runnable): Wrote %d frames into external buffers (local frame index=%d) \n",
                      lNumberOfFramesWrittenByDrivertoBuffers,
                      lReceivedFrameIndexInBufferList);/**/

  }

}
