package dcamj.utils;

import java.util.concurrent.TimeUnit;

public class StopWatch
{
	long mStartingTime;

	public static StopWatch start()
	{
		return new StopWatch();
	}

	private StopWatch()
	{
		reset();
	}

	public StopWatch reset()
	{
		mStartingTime = System.currentTimeMillis();
		return this;
	}

	public long time()
	{
		long mEndingTime = System.currentTimeMillis();
		return mEndingTime - mStartingTime;
	}

	public long time(TimeUnit unit)
	{
		return unit.convert(time(), TimeUnit.MILLISECONDS);
	}

}