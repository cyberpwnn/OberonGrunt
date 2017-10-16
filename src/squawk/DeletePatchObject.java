package squawk;

public class DeletePatchObject extends PatchObject
{
	public DeletePatchObject(String target)
	{
		super(PatchMode.DELETE, target);
	}
}
