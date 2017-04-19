package dcamj2.demo;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import dcamj2.DcamDevice;
import dcamj2.DcamImageSequence;
import dcamj2.DcamLibrary;
import dcamj2.DcamSequenceAcquisition;

/**
 * DcamJ demo
 *
 * @author royer
 */
public class DcamJDemo
{

  /**
   * Tests sequence acquisition
   */
  @Test
  public void testSequenceAcquisition()
  {
    int lWidth = 512;
    int lHeight = 512;
    int lDepth = 10;

    DcamLibrary.initialize();

    DcamDevice lDcamDevice = DcamLibrary.getDeviceForId(0);

    assertNotNull(lDcamDevice);

    DcamSequenceAcquisition lDcamSequenceAcquisition =
                                                     new DcamSequenceAcquisition(lDcamDevice);

    DcamImageSequence lSequence = new DcamImageSequence(2, lWidth, lHeight, lDepth, false);
    
    lDcamSequenceAcquisition.acquireSequence(lSequence);

    
  }

}
