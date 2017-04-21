package dcamj2.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import coremem.recycling.BasicRecycler;
import dcamj2.DcamDevice;
import dcamj2.DcamLibrary;
import dcamj2.DcamSequenceAcquisition;
import dcamj2.imgseq.DcamImageSequence;
import dcamj2.imgseq.DcamImageSequenceFactory;
import dcamj2.imgseq.DcamImageSequenceRequest;

/**
 * DcamJ demo
 *
 * @author royer
 */
public class DcamJDemo
{

  /**
   * Tests sequence acquisition
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testSequenceAcquisition() throws InterruptedException
  {
    int lWidth = 2048;
    int lHeight = 2048;
    int lDepth = 512;

    assertTrue(DcamLibrary.initialize());

    DcamDevice lDcamDevice = new DcamDevice(0);
    assertNotNull(lDcamDevice);

    assertTrue(lDcamDevice.open());

    System.out.println(lDcamDevice.getStatus());

    lDcamDevice.printDeviceInfo();

    DcamSequenceAcquisition lDcamSequenceAcquisition =
                                                     new DcamSequenceAcquisition(lDcamDevice);
    System.out.println("FIRST SEQUENCE");
    DcamImageSequence lSequence1 = new DcamImageSequence(lDcamDevice,
                                                         2,
                                                         lWidth,
                                                         lHeight,
                                                         lDepth);

    assertTrue(lDcamSequenceAcquisition.acquireSequence(0.01,
                                                        lSequence1));

    System.out.println("SECOND SEQUENCE");
    DcamImageSequence lSequence2 = new DcamImageSequence(lDcamDevice,
                                                         2,
                                                         lWidth / 2,
                                                         lHeight / 2,
                                                         lDepth / 2);

    assertTrue(lDcamSequenceAcquisition.acquireSequence(0.01,
                                                        lSequence2));

    lDcamDevice.close();

    assertTrue(DcamLibrary.uninitialize());

  }

  /**
   * Tests sequence acquisition
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testSequenceAcquisitionWithBinning() throws InterruptedException
  {
    int lWidth = 512;
    int lHeight = 512;
    int lDepth = 10;

    assertTrue(DcamLibrary.initialize());

    DcamDevice lDcamDevice = new DcamDevice(0);
    assertNotNull(lDcamDevice);

    assertTrue(lDcamDevice.open());

    System.out.println(lDcamDevice.getStatus());

    lDcamDevice.printDeviceInfo();

    DcamSequenceAcquisition lDcamSequenceAcquisition =
                                                     new DcamSequenceAcquisition(lDcamDevice);

    lDcamDevice.setBinning(2);

    assertEquals(2, lDcamDevice.getBinning());

    DcamImageSequence lSequence = new DcamImageSequence(lDcamDevice,
                                                        2,
                                                        lWidth,
                                                        lHeight,
                                                        lDepth);

    assertTrue(lDcamSequenceAcquisition.acquireSequence(0.01,
                                                        lSequence));

    lDcamDevice.close();

    assertTrue(DcamLibrary.uninitialize());

  }

  /**
   * Tests sequence acquisition
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testRepeatedSequenceAcquisition() throws InterruptedException
  {

    assertTrue(DcamLibrary.initialize());

    DcamDevice lDcamDevice = new DcamDevice(0);
    assertNotNull(lDcamDevice);

    assertTrue(lDcamDevice.open());

    System.out.println(lDcamDevice.getStatus());

    lDcamDevice.printDeviceInfo();

    DcamSequenceAcquisition lDcamSequenceAcquisition =
                                                     new DcamSequenceAcquisition(lDcamDevice);

    for (int i = 0; i < 15; i++)
    {
      System.out.println("SEQUENCE: " + i);

      int lWidth = (int) (512 + Math.random() * 128);
      int lHeight = (int) (512 + Math.random() * 128);
      int lDepth = (int) (10 + Math.random() * 10);
      DcamImageSequence lSequence = new DcamImageSequence(lDcamDevice,
                                                          2,
                                                          lWidth,
                                                          lHeight,
                                                          lDepth);

      System.out.println(lSequence);

      assertTrue(lDcamSequenceAcquisition.acquireSequence(0.01,
                                                          lSequence));

    }

    lDcamDevice.close();

    assertTrue(DcamLibrary.uninitialize());

  }

  /**
   * Tests sequence acquisition
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testRepeatedSequenceAcquisitionWithRecycler() throws InterruptedException
  {

    assertTrue(DcamLibrary.initialize());

    DcamDevice lDcamDevice = new DcamDevice(0);
    assertNotNull(lDcamDevice);

    assertTrue(lDcamDevice.open());

    System.out.println(lDcamDevice.getStatus());

    lDcamDevice.printDeviceInfo();

    DcamSequenceAcquisition lDcamSequenceAcquisition =
                                                     new DcamSequenceAcquisition(lDcamDevice);

    DcamImageSequenceFactory lDcamImageSequenceFactory =
                                                       new DcamImageSequenceFactory();
    BasicRecycler<DcamImageSequence, DcamImageSequenceRequest> lRecycler =
                                                                         new BasicRecycler<>(lDcamImageSequenceFactory,
                                                                                             10);

    for (int i = 0; i < 15; i++)
    {
      System.out.println("SEQUENCE: " + i);

      int lWidth = (int) (512 + Math.random() * 128);
      int lHeight = (int) (512 + Math.random() * 128);
      int lDepth = (int) (10 + Math.random() * 10);
      DcamImageSequenceRequest lRequest =
                                        DcamImageSequenceRequest.build(lDcamDevice,
                                                                       2,
                                                                       lWidth,
                                                                       lHeight,
                                                                       lDepth,
                                                                       true);

      DcamImageSequence lSequence = lRecycler.getOrWait(1,
                                                        TimeUnit.SECONDS,
                                                        lRequest);

      System.out.println(lSequence);

      assertTrue(lDcamSequenceAcquisition.acquireSequence(0.01,
                                                          lSequence));

      // we are supposed to do something here with the image sequence, and then
      // release it..
      lSequence.release();

    }

    lDcamDevice.close();

    assertTrue(DcamLibrary.uninitialize());

  }

}
