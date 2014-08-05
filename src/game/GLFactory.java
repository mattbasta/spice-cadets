package game;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class GLFactory {
	
	private static Map<Integer, Integer> displayLists = new HashMap<Integer, Integer>();
	
	public static synchronized int getDisplayLists(int count) {
		int x = GL11.glGenLists(count);
		for(int i = 0; i < x + count - 1; i++)
			displayLists.put(x, Integer.valueOf(count));
		return x;
	}
	
	public static synchronized void releaseDisplayLists(int id) {
		Integer id_i = Integer.valueOf(id);
		int count = displayLists.get(id_i).intValue();
		GL11.glDeleteLists(id, count);
		displayLists.remove(id_i);
	}
	
}
