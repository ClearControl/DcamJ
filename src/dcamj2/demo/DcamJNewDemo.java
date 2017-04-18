package dcamj2.demo;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import dcamj2.DcamDevice;
import dcamj2.DcamLibrary;
import dcamj2.DcamSequenceAcquisition;

/**
 * DcamJ demo
 *
 * @author royer
 */
public class DcamJNewDemo
{

  /**
   * Tests sequence acquisition
   */
  @Test
  public void testSequenceAcquisition()
  {
    DcamLibrary.initialize();

    DcamDevice lDcamDevice = DcamLibrary.getDeviceForId(0);

    assertNotNull(lDcamDevice);

    DcamSequenceAcquisition lDcamSequenceAcquisition =
                                                     new DcamSequenceAcquisition(lDcamDevice);

    lDcamSequenceAcquisition.acquireSequence(512, 512, 10);

  }

}
