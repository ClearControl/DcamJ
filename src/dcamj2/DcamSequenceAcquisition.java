package dcamj2;

import dcamapi.DCAMCAP_TRANSFERINFO;
import dcamapi.DcamapiLibrary.DCAMWAIT_EVENT;
import dcamj2.utils.StopWatch;

/**
 * Dcam sequence acquisition
 *
 * @author royer
 */
public class DcamSequenceAcquisition extends DcamBase
{

  DcamDevice mDcamDevice;

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
   * @param pImageSequence
   *          image sequence to use
   */
  public void acquireSequence(DcamImageSequence pImageSequence)
  {
    System.out.println("DcamJ(Runnable): mDcamDevice.getStatus()="
                       + mDcamDevice.getStatus());

    mDcamDevice.getBufferControl()
               .attachExternalBuffers(pImageSequence);

    mDcamDevice.startSequence();

    final long lDepth = mDcamDevice.getBufferControl()
                                   .getAttachedImageSequenceDepth();

    DCAMCAP_TRANSFERINFO lTransferinfo =
                                       mDcamDevice.getTransferInfo();

    long lFrameCount = lTransferinfo.nFrameCount();

    final DCAMWAIT_EVENT lDcamcapEventToWaitFor =
                                                DCAMWAIT_EVENT.DCAMCAP_EVENT_FRAMEREADYORSTOPPED;

    int lWaitTimeout = 5;

    int lCurrentPriority = Thread.currentThread().getPriority();
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    boolean lWaitSuccess =
                         mDcamDevice.getDcamWait()
                                    .waitForEventReadyOrStopped(lWaitTimeout);
    Thread.currentThread().setPriority(lCurrentPriority);

    final long lDcamWaitEvent = mDcamDevice.getDcamWait()
                                           .getLastEvent();

    final long lAcquisitionTimeStampInNanoseconds =
                                                  StopWatch.absoluteTimeInNanoseconds();
    // System.out.println(System.nanoTime());

    lTransferinfo = mDcamDevice.getTransferInfo();

    final long lNumberOfFramesWrittenByDrivertoBuffers =
                                                       lTransferinfo.nFrameCount();

    final long lReceivedFrameIndexInBufferList =
                                               lTransferinfo.nNewestFrameIndex();

    System.out.println("DcamJ(Runnable): lNumberOfFramesWrittenByDrivertoBuffers="
                       + lNumberOfFramesWrittenByDrivertoBuffers);
    System.out.println("DcamJ(Runnable): lReceivedFrameIndexInBufferList="
                       + lReceivedFrameIndexInBufferList);

    final boolean lReceivedStopEvent =
                                     mDcamDevice.getDcamWait()
                                                .isLastEventStopped();
    final boolean lReceivedFrameReadyEvent =
                                           mDcamDevice.getDcamWait()
                                                      .isLastEventStopped();

    DcamImageSequence lDcamFrame = mDcamDevice.getBufferControl()
                                              .getStackDcamFrame();

    lDcamFrame.setTimeStampInNs(lAcquisitionTimeStampInNanoseconds);

    System.out.println("DcamJ(Runnable):lNumberOfFramesWrittenByDrivertoBuffers="
                       + lNumberOfFramesWrittenByDrivertoBuffers);
    System.out.println("DcamJ(Runnable):lReceivedFrameIndexInBufferList="
                       + lReceivedFrameIndexInBufferList);
    System.out.format("DcamJ(Runnable): Wrote %d frames into external buffers (local frame index=%d) \n",
                      lNumberOfFramesWrittenByDrivertoBuffers,
                      lReceivedFrameIndexInBufferList);/**/

  }

  /**
   * Ensures that camera is opened with the correct image width and height
   * (centered ROI)
   * 
   * @param pRequestedWidth
   *          new requested width
   * @param pRequestedHeight
   *          new requested height
   */
  public void ensureOpenedWithCorrectWidthAndHeight(long pRequestedWidth,
                                                    long pRequestedHeight)
  {
    long lCurrentWidth = mDcamDevice.getWidth();
    long lCurrentHeight = mDcamDevice.getHeight();

    if (lCurrentWidth != pRequestedWidth
        || lCurrentHeight != pRequestedHeight)
    {

      if (mDebug)
        System.out.format("DcamJ: reopening device %d begin \n",
                          mDcamDevice.getDeviceID());

      if (mDcamDevice.getBufferControl() != null)
        mDcamDevice.getBufferControl().releaseBuffers();
      if (mDcamDevice != null)
        mDcamDevice.close();
      mDcamDevice =
                  DcamLibrary.getDeviceForId(mDcamDevice.getDeviceID());
      mDcamDevice.setCenteredROI(pRequestedWidth, pRequestedHeight);
      if (mDebug)
        System.out.format("DcamJ: reopening device %d end \n",
                          mDcamDevice.getDeviceID());
    }
  }

}
