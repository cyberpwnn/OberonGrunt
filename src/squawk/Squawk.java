package squawk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Squawk
{
	public static File patches;
	public static File compile;

	public static void applyAllPatches(List<Integer> k) throws IOException
	{
		int latest = getLatestPatch();

		System.out.println("Applying " + latest + " patches");

		for(Integer v : k)
		{
			File f = new File(patches, "P." + v);

			if(f.exists())
			{
				System.out.println("# APPLY P." + v);
				Patch p = new Patch(v, compile);
				p.applyPatch(f);
			}

			else
			{
				System.out.println("# Skipping P." + v + " (MISSING)");
			}
		}
	}

	public static int getLatestPatch()
	{
		int max = 0;

		for(File i : patches.listFiles())
		{
			if(i.getName().startsWith("P."))
			{
				int ver = Integer.valueOf(i.getName().split("\\.")[1]);

				if(ver > max)
				{
					max = ver;
				}
			}
		}

		return max;
	}

	public static void setup(File patchFolder, File fgame)
	{
		compile = fgame;
		compile.mkdirs();
		patches = patchFolder;
		patches.mkdirs();
	}

	public static List<File> files(File dir)
	{
		List<File> ff = new ArrayList<File>();

		for(File i : dir.listFiles())
		{
			if(i.isDirectory())
			{
				ff.addAll(files(i));
			}

			else
			{
				ff.add(i);
			}
		}

		return ff;
	}
}
