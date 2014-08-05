package game.models;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;


public class DrawableFont {
	
	private static final Map<String, TrueTypeFont> fontMap = new HashMap<String, TrueTypeFont>();
	private final String config;
	private final TrueTypeFont font;
	
	public DrawableFont(String assetPath) {
		this(assetPath, 3);
	}
	
	public DrawableFont(String assetPath, int fontSize) {
		config = assetPath + String.valueOf(fontSize);
		if(fontMap.containsKey(config)) {
			font = fontMap.get(config);
			return;
		}

		Font font = new Font("Courier New", Font.PLAIN, 1);
		//Font font = null;
		TrueTypeFont ttf = new TrueTypeFont(font, true);
		System.out.println(GL11.glGetError());
		// TrueTypeFont ttf = null;
		/*
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(assetPath));
			ttf = new TrueTypeFont(font.deriveFont(fontSize), true);
		} catch (Exception e) {
			e.printStackTrace();
			// We can't find the font to do anything with it. NOTHING TO SEE HERE.
			font = new Font("Courier New", Font.PLAIN, fontSize);
			//ttf = new TrueTypeFont(font, false);
			ttf = null;
		}
		*/
		fontMap.put(config, ttf);
		this.font = ttf;
	}

	public void draw(String text, int x, int y) {
		//font.drawString(x, y, text);
	}

	public void draw(String text, int x, int y, Color color) {
		final org.newdawn.slick.Color slickColor = new org.newdawn.slick.Color(
			(float)color.getRed() / 255f, (float)color.getGreen() / 255, (float)color.getBlue() / 255);
		//font.drawString(x, y, text, slickColor);
	}
	
}