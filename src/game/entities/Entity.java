package game.entities;

import java.util.Random;

import javax.vecmath.Vector2f;

import org.lwjgl.opengl.GL11;

import game.IUpdatable;
import game.level.LevelManager;
import game.models.IRenderable;

public abstract class Entity implements IRenderable, IUpdatable {
	
	public static final float dampeningPerTick = 0.5f;

	private int damage = 0;
	private boolean dead = false;
	
	public double x, y; // Position
	public double v_x, v_y; // Velocity
	private double a_x, a_y; // Acceleration (impulse)
	public double rotation; // Rotation stored in degrees
	
	public boolean visible = true;

	protected final LevelManager levelManager;
	protected final EntityManager entityManager;
	
	protected static final Random rng = new Random();
	
	public Entity(LevelManager levelManager, EntityManager entityManager) {
		this.levelManager = levelManager;
		this.entityManager = entityManager;
		entityManager.add(this);
	}
	
	/**
	 * Damages the entity.
	 * 
	 * @param value			The amount of damage being dealt.
	 * @param damaged_by	The entity that's causing the damage.
	 */
	public void doDamage(int value, Entity damaged_by) {
		damage += value;
		damaged(damaged_by);
	}
	
	/**
	 * Applies forward acceleration to the entity. This must be called each
	 * tick to apply constant acceleration.
	 * 
	 * @param forward The amount of acceleration to apply.
	 */
	public void impulse(float forward) {
		a_x += (float) Math.cos(rotation / 360 * 2 * Math.PI) * forward;
		a_y -= (float) Math.sin(rotation / 360 * 2 * Math.PI) * forward;
	}
	/**
	 * Applies constant acceleration along two axes. This must be called each
	 * tick to apply constant acceleration.
	 * 
	 * @param x The acceleration along the X-axis.
	 * @param y The acceleration along the Y-axis.
	 */
	public void impulse(float x, float y) {
		a_x += x;
		a_y += y;
	}
	
	/**
	 * Gets the speed of the entity.
	 * 
	 * @return The forward velocity of the entity.
	 */
	public double getSpeed() {return Math.sqrt(v_x * v_x + v_y * v_y);}
	
	/**
	 * Method that is called when the entity is damaged by another entity.
	 * 
	 * @param damaged_by The entity that has damaged this entity.
	 */
	public abstract void damaged(Entity damaged_by);
	
	/**
	 * Kill the entity.
	 */
	public void kill() {
		dead = true;
		died();
	}
	
	/**
	 * Determine whether the entity is dead.
	 * @return A boolean value describing whether the entity is not alive.
	 */
	public boolean isDead() {return dead;}
	
	/**
	 * Method called when the entity has died.
	 */
	protected abstract void died();
	
	@Override
	public void tick(float ratio) {
		if(a_x != 0) {
			v_x += a_x;
			a_x = 0;
		}
		if(a_y != 0) {
			v_y += a_y;
			a_y = 0;
		}
		x += v_x;
		y += v_y;

		if(v_x != 0 || v_y != 0) {
			final Vector2f velocity = new Vector2f((float)v_x, (float)v_y);
			velocity.scale(dampeningPerTick);
			v_x = velocity.x;
			v_y = velocity.y;
		}
	}
	
	/**
	 * Position the entity in the world. This method's implementation should
	 * use GL11.glTranslate3f(). If the entity is not being rendered, it can
	 * simply return without doing anything, though this check may already be
	 * performed by the caller.
	 */
	public abstract void doPosition();
	
	/**
	 * Perform a full draw operation on the entity.
	 */
	@Override
	public final void render() {
		if(!visible)
			return;
		GL11.glPushMatrix();
		doPosition();
		doRender();
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw the entity. This should not perform any standard tests; that is
	 * handled at the entity level. 
	 */
	public abstract void doRender();
	
}
