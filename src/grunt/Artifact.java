package grunt;

import java.io.File;

public class Artifact
{
	private String groupId;
	private String artifactId;
	private String version;
	private String repo;

	public Artifact(String slug, String repo)
	{
		this.repo = repo;
		this.groupId = slug.split(":")[0];
		this.artifactId = slug.split(":")[1];
		this.version = slug.split(":")[2];
	}

	public File getPath(File f)
	{
		return new File(f, groupId.replaceAll("\\.", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar");
	}

	public String getFormalUrl()
	{
		return repo + groupId.replaceAll("\\.", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
	}

	@Override
	public String toString()
	{
		return groupId + ":" + artifactId + ":" + version + " @ " + getFormalUrl();
	}
}
