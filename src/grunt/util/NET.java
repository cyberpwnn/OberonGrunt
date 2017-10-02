package grunt.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NET
{
	public static int getFileSize(URL url)
	{
		HttpURLConnection conn = null;

		try
		{
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			return conn.getContentLength();
		}

		catch(IOException e)
		{
			return -1;
		}

		finally
		{
			conn.disconnect();
		}
	}
}
