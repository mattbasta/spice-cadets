package game.physics;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.MotionState;

public interface ICollidable {
	public CollisionShape getPhysicsShape();
	public MotionState getMotionState();
}
