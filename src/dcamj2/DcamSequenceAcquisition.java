package dcamj2;

import dcamapi.DCAMCAP_TRANSFERINFO;
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
   * @param pExposure
   *          exposure
   * 
   * @param pImageSequence
   *          image sequence to use
   * @return true -> success
   */
  public boolean acquireSequence(double pExposure,
                                 DcamImageSequence pImageSequence)
  {
    System.out.println("DcamJ(Runnable): mDcamDevice.getStatus()="
                       + mDcamDevice.getStatus());

    System.out.println("setting ROI");
    mDcamDevice.setCenteredROI(pImageSequence.getWidth()
                               * mDcamDevice.getBinning(),
                               pImageSequence.getHeight() * mDcamDevice.getBinning());
    /*ensureOpenedWithCorrectWidthAndHeight(pImageSequence.getWidth(),
                                          pImageSequence.getHeight());/**/

    if (mDcamDevice.getWidth() != pImageSequence.getWidth()
                                  * mDcamDevice.getBinning()
        || mDcamDevice.getHeight() != pImageSequence.getHeight()
                                      * mDcamDevice.getBinning())
    {
      return false;
    }

    System.out.println("set exposure");
    mDcamDevice.setExposure(pExposure);

    System.out.println("attach buffers");
    mDcamDevice.getBufferControl()
               .attachExternalBuffers(pImageSequence);

    System.out.println("start sequence");
    mDcamDevice.startSequence();

    int lWaitTimeout = (int) (2 * (1000 * pExposure
                                   * pImageSequence.getDepth()));

    int lCurrentPriority = Thread.currentThread().getPriority();
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

    System.out.println("waiting...");
    boolean lWaitSuccess =
                         mDcamDevice.getDcamWait()
                                    .waitForEventStopped(lWaitTimeout);
    final long lAcquisitionTimeStampInNanoseconds =
                                                  StopWatch.absoluteTimeInNanoseconds();
    Thread.currentThread().setPriority(lCurrentPriority);

    DCAMCAP_TRANSFERINFO lTransferinfo =
                                       mDcamDevice.getTransferInfo();

    long lFrameCount = lTransferinfo.nFrameCount();

    System.out.format("Success: %s with n=%d \n",
                      lWaitSuccess,
                      lFrameCount);

    final long lNumberOfFramesWrittenByDrivertoBuffers =
                                                       lTransferinfo.nFrameCount();

    final long lReceivedFrameIndexInBufferList =
                                               lTransferinfo.nNewestFrameIndex();

    System.out.format("DcamJ(Runnable): Wrote %d frames into external buffers (local frame index=%d) \n",
                      lNumberOfFramesWrittenByDrivertoBuffers,
                      lReceivedFrameIndexInBufferList);/**/

    if (lNumberOfFramesWrittenByDrivertoBuffers != pImageSequence.getDepth())
      return false;

    final boolean lReceivedStopEvent =
                                     mDcamDevice.getDcamWait()
                                                .isLastEventStopped();
    final boolean lReceivedFrameReadyEvent =
                                           mDcamDevice.getDcamWait()
                                                      .isLastEventReady();

    System.out.format("stop event: %s, ready event: %s \n",
                      lReceivedStopEvent,
                      lReceivedFrameReadyEvent);

    DcamImageSequence lDcamFrame = mDcamDevice.getBufferControl()
                                              .getStackDcamFrame();

    lDcamFrame.setTimeStampInNs(lAcquisitionTimeStampInNanoseconds);

    mDcamDevice.stop();

    mDcamDevice.getBufferControl().releaseBuffers();

    return true;
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

      if (mDcamDevice.mBufferControl != null)
        mDcamDevice.mBufferControl.releaseBuffers();
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
