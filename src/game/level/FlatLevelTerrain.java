package game.level;

import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.CollisionShape;

public class FlatLevelTerrain extends LevelTerrain {
	
	public FlatLevelTerrain(String region, int x, int y) {
		super(region, x, y);		
	}
	
	@Override
	protected void doRender() {
		GL11.glBegin(GL11.GL_QUADS);
		
		for(int i = 0; i < LevelManager.chunkHeight; i++) {
			for(int j = 0; j < LevelManager.chunkWidth; j++) {
				setupTileMaterial(terrainTextures[i][j]);
				
				// Figure out the normal of the terrain surface so we can do
				// lighting.
				GL11.glNormal3f(0.0f, 1.0f, 0.0f);
				
				GL11.glVertex3f((float)j, 0, (float)i);
				GL11.glVertex3f((float)(j + 1), 0, (float)i);
				GL11.glVertex3f((float)(j + 1), 0, (float)(i + 1));
				GL11.glVertex3f((float)j, 0, (float)(i + 1));
			}
		}
		GL11.glEnd();
	}
	
	@Override
	public double heightAtPosition(double x, double y) {return 0;}

	@Override
	public CollisionShape getGroundShape() {return null;}

	@Override
	protected long shapeHashCode() {
		return hashCode();
	}

}
