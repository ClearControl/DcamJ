package dcamj2;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bridj.IntValuedEnum;

import dcamapi.DcamapiLibrary.DCAMERR;

public class DcamBase
{
	private final ConcurrentLinkedQueue<IntValuedEnum<DCAMERR>> mErrorList = new ConcurrentLinkedQueue<IntValuedEnum<DCAMERR>>();

	public boolean mDebug = false;
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

	public final Collection<IntValuedEnum<DCAMERR>> getErrorList()
	{
		return mErrorList;
	}

	public final void displayErrorList()
	{
		System.out.println(mErrorList);
	}

	public final void clearErrorList()
	{
		mErrorList.clear();
	}

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
