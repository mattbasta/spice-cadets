package game.level;

import java.util.Random;

import game.entities.EntityManager;
import game.entities.NPC;
import game.entities.Person;
import game.models.IRenderable;

public class LevelChunk implements IRenderable {

	private final LevelManager levelManager;
	public final LevelTerrain terrain;
	
	public final String region;
	public final int x, y;
	private final long hash;
	
	private final Random rng = new Random();
	
	public LevelChunk(LevelManager levelManager, String region, int x, int y) {
		this.levelManager = levelManager;
		terrain = new FlatLevelTerrain(region, x, y);
		
		this.region = region;
		this.x = x;
		this.y = y;
		hash = LevelChunk.chunkHash(x, y);
	}

	@Override
	public void tryPrerender() {terrain.tryPrerender();}

	@Override
	public void render() {terrain.render();}

	@Override
	public void discard() {terrain.discard();}
	
	@Override
	public int hashCode() {
		long hash = chunkHash(x, y);
		return ((int)hash) ^ ((int)(hash >> 32));
	}
	
	public static long chunkHash(int x, int y) {
		// Hash function for the coordinates. Returns a single value for any
		// given chunk coordinates.
		return ((long)x) & 0xffffffffL | (((long)y) & 0xffffffffL) << 32;
	}
	
	public void spawnEntities(EntityManager entityManager) {
		// Set the seed for entity spawning.
		rng.setSeed(hash);
		
		final int entitiesToSpawn = rng.nextInt(4);
		for(int i = 0; i < entitiesToSpawn; i++) {
			Person p = new NPC(levelManager, entityManager);
			p.x = x + rng.nextDouble() * LevelManager.chunkWidth;
			p.y = y + rng.nextDouble() * LevelManager.chunkHeight;
			p.rotation = rng.nextDouble() * 360;
		}
	}

}
