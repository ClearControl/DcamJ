package dcamj.test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;

import sun.misc.Unsafe;

public class Tests
{

	public static Unsafe getUnsafe()
	{
		try
		{
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		}
		catch (Exception e)
		{ /* ... */
		}
		return null;
	}

	@Test
	public void test()
	{
		getUnsafe().allocateMemory(1000);
	}

}
