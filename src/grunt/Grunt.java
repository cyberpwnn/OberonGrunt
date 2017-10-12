package grunt;

import java.io.File;

public class Grunt
{
	public static boolean localfs = false;

	public static void main(String[] arg)
	{
		if(arg.length > 0)
		{
			for(String is : arg)
			{
				if(is.equalsIgnoreCase("-fs"))
				{
					File f = new File("f");

					System.out.println("Checking all files in " + f.getAbsolutePath());

					if(f.exists() && f.isDirectory())
					{
						System.out.println("Checking " + f.listFiles().length + " Files");

						for(File i : f.listFiles())
						{
							System.out.println(i.getName() + ": " + i.length());
						}
					}

					else
					{
						System.out.println("Error " + f.getAbsolutePath() + " does not exist (or isnt a folder)");
					}

					return;
				}

				if(is.equalsIgnoreCase("-localfs"))
				{
					localfs = true;
				}
			}
		}

		Client c = new Client();
		c.logIn();
	}
}
