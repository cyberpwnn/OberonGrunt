package squawk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import squawk.util.F;

public class Patch
{
	private List<PatchedObject> patches;
	private int version;
	private File base;

	public Patch(int version, File base)
	{
		this.base = base;
		this.version = version;
		patches = new ArrayList<PatchedObject>();
	}

	public void buildPatch(File file) throws IOException
	{
		System.out.println("======================================================");
		System.out.println("Writing Patch #" + version);
		System.out.println("Root Directory: " + base.getAbsolutePath());
		System.out.println("Patch File: " + file.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.writeInt(version);
		dos.writeInt(patches.size());

		System.out.println("Writing " + patches.size() + " objects");

		for(PatchedObject i : patches)
		{
			File f = new File(base, i.getTarget());
			dos.writeInt(i.getMode().ordinal());
			dos.writeUTF(i.getTarget());

			if(i.getMode().equals(PatchMode.WRITE))
			{
				dos.writeLong(f.length());
				System.out.println(" --> " + i.getMode() + " " + i.getTarget() + " (" + F.fileSize(f.length()) + ")");

				byte[] buffer = new byte[13370];
				FileInputStream fin = new FileInputStream(f);
				int len;

				while((len = fin.read(buffer)) != -1)
				{
					dos.write(buffer, 0, len);
				}

				fin.close();
			}

			else
			{
				System.out.println(" --> " + i.getMode() + " " + i.getTarget());
			}
		}

		dos.close();
		System.out.println("Wrote " + file.getName() + " (" + F.fileSize(file.length()) + ")");
		System.out.println("======================================================");
	}

	public void applyPatch(File file) throws IOException
	{
		System.out.println("======================================================");
		FileInputStream fin = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fin);

		version = dis.readInt();
		int patches = dis.readInt();

		System.out.println("Applying Patch #" + version);
		System.out.println("Applying " + patches + " patches");

		for(int i = 0; i < patches; i++)
		{
			PatchMode mode = PatchMode.values()[dis.readInt()];
			String target = dis.readUTF();
			long size = -1;

			if(mode.equals(PatchMode.WRITE))
			{
				size = dis.readLong();
				byte[] buffer = new byte[13370];
				int read;
				File f = new File(base, target);
				f.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(f);

				while(size > 0)
				{
					read = (int) (size > 13370 ? 13370 : size);
					dis.read(buffer, 0, read);
					fos.write(buffer, 0, read);
					size -= read;
				}

				fos.close();
				System.out.println(" <-> " + mode + " " + target + " (" + F.fileSize(size) + ")");
			}

			else if(mode.equals(PatchMode.DELETE))
			{
				new File(base, target).delete();

				if(!new File(base, target).getParentFile().equals(base))
				{
					if(new File(base, target).getParentFile().listFiles().length == 0)
					{
						new File(base, target).getParentFile().delete();
					}
				}

				System.out.println(" <-> " + mode + " " + target);
			}
		}

		dis.close();
		System.out.println("Successfully applied " + patches + " patches");
		System.out.println("======================================================");
	}

	public List<PatchedObject> getPatches()
	{
		return patches;
	}

	public int getVersion()
	{
		return version;
	}

	public File getBase()
	{
		return base;
	}

	public void setBase(File base)
	{
		this.base = base;
	}
}
