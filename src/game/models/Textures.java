package game.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Textures {
	private static final Map<String, Texture> textures = new HashMap<String, Texture>();
	
	public static Texture getTexture(String name) {
		if(textures.containsKey(name))
			return textures.get(name);
		
		try {
			final Texture t = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(getPath(name)));
			textures.put(name, t);
			return t;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getPath(String name) {
		return "assets/" + name + ".png";
	}
	
}
