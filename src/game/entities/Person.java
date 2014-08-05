package game.entities;

import game.IUpdatable;
import game.level.LevelManager;
import game.models.Model;
import game.models.ParticleRenderer;
import game.models.PersonBottle;
import game.models.PlayerModel;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

public abstract class Person extends PhysicalEntity implements IUpdatable {
	
	protected final Model model = new PlayerModel();
	
	public static final float rotationPerTick = 2.2f;
	public static final float movementPerTick = 0.1f;
	
	private float waddle_counter = 0.0f;
	private double waddle_rotate = 0.0f;
	private double waddle_hop = 0.0f;
	private float runningTilt = 0.0f;
	
	public boolean isRunning = false;
	public boolean isSneaking = false;

	private final PersonBottle body;

	public Person(LevelManager levelManager, EntityManager entityManager) {
		super(levelManager, entityManager);
		body = new PersonBottle(this);
	}

	@Override
	public void damaged(Entity damaged_by) {}

	@Override
	public CollisionShape getPhysicsShape() {
		return new BoxShape(new Vector3f(1.0f, 2.0f, 1.0f));
	}
	
	@Override
	public void doPosition() {
		super.doPosition();

		GL11.glTranslated(this.x, levelManager.getHeightAtPoint((float)this.x, (float)this.y), this.y);
		GL11.glRotated(this.rotation, 0.0f, 1.0f, 0.0f);
		
		float waddleScale = 1.0f;
		if(isSneaking)
			waddleScale = 0.2f;
		
		if(getSpeed() > 0.0025f) {
			waddle_counter += 0.2f * waddleScale;
			waddle_rotate = (waddle_rotate + Math.sin(waddle_counter / 1.7)) / 2;
			waddle_hop = (waddle_hop + Math.sin(waddle_counter * 1.2)) / 2;
		} else {
			if(waddle_counter > 0)
				waddle_counter = 0;
			waddle_rotate *= 0.9f;
			waddle_hop *= 0.7f;
		}

		GL11.glTranslated(0.0f, Math.abs(waddle_hop) * 0.5f, 0.0f);
		GL11.glRotated(waddle_rotate * waddle_rotate * waddle_rotate * 7.0f, 1.0f, 0.0f, 0.0f);
		
		float runTilt = 0.0f;
		if(isRunning && !isSneaking)
			runTilt = 10.0f;
		runningTilt = (runningTilt * 3 + runTilt) / 4;
		GL11.glRotated(runningTilt, 0.0f, 0.0f, 1.0f);			
	}
	
	@Override
	public void tick(float ratio) {
		super.tick(ratio);
		
		if(isRunning && rng.nextInt(5) == 0)
			kickUpDirt();
	}
	
	private void kickUpDirt() {
		float wiggle_x = rng.nextFloat() * 0.3f - 0.15f,
			  wiggle_z = rng.nextFloat() * 0.3f - 0.15f,
			  wiggle_vx = rng.nextFloat() * 0.3f - 0.15f,
			  wiggle_vy = rng.nextFloat() * 0.3f - 0.15f;
		ParticleRenderer.spawnParticle(
			x + wiggle_x, 0.0f, y + wiggle_z,
		    1.0f, 1.0f, 1.0f, 30,
		    v_x * -1.5f + wiggle_vx, 0.3f, v_y * -1.5f + wiggle_vy,
		    0.2f, true
		);
	}
	
	@Override
	protected void died() {
		for(int i = 0; i < 300; i++) {
			final double direction = rng.nextFloat() * Math.PI * 2,
						 magnitude = rng.nextFloat() - 0.5f;
			ParticleRenderer.spawnParticle(
				x, rng.nextFloat() + 0.2f, y,
				0.9f, 0.0f, 0.0f, rng.nextInt(20) + 50,
				Math.sin(direction) * magnitude, rng.nextFloat() * 0.5f + 0.2f, Math.cos(direction) * magnitude,
			    0.05f, true
			);
		}
		
		visible = false;
	}
	
	@Override
	public void doRender() {
		//model.render();
		body.render();
	}

	@Override
	public void tryPrerender() {model.tryPrerender();}
	
	@Override
	public void discard() {model.discard();}

}
