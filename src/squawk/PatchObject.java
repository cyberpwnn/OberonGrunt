package squawk;

public class PatchObject implements PatchedObject
{
	private PatchMode mode;
	private String target;

	public PatchObject(PatchMode mode, String target)
	{
		this.mode = mode;
		this.target = target;
	}

	public PatchMode getMode()
	{
		return mode;
	}

	public String getTarget()
	{
		return target;
	}
}
