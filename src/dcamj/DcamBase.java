package dcamj;

import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bridj.IntValuedEnum;

import dcamapi.DcamapiLibrary.DCAMERR;

public class DcamBase
{
	private ConcurrentLinkedQueue<IntValuedEnum<DCAMERR>> mErrorList;

	protected final void addError(IntValuedEnum<DCAMERR> pError)
	{
		mErrorList.add(pError);
	}

	protected final boolean addErrorToListAndCheckHasSucceeded(final IntValuedEnum<DCAMERR> lError)
	{
		mErrorList.add(lError);
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
		for (IntValuedEnum<DCAMERR> lEntry : mErrorList)
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
