package game.models;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBPointParameters;
import org.lwjgl.opengl.ARBPointSprite;
import org.lwjgl.opengl.GL11;

public class ParticleRenderer implements IRenderable {
	
	private static List<Particle> particles = new LinkedList<Particle>();

	@Override
	public void tryPrerender() {}

	@Override
	public void render() {
		if(particles.isEmpty())
			return;

		GL11.glEnable(ARBPointSprite.GL_POINT_SPRITE_ARB);
		GL11.glTexEnvf(ARBPointSprite.GL_POINT_SPRITE_ARB, ARBPointSprite.GL_COORD_REPLACE_ARB, GL11.GL_TRUE);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		final FloatBuffer quadratic = BufferUtils.createFloatBuffer(4);
		quadratic.put(1.0f).put(0.0f).put(0.1f).put(1.0f).flip();
		ARBPointParameters.glPointParameterARB(ARBPointParameters.GL_POINT_DISTANCE_ATTENUATION_ARB, quadratic);

		ARBPointParameters.glPointParameterfARB(ARBPointParameters.GL_POINT_SIZE_MAX_ARB, 100.0f);
		ARBPointParameters.glPointParameterfARB(ARBPointParameters.GL_POINT_SIZE_MIN_ARB, 1.0f);
		
		GL11.glPointSize(50.0f);
		
		GL11.glBegin(GL11.GL_POINTS);
		
		final Iterator<Particle> i = particles.listIterator();
		while(i.hasNext()) {
			final Particle p = i.next();
			
			GL11.glColor3f(p.red, p.green, p.blue);
			GL11.glVertex3d(p.x, p.y + 0.5f, p.z);
			
			p.x += p.vx;
			p.y += p.vy;
			p.z += p.vz;
			
			if(p.friction != 0.0f) { 
				p.vx -= p.vx * p.friction;
				if(!p.hasGravity)
					p.vy -= p.vy * p.friction;
				p.vz -= p.vz * p.friction;
			}
			if(p.hasGravity) {
				// TODO: Tie this into the normal physics gravity constant.
				p.vy -= 0.1f;
				if(p.y + p.vy < 0f) {
					p.vy *= -0.5f;
					p.y = 0;
				}
			}
			
			// Kill the particle.
			if(p.ticksBeforeDeath-- == 0)
				i.remove();
		}
		
		GL11.glEnd();
		
		GL11.glDisable(ARBPointSprite.GL_POINT_SPRITE_ARB);
	}

	@Override
	public void discard() {}
	
	public static void spawnParticle(double x, double y, double z, float red, float green, float blue, int duration) {
		spawnParticle(x, y, z, red, green, blue, duration, 1.0f, 1.0f, 1.0f, 0.0f, false);
	}
	public static void spawnParticle(double x, double y, double z,
									 float red, float green, float blue,
									 int duration,
									 double vx, double vy, double vz, double friction,
									 boolean hasGravity) {
		Particle p = new Particle();
		p.x = x;
		p.y = y;
		p.z = z;
		
		p.red = red;
		p.green = green;
		p.blue = blue;

		p.vx = vx;
		p.vy = vy;
		p.vz = vz;
		p.friction = friction;
		
		p.hasGravity = hasGravity;
		
		p.ticksBeforeDeath = duration;
		
		particles.add(p);
	}

}
