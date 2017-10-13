package grunt;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;

import grunt.ClientAuthentication.AuthenticationResponse;
import grunt.json.F;
import grunt.json.JSONArray;
import grunt.json.JSONObject;
import grunt.ui.ProgressGameCrash;
import grunt.ui.ProgressLogin;
import grunt.ui.ProgressRunning;
import grunt.ui.ProgressStart;
import grunt.util.DLQ;
import grunt.util.GList;
import grunt.util.GMap;
import grunt.util.JavaFinder;
import grunt.util.JavaInfo;
import grunt.util.OSF;
import grunt.util.OSF.OS;
import grunt.util.OldPropertyMapSerializer;

public class Client
{
	private File fbase;
	private File fasm;
	private File fassets;
	private File fbin;
	private File fhome;
	private File fnatives;
	private File flibs;
	private File fobjects;
	private File fauth;
	private File fgame;
	private ClientAuthentication auth;
	private ProgressLogin login;
	public static ProgressStart ps;
	public static ProgressRunning ru;
	public static double x = 0;
	public static double y = 0;
	private DLQ q;
	private static GMap<String, String> artifactRemapping;
	public static GList<String> lines = new GList<String>();

	public Client()
	{
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		x = (int) ((dimension.getWidth() - 320) / 2);
		y = (int) ((dimension.getHeight() - 303) / 2);
		fbase = new File(new File(Grunt.localfs ? "." : System.getProperty("user.home")), "Oberon");
		fasm = new File(fbase, "client");
		fauth = new File(fasm, "auth.ksg");
		fgame = new File(fbase, "game");
		fassets = new File(fgame, "assets");
		fobjects = new File(fassets, "objects");
		fbin = new File(fbase, "bin");
		fnatives = new File(fbin, "natives");
		flibs = new File(fbin, "libs");
		q = new DLQ(1);
		fobjects.mkdirs();
		fasm.mkdirs();
		fnatives.mkdirs();
		flibs.mkdirs();
		fgame.mkdirs();
		artifactRemapping = buildArtifactRemapping(new GMap<String, String>());
	}

	private GMap<String, String> buildArtifactRemapping(GMap<String, String> map)
	{
		map.put("net.minecraftforge:forge:1.7.10-10.13.4.1614-1.7.10", "http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/forge-1.7.10-10.13.4.1614-1.7.10-universal.jar");
		map.put("org.scala-lang:scala-xml_2.11:1.0.2", "http://central.maven.org/maven2/org/scala-lang/modules/scala-xml_2.11/1.0.2/scala-xml_2.11-1.0.2.jar");
		map.put("org.scala-lang:scala-swing_2.11:1.0.1", "http://central.maven.org/maven2/org/scala-lang/modules/scala-swing_2.11/1.0.1/scala-swing_2.11-1.0.1.jar");
		map.put("org.scala-lang:scala-parser-combinators_2.11:1.0.1", "http://central.maven.org/maven2/org/scala-lang/modules/scala-parser-combinators_2.11/1.0.1/scala-parser-combinators_2.11-1.0.1.jar");

		return map;
	}

	public void logIn()
	{
		auth = new ClientAuthentication(fauth);

		if(auth.authenticate().equals(AuthenticationResponse.SUCCESS))
		{
			onLoggedIn();
		}

		else
		{
			loginWithPassword();
		}
	}

