package game.level;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.StaticPlaneShape;

import game.entities.EntityManager;
import game.models.IRenderable;

public class LevelManager implements IRenderable {

	public static final int chunkWidth = 50, chunkHeight = 50;
	public static final int focusExtraDistance = 100;
	
	private final List<LevelChunk> loadedChunks = new ArrayList<LevelChunk>();
	private final Map<Long, LevelChunk> chunkMap = new HashMap<Long, LevelChunk>();
	private final Set<Long> absentChunks = new HashSet<Long>();

	private final FloatBuffer sunlightDirection = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer sunlightColor = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer sunlightAmbience = BufferUtils.createFloatBuffer(4);

	private final FloatBuffer playerLightDirection = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer indoorsOverhead = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer indoorsIncandescent = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer indoorsFluorescent = BufferUtils.createFloatBuffer(4);
	
	private String currentRegion = "";
	private int focus_x = Integer.MIN_VALUE, focus_y = Integer.MIN_VALUE;
	private int zoomFocus = 0;
	
	private final EntityManager entityManager;
	
	private final StaticPlaneShape groundPlane;
	
	public enum LightingType {
		Outdoors,
		IndoorsBright, IndoorsDark, IndoorsGloomy,
		IndoorsWarm
	}
	
	public LightingType lighting = LightingType.Outdoors;
	
	public LevelManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		
		sunlightDirection.put(0.0f).put(-1.0f).put(0.0f).put(0.0f).flip();  // Down, directional
		sunlightColor.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();  // White
		sunlightAmbience.put(0.5f).put(0.5f).put(0.5f).put(0.5f).flip();  // White
		
		playerLightDirection.put(0.0f).put(-1.0f).put(0.0f).put(0.0f).flip();  // Down, positional
		indoorsOverhead.put(0.0f).put(-1.0f).put(0.0f).put(0.0f).flip();  // Down, directional
		indoorsIncandescent.put(0.8f).put(0.7f).put(0.6f).put(1.0f).flip();
		indoorsFluorescent.put(0.3f).put(0.325f).put(0.4f).put(1.0f).flip();
		
