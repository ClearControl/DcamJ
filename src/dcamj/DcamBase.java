package dcamj;

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
		mErrorList.add(pError);
		if (mDebug)
		{
			System.out.println(pError);
		}
		else if (mShowErrors && !DcamLibrary.hasSucceeded(pError))
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

	public final void clearErrorList()
	{
		mErrorList.clear();
	}

	public final boolean haveAllSucceeded()
	{
		for (final IntValuedEnum<DCAMERR> lEntry : mErrorList)
		{
			final boolean lHasSuceeded = DcamLibrary.hasSucceeded(lEntry);
			if (!lHasSuceeded)
			{
				return false;
			}
		}
		return true;
	}

}