	private void onLoggedIn()
	{
		System.out.println("Logged In as " + auth.getProfileName());

		try
		{
			downloadGame();
			int code = launchGame();
			System.out.println("Process exited with error code " + code);

			if(code != 0)
			{
				ProgressGameCrash c = new ProgressGameCrash();
				c.setVisible(true);
			}

			else
			{
				System.exit(0);
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	private int launchGame() throws IOException, InterruptedException
	{
		System.out.println("Launching Game....");
		ru = new ProgressRunning();
		ru.setVisible(true);

		File main = fgame;
		File libs = flibs;
		File natives = fnatives;
		String mainClassForge = "net.minecraft.launchwrapper.Launch";
		String properties = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new OldPropertyMapSerializer()).create().toJson(auth.getProfileSettings());
		List<File> classpath = getLibClasspath(libs);
		GList<String> arguments = new GList<String>();

		main.mkdirs();
		arguments.add(getDefaultJavaPath());
		arguments.add("-Xmx3G");
		arguments.add("-Xms1M");
		arguments.add("-XX:+UseConcMarkSweepGC");
		arguments.add("-XX:+CMSIncrementalMode");
		arguments.add("-XX:-UseAdaptiveSizePolicy");
		arguments.add("-Djava.library.path=" + natives.getAbsolutePath());
		arguments.add("-Dorg.lwjgl.librarypath=" + natives.getAbsolutePath());
		arguments.add("-Dnet.java.games.input.librarypath=" + natives.getAbsolutePath());
		arguments.add("-Duser.home=" + main.getParentFile().getAbsolutePath());
		arguments.add("-Duser.language=en");

		if(OSF.getCurrentOS() == OS.WINDOWS)
		{
			arguments.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		}

		arguments.add("-Djava.net.preferIPv4Stack=true");
		arguments.add("-Djava.net.useSystemProxies=true");
		arguments.add("-cp");

		StringBuilder cpb = new StringBuilder("");

		for(File f : classpath)
		{
			cpb.append(OSF.getJavaDelimiter());
			cpb.append(f.getAbsolutePath());
		}

		cpb.deleteCharAt(0);
		arguments.add(cpb.toString());
		arguments.add("-XX:+UseParNewGC");
		arguments.add("-XX:+UseConcMarkSweepGC");
		arguments.add("-XX:+CICompilerCountPerCPU");
		arguments.add("-XX:+TieredCompilation");
		arguments.add(mainClassForge);
		arguments.add("--username");
		arguments.add(auth.getProfileName());
		arguments.add("--version");
		arguments.add("1.7.10");
		arguments.add("--gameDir");
		arguments.add(main.getAbsolutePath());
		arguments.add("--assetsDir");
		arguments.add("/assets");
		arguments.add("--assetsIndex");
		arguments.add("/assets/asset-index.json");
		arguments.add("--uuid");
		arguments.add(auth.getUuid());
		arguments.add("--accessToken");
		arguments.add(auth.getToken());
		arguments.add("--userProperties");
		arguments.add(properties);
		arguments.add("--userType");
		arguments.add(auth.getProfileType());
		arguments.add("--tweakClass");
		arguments.add("cpw.mods.fml.common.launcher.FMLTweaker");

		System.out.println("======================================================================");

		for(String i : arguments)
		{
			if(i.contains(";"))
			{
				for(String j : i.split(";"))
				{
					System.out.println(";;" + j + ";;");
				}
			}

			else
			{
				System.out.println(i);
			}
		}

		System.out.println("======================================================================");

		ProcessBuilder builder = new ProcessBuilder(arguments);

		builder.directory(main);
		System.out.println("==========================================================================");
		System.out.println("Launching Client!");

		Process process = builder.start();
		BufferedReader bu = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		int count = 0;
		int vec = 181;

		while((line = bu.readLine()) != null)
		{
			if(line.contains("LWJGL Version: 2.9.1"))
			{
				System.out.println("[GRUNT]: CLOSE UI");
				System.out.println("COUNT: " + count);
				ru.setVisible(false);
			}

			String km = "";

			if(line.contains(":"))
			{
				km = line.split(":")[line.split(":").length - 1];
			}

			else
			{
				km = line;
			}

			ProgressRunning.lblLog.setText(km);
			System.out.println("[CLIENT]: " + km);
			ProgressRunning.panel.setProgress((int) (((double) count / (double) vec) * 100));
			ProgressRunning.label.setText(F.pc(((double) count / (double) vec), 0));
			count++;
		}

		int code = process.waitFor();

		return code;
	}

	private List<File> getLibClasspath(File libs)
	{
		List<File> classpath = Lists.newArrayList();

		System.out.println("Building Classpath");

		for(File i : getFiles(libs))
		{
			classpath.add(new File(i.getPath()));
			System.out.println("Adding " + i.getName() + " to classpath");
		}

		classpath.add(new File(fbin, "client.jar"));

		return classpath;
	}

	private static List<File> getFiles(File folder)
	{
		List<File> files = new ArrayList<File>();

		for(File i : folder.listFiles())
		{
			if(i.isFile())
			{
				if(i.getName().equals("guava-15.0.jar"))
				{
					continue;
				}

				System.out.println("Reading Library: " + i.getAbsolutePath());
				files.add(i);
			}

			else
			{
				files.addAll(getFiles(i));
			}
		}

		return files;
	}

	private static String getDefaultJavaPath()
	{
		JavaInfo javaVersion;

		if(OSF.getCurrentOS() == OS.MACOSX)
		{
			javaVersion = JavaFinder.parseJavaVersion();

			if(javaVersion != null && javaVersion.path != null)
			{
				return javaVersion.path;
			}
		}

		else if(OSF.getCurrentOS() == OS.WINDOWS)
		{
			javaVersion = JavaFinder.parseJavaVersion();

			if(javaVersion != null && javaVersion.path != null)
			{
				return javaVersion.path.replace(".exe", "w.exe");
			}
		}

		return System.getProperty("java.home") + "/bin/java";
	}

	private void downloadGame() throws IOException
	{
		File vm = new File(fasm, "version-manifest.json");
		File vmv = new File(fasm, "manifest.json");
		File iid = new File(fassets, "asset-index.json");
		File cli = new File(fbin, "client.jar");
		File rxm = new File(fasm, "version.json");
		File cms = new File(fasm, "cms.gct");
		String dlx = URLX.CMS;
		ps = new ProgressStart();
		q.q(URLX.VERSION_META, vm);

		if(rxm.exists())
		{
			rxm.delete();
		}

		q.q(URLX.RDM, rxm);
		q.flush();
		JSONObject vrs = readJSON(rxm);
		dlx = dlx.replace("%v%", vrs.getString("version"));
		JSONObject jvm = readJSON(vm);
		writeJSON(jvm, vm);
		JSONArray ja = jvm.getJSONArray("versions");
		JSONObject m = null;

		for(int i = 0; i < ja.length(); i++)
		{
			JSONObject j = ja.getJSONObject(i);

			if(j.getString("id").equals("1.7.10") && j.getString("type").equals("release"))
			{
				m = j;
				break;
			}
		}

		q.q(m.getString("url"), vmv);
		q.flush();

		JSONObject manifest = readJSON(vmv);
		JSONObject assetIndex = manifest.getJSONObject("assetIndex");
		JSONObject downloads = manifest.getJSONObject("downloads");
		JSONObject client = downloads.getJSONObject("client");
		q.q(assetIndex.getString("url"), iid);
		q.flush();
		long sd = Long.valueOf(vrs.getString("size"));
		boolean extract = false;

		if(!cms.exists() || cms.length() != sd)
		{
			cms.delete();
			System.out.println("Game Content out of date or missing. Downloading.");
			q.q(dlx, cms, sd);
			extract = true;
		}

		JSONObject assets = readJSON(iid);
		JSONArray libraries = manifest.getJSONArray("libraries");
		JSONObject objects = assets.getJSONObject("objects");
		Iterator<String> itObject = objects.keys();

		q.q(client.getString("url"), cli, client.getLong("size"));

		while(itObject.hasNext())
		{
			String key = itObject.next();
			JSONObject asset = objects.getJSONObject(key);
			long size = asset.getLong("size");
			String hash = asset.getString("hash");
			String hashRoot = hash.substring(0, 2);
			File fr = new File(fgame, "resourcepacks");
			File fx = new File(fr, "main");
			File f = new File(fx, key);
			File d = new File(fobjects, hashRoot);

			if(key.endsWith(".ogg"))
			{
				continue;
			}

			if(key.endsWith(".png"))
			{
				continue;
			}

			if(key.startsWith("minecraft"))
			{
				f = new File(fx, "assets/" + key);
			}

			f.getParentFile().mkdirs();
			File k = f;
			q.q("http://resources.download.minecraft.net/" + hashRoot + "/" + hash, f, size, new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						System.out.println("Copy asset: " + k + " > " + d);
						d.mkdirs();
						Files.copy(k, new File(d, hash));
					}

					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			});
		}

		for(int i = 0; i < libraries.length(); i++)
		{
			JSONObject lib = libraries.getJSONObject(i);
			JSONObject libDownloads = lib.getJSONObject("downloads");

			if(libDownloads.has("artifact"))
			{
				String name = lib.getString("name").replaceAll(":", "-") + ".jar";
				System.out.println(name);
				JSONObject artifact = libDownloads.getJSONObject("artifact");
				String path = artifact.getString("path");
				File f = new File(flibs, path);
				f.getParentFile().mkdirs();
				q.q(artifact.getString("url"), f, artifact.getLong("size"));
			}

			else if(lib.has("natives"))
			{
				JSONObject nativeset = lib.getJSONObject("natives");
				String raw = OSF.rawOS();

				if(nativeset.has(raw))
				{
					String classifier = nativeset.getString(raw).replace("${arch}", OSF.is64BitWindows() ? "64" : "32");
					JSONObject classifiers = libDownloads.getJSONObject("classifiers");

					if(classifiers.has(classifier))
					{
						JSONObject cl = classifiers.getJSONObject(classifier);
						String path = cl.getString("path");
						File f = new File(flibs, path);
						q.q(cl.getString("url"), f, cl.getLong("size"), new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									extractNatives(f, fnatives);
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
							}
						});
					}

					else
					{
						System.out.println("No classifier for " + classifier);
					}
				}

				else
				{
					System.out.println("WARNING No classifier RAW for " + raw);
				}
			}
		}

		JSONObject forge = new JSONObject(URLX.IFORGE);
		JSONArray ilibs = forge.getJSONArray("libraries");

		for(int i = 0; i < ilibs.length(); i++)
		{
			JSONObject ilib = ilibs.getJSONObject(i);

			if(ilib.has("url"))
			{
				String name = ilib.getString("name");
				String curl = "http://central.maven.org/maven2/";

				Artifact a = new Artifact(name, curl);
				String kurl = a.getFormalUrl();

				if(artifactRemapping.containsKey(name))
				{
					System.out.println("Remapping artifact coordinates: " + name + " to \n" + artifactRemapping.get(name));
					kurl = artifactRemapping.get(name);
				}

				else
				{
					System.out.println("Mapping artifact coordinates: " + name + " to \n" + kurl);
				}

				a.getPath(flibs).getParentFile().mkdirs();
				q.q(kurl, a.getPath(flibs));
			}

			else
			{
				String name = ilib.getString("name");
				String curl = "https://libraries.minecraft.net/";
				Artifact a = new Artifact(name, curl);
				String kurl = a.getFormalUrl();

				if(artifactRemapping.containsKey(name))
				{
					System.out.println("Remapping artifact coordinates: " + name + " to \n" + artifactRemapping.get(name));
					kurl = artifactRemapping.get(name);
				}

				else
				{
					System.out.println("Mapping artifact coordinates: " + name + " to \n" + kurl);
				}

				a.getPath(flibs).getParentFile().mkdirs();
				q.q(kurl, a.getPath(flibs));
			}
		}

		q.flush();
		inject(cms, fgame, extract);
		cleanup();
		hackOptions();
		System.out.println("Game Downloaded");
		ps.setVisible(false);
		x = ps.getLocation().getX();
		y = ps.getLocation().getY();
	}

	private void inject(File zip, File dir, boolean ex)
	{
		if(ex)
		{
			dir.mkdirs();
			System.out.println("Game Extract Requested");

			try
			{
				extractAll(zip, dir);
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void hackOptions() throws IOException
	{
		File options = new File(fgame, "options.txt");
		File optionsHack = new File(fgame, "options-hack.txt");

		if(options.exists())
		{
			BufferedReader bu = new BufferedReader(new FileReader(options));
			PrintWriter pw = new PrintWriter(new FileWriter(optionsHack));

			String line;

			while((line = bu.readLine()) != null)
			{
				if(line.startsWith("resourcePacks:["))
				{
					pw.println("resourcePacks:[\"main\",\"pack\"]");
				}

				else
				{
					pw.println(line);
				}
			}

			bu.close();
			pw.close();
			options.delete();
			Files.copy(optionsHack, options);
			optionsHack.delete();
		}

		if(!options.exists())
		{
			options.createNewFile();
			PrintWriter pw = new PrintWriter(new FileWriter(options));
			pw.println("resourcePacks:[\"main\",\"pack\"]");
			pw.close();
		}
	}

	private void cleanup()
	{
		try
		{
			for(File i : fnatives.listFiles())
			{
				if(i.getName().equals("META-INF") && i.isDirectory())
				{
					for(File j : i.listFiles())
					{
						j.delete();
					}

					i.delete();
				}

				else if(i.getName().equals("META-INF") && i.isFile())
				{
					i.delete();
				}
			}
		}

		catch(Exception e)
		{

		}
	}

	private void loginWithPassword()
	{
		boolean[] fk = new boolean[] {false};

		login = new ProgressLogin()
		{
			private static final long serialVersionUID = -5980962525568940052L;

			@Override
			public boolean onSubmit(String username, String password)
			{
				auth = new ClientAuthentication(fauth, username, password);
				AuthenticationResponse r = auth.authenticate();

				if(r.equals(AuthenticationResponse.SUCCESS))
				{
					fk[0] = true;
					login.setVisible(false);
					Client.x = login.getLocation().getX();
					Client.y = login.getLocation().getY();
				}

				return r.equals(AuthenticationResponse.SUCCESS);
			}
		};

		login.setVisible(true);

		while(!fk[0])
		{
			try
			{
				Thread.sleep(100);
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		onLoggedIn();
	}

	private static void writeJSON(JSONObject o, File f) throws IOException
	{
		FileWriter fw = new FileWriter(f);
		PrintWriter pw = new PrintWriter(fw);

		pw.println(o.toString());

		pw.close();
	}

	private static JSONObject readJSON(File man) throws IOException
	{
		FileReader fr = new FileReader(man);
		BufferedReader bu = new BufferedReader(fr);

		String line;
		String content = "";

		while((line = bu.readLine()) != null)
		{
			content += line;
		}

		bu.close();

		return new JSONObject(content);
	}

	private static void extractNatives(File zip, File lfolder) throws IOException
	{
		final ZipFile file = new ZipFile(zip);

		try
		{
			final Enumeration<? extends ZipEntry> entries = file.entries();

			while(entries.hasMoreElements())
			{
				final ZipEntry entry = entries.nextElement();

				if(entry.getName().contains("META") || entry.getName().contains("MANIFEST"))
				{
					continue;
				}

				writeStream(entry.getName(), file.getInputStream(entry), lfolder, zip);
			}
		}

		finally
		{
			file.close();
		}
	}

	private static void extractAll(File zip, File lfolder) throws IOException
	{
		final ZipFile file = new ZipFile(zip);

		try
		{
			final Enumeration<? extends ZipEntry> entries = file.entries();

			while(entries.hasMoreElements())
			{
				final ZipEntry entry = entries.nextElement();

				if(entry.isDirectory())
				{
					new File(lfolder, entry.getName()).mkdirs();
					continue;
				}

				writeStream(entry.getName(), file.getInputStream(entry), lfolder, zip);
			}
		}

		finally
		{
			file.close();
		}
	}

	private static int writeStream(String n, final InputStream is, File lfolder, File zip) throws IOException
	{
		lfolder.mkdirs();
		FileOutputStream fos = new FileOutputStream(new File(lfolder, n));
		final byte[] buf = new byte[8192];
		int read = 0;
		int cntRead;

		while((cntRead = is.read(buf, 0, buf.length)) >= 0)
		{
			read += cntRead;
			fos.write(buf, 0, cntRead);
		}

		System.out.println("Extract: " + n + " from " + zip.getName());

		fos.close();

		return read;
	}

	public File getFbase()
	{
		return fbase;
	}

	public File getFasm()
	{
		return fasm;
	}

	public File getFassets()
	{
		return fassets;
	}

	public File getFbin()
	{
		return fbin;
	}

	public File getFhome()
	{
		return fhome;
	}

	public File getFnatives()
	{
		return fnatives;
	}

	public File getFlibs()
	{
		return flibs;
	}

	public File getFobjects()
	{
		return fobjects;
	}

	public File getFauth()
	{
		return fauth;
	}

	public ClientAuthentication getAuth()
	{
		return auth;
	}

	public DLQ getQ()
	{
		return q;
	}
}
