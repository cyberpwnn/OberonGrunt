package grunt.ui;

import java.awt.Color;

public class UX
{
	public static Color c = Color.ORANGE;

	public static void c(Color hsbColor)
	{
		int r = hsbColor.getRed();
		int g = hsbColor.getGreen();
		int b = hsbColor.getBlue();

		if(r < 200)
		{
			r += 50;
		}

		if(g < 200)
		{
			g += 50;
		}

		if(b < 200)
		{
			b += 50;
		}

		c = new Color(r, g, b).brighter();
	}
}
