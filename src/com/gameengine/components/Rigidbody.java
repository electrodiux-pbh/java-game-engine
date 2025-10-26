package com.gameengine.components;

import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

import com.bulletphysics.dynamics.RigidBody;

public class Rigidbody extends Component {

	private Vector2f velocity = new Vector2f();
	public float angularDamping = 0.8F;
	public float linearDamping = 0.8F;
	public float mass = 1;
	public BodyType bodyType = BodyType.DYNAMIC;
	
	public boolean fixedRotation = false;
	public boolean fixedCollision = true;
	
	private transient RigidBody rawBody;

	@Override
	public void update() {
		if(rawBody != null) {
//			com.bulletphysics.linearmath.Transform transform = rawBody.getWorldTransform(null);
//			
//			this.parent().transform.position.setX();
//			this.parent().transform.position.setY(rawBody.getPosition().y);
//			this.parent().transform.rotation.setYaw((float) Math.toDegrees(rawBody.getAngle()));
		}
	}
	
	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public float getAngularDamping() {
		return angularDamping;
	}

	public void setAngularDamping(float angularDamping) {
		this.angularDamping = angularDamping;
	}

	public float getLinearDamping() {
		return linearDamping;
	}

	public void setLinearDamping(float linearDamping) {
		this.linearDamping = linearDamping;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public BodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}

	public boolean isFixedRotation() {
		return fixedRotation;
	}

	public void setFixedRotation(boolean fixedRotation) {
		this.fixedRotation = fixedRotation;
	}

	public boolean isFixedCollision() {
		return fixedCollision;
	}

	public void setFixedCollision(boolean continuousCollision) {
		this.fixedCollision = continuousCollision;
	}
	
	public RigidBody getRawBody() {
		return rawBody;
	}

	public void setRawBody(RigidBody body) {
		this.rawBody = body;
	}
	
}
