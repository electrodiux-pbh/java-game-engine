package com.gameengine.phys;

import javax.vecmath.Vector3f;

import org.jetbrains.annotations.NotNull;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.gameengine.components.GameObject;
import com.gameengine.components.Rigidbody;
import com.gameengine.util.Timer;

public class Physics3D {

	private Vector3f gravityVector;
	
	private DynamicsWorld world;
	private BroadphaseInterface broadphase;
	
	private CollisionConfiguration collisionConfig;
	private CollisionDispatcher collisionDispatcher;
	
	private ConstraintSolver solver;
	
	private float physicsTime = 0.0F;
	private float physicsTimeStep = 1.0F / 30.0F;
	private int velocityIterations = 8;
	private int positionIterations = 3;
	
	public void loadPhysics() {
		gravityVector = new Vector3f(0, -9.8F, 0);
		
		broadphase = new DbvtBroadphase();
		
		collisionConfig = new DefaultCollisionConfiguration();
		collisionDispatcher = new CollisionDispatcher(collisionConfig);
		
		solver = new SequentialImpulseConstraintSolver();
		
		world = new DiscreteDynamicsWorld(collisionDispatcher, broadphase, solver, collisionConfig);
		world.setGravity(gravityVector);
	}
	
	public void addGameObject(@NotNull GameObject obj) {
		Rigidbody body = obj.getComponent(Rigidbody.class);
		if(body != null) {
			world.addRigidBody(body.getRawBody());
		}
	}
	
	public void removeGameObject(@NotNull GameObject obj) {
		Rigidbody body = obj.getComponent(Rigidbody.class);
		if(body == null)
			return;
		if(body.getRawBody() == null)
			return;
		world.removeRigidBody(body.getRawBody());
	}
	
	public void update() {
		physicsTime += Timer.getDefaultTimer().getDeltaTime();
		if(physicsTime >= 0.0F) {
			physicsTime -= physicsTimeStep;
			world.stepSimulation(physicsTimeStep, velocityIterations, positionIterations);
		}
	}
	
	public void setGravityScale(float gravityValue) {
		gravityVector.y = -gravityValue;
		world.setGravity(gravityVector);
	}
	
}
