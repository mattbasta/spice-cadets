package game.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class Box implements IRenderable {
	
	private enum RenderType {
		Shape, Color, Texture
	}
	
	public float x, y, z;
	public float size_x, size_y, size_z;
	private RenderType renderType = RenderType.Shape;
	private Color shapeColor = null;
	
	/**
	 * Initializes a new Box.
	 * 
	 * @param x	The X coordinate of the center of the box.
	 * @param y The Y coordinate of the center of the box.
	 * @param z	The Z coordinate of the center of the box.
	 * @param size_x	The size of the box along the X-axis.
	 * @param size_y	The size of the box along the Y-axis.
	 * @param size_z	The size of the box along the Z-axis.
	 */
	public Box(float x, float y, float z, float size_x, float size_y, float size_z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.size_x = size_x;
		this.size_y = size_y;
		this.size_z = size_z;
	}
	/**
	 * Initializes a new Box that's colored.
	 * 
	 * @param x	The X coordinate of the center of the box.
	 * @param y The Y coordinate of the center of the box.
	 * @param z	The Z coordinate of the center of the box.
	 * @param size_x	The size of the box along the X-axis.
	 * @param size_y	The size of the box along the Y-axis.
	 * @param size_z	The size of the box along the Z-axis.
	 * @param color		The color of the box.
	 */
	public Box(float x, float y, float z, float size_x, float size_y, float size_z, Color color) {
		this(x, y, z, size_x, size_y, size_z);
		this.renderType = RenderType.Color;
		this.shapeColor = color;
	}
	/*
	public Box(float x, float y, float z, float size_x, float size_y, float size_z, Texture texture) {
		this(x, y, z, size_x, size_y, size_z);
		this.renderType = RenderType.Texture;
		this.shapeTexture = texture;
	}*/
	
	// Cube drawing bit
    // Borrowed in part from Cube.java, written by Ciardhubh
    public static final float[][] cube_vertices = {
        {-0.5f, -0.5f, -0.5f}, // 0
        {0.5f, -0.5f, -0.5f},
        {0.5f, 0.5f, -0.5f},
        {-0.5f, 0.5f, -0.5f}, // 3
        {-0.5f, -0.5f, 0.5f}, // 4
        {0.5f, -0.5f, 0.5f},
        {0.5f, 0.5f, 0.5f},
        {-0.5f, 0.5f, 0.5f} // 7
    };
    public static final float[][] cube_normals = {
        {0, 0, -1},
        {0, 0, 1},
        {0, -1, 0},
        {0, 1, 0},
        {-1, 0, 0},
        {1, 0, 0}
    };
    public static final byte[][] cube_indicies = {
        {0, 3, 2, 1},
        {4, 5, 6, 7},
        {0, 1, 5, 4},
        {3, 7, 6, 2},
        {0, 4, 7, 3},
        {1, 2, 6, 5}
    };

  
	public void render() {
		
		boolean disableDepth = false;
		if(renderType == RenderType.Color) {
			final float alpha = shapeColor.getAlpha() / 255.0f;
			disableDepth = alpha != 1.0f;
			GL11.glColor4f(shapeColor.getRed() / 255.0f,
						   shapeColor.getGreen() / 255.0f,
						   shapeColor.getBlue() / 255.0f,
						   alpha);
		
		}
		
		if(disableDepth)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glBegin(GL11.GL_QUADS);
			
		for(int i = 0; i < 6; i++) {
            for(int m = 0; m < 4; m++) {
                float[] temp = cube_vertices[cube_indicies[i][m]];
                GL11.glNormal3f(cube_normals[i][0], cube_normals[i][1], cube_normals[i][2]);
                GL11.glVertex3f(temp[0] * size_x + x, temp[1] * size_y + y, temp[2] * size_z + z);
            }
        }

		GL11.glEnd();

		if(disableDepth)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		
	}
	
	@Override
	public void tryPrerender() {}
	
	@Override
	public void discard() {}
	
}
