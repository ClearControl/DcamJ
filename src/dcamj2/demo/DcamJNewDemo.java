package dcamj2.demo;

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
    
    DcamSequenceAcquisition lDcamSequenceAcquisition = new DcamSequenceAcquisition(lDcamDevice);
    
    
    
  }

}
