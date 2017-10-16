package squawk.util;

public class OSF
{
	public static enum OS
	{
		WINDOWS, UNIX, MACOSX, OTHER,
	}

	public static String rawOS()
	{
		switch(getCurrentOS())
		{
		case MACOSX:
			return "osx";
		case OTHER:
			return "linux";
		case UNIX:
			return "linux";
		case WINDOWS:
			return "windows";
		default:
			return "linux";
		}
	}

	public static OS getCurrentOS()
	{
		String osString = System.getProperty("os.name").toLowerCase();
		if(osString.contains("win"))
		{
			return OS.WINDOWS;
		}
		else if(osString.contains("nix") || osString.contains("nux"))
		{
			return OS.UNIX;
		}
		else if(osString.contains("mac"))
		{
			return OS.MACOSX;
		}
		else
		{
			return OS.OTHER;
		}
	}

	public static boolean is64BitWindows()
	{
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
		return (arch.endsWith("64") || (wow64Arch != null && wow64Arch.endsWith("64")));
	}

	public static String getJavaDelimiter()
	{
		switch(getCurrentOS())
		{
		case WINDOWS:
			return ";";
		case UNIX:
			return ":";
		case MACOSX:
			return ":";
		default:
			return ";";
		}
	}
}
