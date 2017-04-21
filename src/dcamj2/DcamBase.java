package dcamj2;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bridj.IntValuedEnum;

import dcamapi.DcamapiLibrary.DCAMERR;

/**
 *
 *
 * @author royer
 */
public class DcamBase
{

  private final ConcurrentLinkedQueue<IntValuedEnum<DCAMERR>> mErrorList =
                                                                         new ConcurrentLinkedQueue<IntValuedEnum<DCAMERR>>();

  /**
   * Debug flag
   */
  public boolean mDebug = false;

  /**
   * Show errors flag
   */
  public boolean mShowErrors = false;

  protected final void addError(final IntValuedEnum<DCAMERR> pError)
  {
    if (mDebug)
    {
      mErrorList.add(pError);
      System.out.println(pError);
    }

    if (mShowErrors && !DcamLibrary.hasSucceeded(pError))
    {
      System.err.println(pError);
    }
  }

  protected final boolean addErrorToListAndCheckHasSucceeded(final IntValuedEnum<DCAMERR> lError)
  {
    addError(lError);
    final boolean lSuccess = DcamLibrary.hasSucceeded(lError);
    return lSuccess;
  }

  /**
   * Returns list of errors that occurred.
   * 
   * @return error list
   */
  public final Collection<IntValuedEnum<DCAMERR>> getErrorList()
  {
    return mErrorList;
  }

  /**
   * Display error list
   */
  public final void displayErrorList()
  {
    System.out.println(mErrorList);
  }

  /**
   * Clear error list
   */
  public final void clearErrorList()
  {
    mErrorList.clear();
  }

  /**
   * Checks the error list and makes sure that the error list does not contain -
   * well errors ... :-) != error list empty, since one type of error is success
   *
   * 
   * @return true -> all successes
   */
  public final boolean haveAllSucceeded()
  {
    for (final IntValuedEnum<DCAMERR> lEntry : mErrorList)
    {
      final boolean lHasSucceeded = DcamLibrary.hasSucceeded(lEntry);
      if (!lHasSucceeded)
      {
        return false;
      }
    }
    return true;
  }

}
