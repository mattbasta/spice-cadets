package game.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.MotionState;

import game.IUpdatable;

public class GamePhysicsManager implements IUpdatable {

	public static final float gravity = -9.8f;

	private final CollisionConfiguration configuration;
	private final CollisionDispatcher dispatcher;
	private final BroadphaseInterface overlappingPairCache;
	private final ConstraintSolver solver;
	private final DynamicsWorld dynamicsWorld;
	
	private final List<ICollidable> collidables = new ArrayList<ICollidable>();
	private final Map<ICollidable, RigidBody> bodies = new HashMap<ICollidable, RigidBody>();
	//private final Queue<BoxShape> releasedBoxes = new LinkedList<BoxShape>();
	//private final Queue<SphereShape> releasedSpheres = new LinkedList<SphereShape>();

	public GamePhysicsManager() {
		// Set up the configuration for the collision dynamics.
		configuration = new DefaultCollisionConfiguration();
		// Set up the dispatcher.
		dispatcher = new CollisionDispatcher(configuration);

		// Set up the pair cache.
		final Vector3f aabbMin = new Vector3f(-10000, -10, -10000),
					   aabbMax = new Vector3f(10000, 50, 10000);
		overlappingPairCache = new AxisSweep3(aabbMin, aabbMax);
		
		// Set up the solver.
		solver = new SequentialImpulseConstraintSolver();
		
		// Set up the world simulator.
		dynamicsWorld =
				new DiscreteDynamicsWorld(dispatcher, overlappingPairCache,
										  solver, configuration);
		dynamicsWorld.setGravity(new Vector3f(0.0f, gravity, 0.0f));
		
	}
	
	/**
	 * Registers an ICollidable object with the physics engine and inserts it
	 * into the world.
	 * @param object The collidable object to register.
	 */
	public void registerCollidable(ICollidable object) {
		if(bodies.containsKey(object)) {
			// If the body is already present, unregister its rigid body and
			// register a new one.
			RigidBody oldBody = bodies.get(object);
			dynamicsWorld.removeRigidBody(oldBody);
			bodies.remove(object);
		} else
			collidables.add(object);
		
		RigidBody body = getRigidBody(object.getPhysicsShape(), 1.0f,
									  object.getMotionState());
		bodies.put(object, body);
	}
	
	/**
	 * Generate a rigid body object for a given shape, mass, and motion state.
	 * @param shape The shape of the rigid body.
	 * @param mass The mass of the body.
	 * @param motionState The motion state that the physics object will map to.
	 * @return The newly constructed rigid body.
	 */
	private RigidBody getRigidBody(CollisionShape shape, float mass, MotionState motionState) {
		Vector3f inertia = new Vector3f(0.0f, 0.0f, 0.0f);
		shape.calculateLocalInertia(mass, inertia);
		
		RigidBodyConstructionInfo rbInfo =
				new RigidBodyConstructionInfo(mass, motionState, shape, inertia);
		return new RigidBody(rbInfo);
	}

	@Override
	public void tick(float ratio) {
		dynamicsWorld.stepSimulation(ratio * 1000 / 60);
	}
	
	/**
	 * Clear all collidable objects from the world.
	 */
	public void reset() {
		collidables.clear();
		int bodyCount = bodies.size();
		for(int i = 0; i < bodyCount; i++) {
			RigidBody r = bodies.get(i);
			dynamicsWorld.removeRigidBody(r);
		}
		bodies.clear();
	}

}
