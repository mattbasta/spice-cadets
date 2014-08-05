package game.level;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;

public class TexturedLevelTerrain extends LevelTerrain {
	
	private final float[][] terrainHeight;
	
	public TexturedLevelTerrain(String region, int x, int y) {
		super(region, x, y);
		
		final int height = LevelManager.chunkHeight,
			      width = LevelManager.chunkWidth;
		
		// TODO: Replace this with a file loader.
		
		final int hp1 = height + 1, wp1 = width + 1;
		terrainHeight = new float[hp1][wp1];
		for(int i = 0; i < hp1 * wp1; i++)
			terrainHeight[i / hp1][i % wp1] = (float)(Math.sin((i + i / hp1) % wp1) + 1);
		
	}
	
	@Override
	protected void doRender() {
		GL11.glBegin(GL11.GL_QUADS);
		
		for(int i = 0; i < LevelManager.chunkHeight; i++) {
			for(int j = 0; j < LevelManager.chunkWidth; j++) {
				setupTileMaterial(terrainTextures[i][j]);
				
				// Figure out the normal of the terrain surface so we can do
				// lighting.
				Vector3f normal = new Vector3f(),
						 bottomLeft = new Vector3f(0.0f, terrainHeight[i + 1][j] - terrainHeight[i][j], 1.0f),
						 topRight = new Vector3f(1.0f, terrainHeight[i][j + 1] - terrainHeight[i][j], 0.0f);
				Vector3f.cross(bottomLeft, topRight, normal);
				normal.normalise();
				GL11.glNormal3f(normal.x, normal.y, normal.z);
				
				GL11.glVertex3f((float)j, terrainHeight[i][j], (float)i);
				GL11.glVertex3f((float)(j + 1), terrainHeight[i][j + 1], (float)i);
				GL11.glVertex3f((float)(j + 1), terrainHeight[i + 1][j + 1], (float)(i + 1));
				GL11.glVertex3f((float)j, terrainHeight[i + 1][j], (float)(i + 1));
			}
		}
		GL11.glEnd();
	}
	
	@Override
	public double heightAtPosition(double x, double y) {
		x %= LevelManager.chunkWidth;
		y %= LevelManager.chunkHeight;
		
		final int xf = (int) Math.floor(x),
			      xc = (int) Math.ceil(x);
		final double xperc = x - xf;
		final int yf = (int) Math.floor(y),
			      yc = (int) Math.ceil(y);
		final double yperc = y - yf;
		
		final double left = terrainHeight[yf][xf] + (terrainHeight[yf][xc] - terrainHeight[yf][xf]) * xperc,
			         right = terrainHeight[yc][xf] + (terrainHeight[yc][xc] - terrainHeight[yc][xf]) * xperc;
		return left + (right - left) * yperc;
	}

	@Override
	public CollisionShape getGroundShape() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long shapeHashCode() {
		return hashCode();
	}

}
