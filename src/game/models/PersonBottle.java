package game.models;

import org.lwjgl.opengl.GL11;

import game.entities.Person;

public class PersonBottle implements IRenderable {
	
	private final static float bodyWidth = 0.5f;
	private final static float neckWidth = 0.25f;
	private final static float bodyHeight = 1.4f;
	private final static float neckHeight = 0.2f;
	private final static float glassThickness = 0.1f;
	
	protected final Person me;
	
	public PersonBottle(Person whoAmI) {
		me = whoAmI;
	}

	@Override
	public void tryPrerender() {}
	
	private void setColor(float opacity) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, opacity);
	}

	@Override
	public void render() {
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		setColor(0.4f);
		
		final double init_rot = -me.rotation - 90.0f;
		// Render the back
		
		// Render the bottom outside of the neck
		/*GL11.glBegin(GL11.GL_QUADS);
		double angle = init_rot / 180.0f * Math.PI;
		for(double i = init_rot; i < init_rot + 170.0f; i += 10) {
			final double next_angle = (i + 10) / 180.0f * Math.PI;
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GL11.glVertex3f((float)Math.sin(angle) * bodyWidth, 0.0f, (float)Math.cos(angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(angle) * bodyWidth, bodyHeight, (float)Math.cos(angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * bodyWidth, bodyHeight, (float)Math.cos(next_angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * bodyWidth, 0.0f, (float)Math.cos(next_angle) * bodyWidth);
            
            angle = next_angle;
		}
		GL11.glEnd();*/
		
		//render contents here
		
		GL11.glBegin(GL11.GL_QUADS);
		double angle = init_rot / 180.0f * Math.PI;
		for(double i = init_rot; i < init_rot + 180; i += 10) {
			final double next_angle = (i + 10) / 180.0f * Math.PI;
			GL11.glNormal3f((float)Math.sin(angle), 0f, (float)Math.cos(angle));
            GL11.glVertex3f((float)Math.sin(angle) * bodyWidth, 0.0f, (float)Math.cos(angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(angle) * bodyWidth, bodyHeight, (float)Math.cos(angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * bodyWidth, bodyHeight, (float)Math.cos(next_angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * bodyWidth, 0.0f, (float)Math.cos(next_angle) * bodyWidth);
            
            angle = next_angle;
		}
		GL11.glEnd();
		
		
		// Render the bottom outside of the neck
		GL11.glBegin(GL11.GL_QUADS);
		angle = init_rot / 180.0f * Math.PI;
		for(double i = init_rot; i < init_rot + 360; i += 10) {
			final double next_angle = (i + 10) / 180.0f * Math.PI;
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GL11.glVertex3f((float)Math.sin(angle) * neckWidth, bodyHeight, (float)Math.cos(angle) * neckWidth);
            GL11.glVertex3f((float)Math.sin(angle) * bodyWidth, bodyHeight, (float)Math.cos(angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * bodyWidth, bodyHeight, (float)Math.cos(next_angle) * bodyWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * neckWidth, bodyHeight, (float)Math.cos(next_angle) * neckWidth);
            
            angle = next_angle;
		}
		GL11.glEnd();

		// Render the neck
		GL11.glBegin(GL11.GL_QUADS);
		angle = init_rot / 180.0f * Math.PI;
		for(double i = init_rot; i < init_rot + 360; i += 10) {
			final double next_angle = (i + 10) / 180.0f * Math.PI;
			GL11.glNormal3f((float)Math.sin(angle), 0f, (float)Math.cos(angle));
            GL11.glVertex3f((float)Math.sin(angle) * neckWidth, bodyHeight, (float)Math.cos(angle) * neckWidth);
            GL11.glVertex3f((float)Math.sin(angle) * neckWidth, bodyHeight + neckHeight, (float)Math.cos(angle) * neckWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * neckWidth, bodyHeight + neckHeight, (float)Math.cos(next_angle) * neckWidth);
            GL11.glVertex3f((float)Math.sin(next_angle) * neckWidth, bodyHeight, (float)Math.cos(next_angle) * neckWidth);
            
            setColor(0.2f);
            
            // The inside of the neck
            GL11.glVertex3f((float)Math.sin(angle) * (neckWidth - glassThickness), bodyHeight, (float)Math.cos(angle) * (neckWidth - glassThickness));
            GL11.glVertex3f((float)Math.sin(angle) * (neckWidth - glassThickness), bodyHeight + neckHeight, (float)Math.cos(angle) * (neckWidth - glassThickness));
            GL11.glVertex3f((float)Math.sin(next_angle) * (neckWidth - glassThickness), bodyHeight + neckHeight, (float)Math.cos(next_angle) * (neckWidth - glassThickness));
            GL11.glVertex3f((float)Math.sin(next_angle) * (neckWidth - glassThickness), bodyHeight, (float)Math.cos(next_angle) * (neckWidth - glassThickness));
            
            setColor(0.4f);
            
            angle = next_angle;
		}
		GL11.glEnd();
		
		// Render the top outside of the neck
		GL11.glBegin(GL11.GL_QUADS);
		angle = init_rot / 180.0f * Math.PI;
		for(double i = init_rot; i < init_rot + 360; i += 10) {
			final double next_angle = (i + 10) / 180.0f * Math.PI;
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GL11.glVertex3f((float)Math.sin(angle) * neckWidth, bodyHeight + neckHeight, (float)Math.cos(angle) * neckWidth);
            GL11.glVertex3f((float)Math.sin(angle) * (neckWidth - glassThickness), bodyHeight + neckHeight, (float)Math.cos(angle) * (neckWidth - glassThickness));
            GL11.glVertex3f((float)Math.sin(next_angle) * (neckWidth - glassThickness), bodyHeight + neckHeight, (float)Math.cos(next_angle) * (neckWidth - glassThickness));
            GL11.glVertex3f((float)Math.sin(next_angle) * neckWidth, bodyHeight + neckHeight, (float)Math.cos(next_angle) * neckWidth);
            
            angle = next_angle;
		}
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
	}

	@Override
	public void discard() {}

}
