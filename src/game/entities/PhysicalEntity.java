package game.entities;

import game.level.LevelManager;
import game.physics.ICollidable;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public abstract class PhysicalEntity extends Entity implements ICollidable {

	private final MotionState motionState;
	private final FloatBuffer transformBuffer = BufferUtils.createFloatBuffer(16);
	
	public PhysicalEntity(LevelManager levelManager, EntityManager entityManager) {
		super(levelManager, entityManager);
		motionState = new DefaultMotionState();
	}

	@Override
	public MotionState getMotionState() {return motionState;}
	
	@Override
	public void doPosition() {
		final Transform t = new Transform();
		t.set(((DefaultMotionState)motionState).graphicsWorldTrans);
		
		final float[] glMatrix = new float[16];
		t.getOpenGLMatrix(glMatrix);
		transformBuffer.clear();
		transformBuffer.put(glMatrix).flip();
		GL11.glMultMatrix(transformBuffer);
	}

}
