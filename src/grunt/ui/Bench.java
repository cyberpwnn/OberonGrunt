package grunt.ui;

import java.text.DecimalFormat;

public class Bench
{
	private long msx;
	private String m;

	public Bench(String name)
	{
		m = name;
		msx = System.nanoTime();
	}

	public void stop(String name)
	{
		System.out.println(m + " > " + name + " took " + nsMs((System.nanoTime() - msx), 2) + "ms");
	}

	private static String f(double i, int p)
	{
		String form = "#";

		if(p > 0)
		{
			form = form + "." + repeat("#", p);
		}

		DecimalFormat DF = new DecimalFormat(form);

		return DF.format(i);
	}

	private static String repeat(String s, int n)
	{
		if(s == null)
		{
			return null;
		}

		final StringBuilder sb = new StringBuilder();

		for(int i = 0; i < n; i++)
		{
			sb.append(s);
		}

		return sb.toString();
	}

	public static String nsMs(long ns)
	{
		return f(ns / 1000000.0, 0);
	}

	public static String nsMs(long ns, int p)
	{
		return f(ns / 1000000.0, p);
	}

}
