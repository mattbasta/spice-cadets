package game.level;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.CollisionShape;

import game.models.FixedShape;

public abstract class LevelTerrain extends FixedShape {
	
	protected final int[][] terrainTextures;
	protected final FloatBuffer terrainMaterial = BufferUtils.createFloatBuffer(4);
	
	public LevelTerrain(String region, int x, int y) {
		// Set up the material buffer.
		terrainMaterial.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		
		this.makeDisplayList = true;  // Cache the rendering for fast good times.
		
		final int height = LevelManager.chunkHeight,
			      width = LevelManager.chunkWidth;
		
		// TODO: Replace this with a file loader.
		
		terrainTextures = new int[height][width];
		final Random rng = new Random();
		for(int i = 0; i < height * width; i++)
			terrainTextures[i / height][i % width] = rng.nextInt(5);
		
	}
	
	protected void setupTileMaterial(int texture) {
		// Set a color for now; based on the random texture.
		switch(texture) {
		case 0:
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
			break;
		case 1:
			GL11.glColor3f(0.6f, 0.7f, 0.5f);
			break;
		case 2:
			GL11.glColor3f(0.7f, 0.7f, 0.7f);
			break;
		case 3:
			GL11.glColor3f(0.4f, 0.4f, 0.4f);
			break;
		case 4:
			GL11.glColor3f(0.3f, 0.3f, 0.3f);
			break;
		}
	}
	
	/**
	 * Returns the height of the terrain at the provided coordinate. If the
	 * coordinate is a decimal value, the terrain height will be interpolated
	 * from surrounding values.
	 * 
	 * @param x The X value of the coordinate to query.
	 * @param y The Y value of the coordinate to query.
	 * @return The height of the terrain at coordinate (x, y).
	 */
	public abstract double heightAtPosition(double x, double y);
	
	/**
	 * Gets the collision shape of the ground if available. If the ground is
	 * flat, simply returns `null`.
	 * @return The collision shape for the ground in this chunk, else `null`
	 */
	public abstract CollisionShape getGroundShape();

}
