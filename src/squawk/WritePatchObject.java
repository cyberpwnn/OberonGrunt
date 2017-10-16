package squawk;

public class WritePatchObject extends PatchObject
{
	public WritePatchObject(String target)
	{
		super(PatchMode.WRITE, target);
	}
}
