package squawk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class AggressiveGZipOutputStream extends GZIPOutputStream
{
	public AggressiveGZipOutputStream(OutputStream out) throws IOException
	{
		super(out);

		def.setLevel(9);
	}
}