		// Define a flat plane that exists at y=0
		groundPlane = new StaticPlaneShape(new Vector3f(0.0f, 1.0f, 0.0f), 0);
		
	}
	
	/**
	 * Set the zoom so we can use it to increase the number of chunks to load.
	 * @param zoom The new zoom value.
	 */
	public void setZoom(int zoom) {
		zoomFocus = Math.max(zoom, 0);
	}
	
	/**
	 * Load a new region around the given coordinates. This function will unload
	 * all chunks currently in memory. If the region being requested is the same
	 * as the current region, this funtion is equivalent to calling
	 * `loadChunksAroundPoint`.
	 * 
	 * @param region The name of the region to load.
	 * @param x The X value of the position we're loading around.
	 * @param y The Y value of the position we're loading around.
	 */
	public void loadRegion(String region, int x, int y) {
		if(region == currentRegion) {
			loadChunksAroundPoint(x, y);
			return;
		}
		
		// Clear out existing chunks.
		unloadAllChunks();
		absentChunks.clear();
		
		currentRegion = region;
		
		// Load the new chunks
		loadChunksAroundPoint(x, y);
	}
	
	/**
	 * Load the chunks around the provided coordinates. The chunks required
	 * are automatically determined based on the zoom distance around the
	 * player. Chunks that are outside of the new "focused" area will
	 * automatically be unloaded.
	 * 
	 * @param x The X value of the position we're loading around.
	 * @param y The Y value of the position we're loading around.
	 */
	public void loadChunksAroundPoint(int x, int y) {
		if(focus_x == x && focus_y == y)
			return;
		
		focus_x = x;
		focus_y = y;
		unloadDistantChunks();
		
		int center_chunk_x = x - x % chunkWidth,
			center_chunk_y = y - y % chunkHeight,
			chunks_horiz = focusExtraDistance / chunkWidth,
			chunks_vert = focusExtraDistance / chunkHeight;
		for(int i = center_chunk_x - (chunks_horiz * chunkWidth);
			i < center_chunk_x + (chunks_horiz * chunkWidth);
			i += chunkWidth) {
			
			for(int j = center_chunk_y - (chunks_vert * chunkHeight);
				j < center_chunk_y + (chunks_vert * chunkHeight);
				j += chunkHeight) {
				
				loadChunk(i, j);
			}
		}
	}
	
	/**
	 * Loads a single chunk into memory.
	 * 
	 * @param x The X coordinate of the chunk being loaded.
	 * @param y The Y coordinate of the chunk being loaded.
	 */
	private void loadChunk(int x, int y) {
		// Test that the chunk isn't already loaded.
		final Long hash = Long.valueOf(LevelChunk.chunkHash(x, y));
		if(absentChunks.contains(hash))
			return;
		if(chunkMap.containsKey(hash))
			return;
		
		// If the chunk is outside of the loadable range of chunks, put it in
		// the list of unloadable chunks and 
		if(x < -1000 || x > 1000 || y < -1000 || y > 1000) {
			absentChunks.add(hash);
			return;
		}
		
		LevelChunk lc = new LevelChunk(this, currentRegion, x, y);
		chunkMap.put(hash, lc);
		loadedChunks.add(lc);
		
		// Have that bad boy try to compile his graphics.
		lc.tryPrerender();
		
		lc.spawnEntities(entityManager);
	}
	
	/**
	 * Unload a chunk instance.
	 * @param chunk The chunk instance to unload.
	 */
	private void unloadChunk(LevelChunk chunk) {
		unloadChunk(LevelChunk.chunkHash(chunk.x, chunk.y), chunk);
	}
	/**
	 * Unload a chunk instance with a particular hash value. This version of the
	 * `unloadChunk` method is more efficient because it skips the hash
	 * generation process.
	 * 
	 * @param hash	The hash of the chunk instance.
	 * @param chunk	The chunk instance to unload.
	 */
	private void unloadChunk(long hash, LevelChunk chunk) {
		chunkMap.remove(Long.valueOf(hash));
		chunk.discard();
	}
	
	/**
	 * When called, this function will unload all chunks that are outside the
	 * current "focus" range.
	 */
	private void unloadDistantChunks() {
		final Iterator<LevelChunk> i = loadedChunks.iterator();
		while(i.hasNext()) {
			LevelChunk lc = i.next();
			if(lc.x < focus_x - focusExtraDistance - chunkWidth ||
			   lc.x > focus_x + focusExtraDistance ||
			   lc.y < focus_y - focusExtraDistance - chunkHeight ||
			   lc.y > focus_y + focusExtraDistance) {
				i.remove();
				unloadChunk(lc);
			}
		}
	}
	
	/**
	 * This will unload all loaded chunks.
	 */
	private void unloadAllChunks() {
		while(!loadedChunks.isEmpty()) {
			LevelChunk lc = loadedChunks.remove(0);
			unloadChunk(lc);
		}
	}

	@Override
	public void tryPrerender() {}

	@Override
	public void render() {
		final int size = loadedChunks.size();
		for(int i = 0; i < size; i++) {
			LevelChunk lc = loadedChunks.get(i);
			GL11.glPushMatrix();
			GL11.glTranslatef(lc.x, 0, lc.y);
			lc.render();
			GL11.glPopMatrix();
		}
	}

	@Override
	public void discard() {
		unloadAllChunks();
	}
	
	/**
	 * Set up all lighting for the current region.
	 */
	public void setLighting() {
		
		switch(lighting) {
		case Outdoors:
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, sunlightDirection);
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, sunlightColor);
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, sunlightAmbience);
			
			GL11.glDisable(GL11.GL_LIGHT1);
			break;
		case IndoorsBright:
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, playerLightDirection);
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, indoorsIncandescent);
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, indoorsFluorescent);
			
			GL11.glEnable(GL11.GL_LIGHT1);
			GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, indoorsOverhead);
			GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, indoorsFluorescent);
			
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * Return the terrain height for a particular coordinate within the current
	 * region. This will only return a value for loaded chunks.
	 * 
	 * @param x	The X coordinate to query the height for.
	 * @param y	The Y coordinate to query the height for.
	 * @return	The height of the terrain at the given coordinate pair.
	 */
	public double getHeightAtPoint(double x, double y) {
		final int c_x = (int) (Math.floor(x / chunkWidth) * chunkWidth),
		          c_y = (int) (Math.floor(y / chunkHeight) * chunkHeight);
		
		final Long chunk_hash = Long.valueOf(LevelChunk.chunkHash(c_x, c_y));
		if(!chunkMap.containsKey(chunk_hash))
			return 0;
		
		return chunkMap.get(chunk_hash).terrain.heightAtPosition(x - c_x, y - c_y);
	}

}
