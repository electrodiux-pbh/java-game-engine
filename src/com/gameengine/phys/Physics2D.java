package com.gameengine.phys;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import com.gameengine.GameEngineManager;
import com.gameengine.components.BoxCollider2D;
import com.gameengine.components.CircleCollider;
import com.gameengine.components.GameObject;
import com.gameengine.components.Rigidbody2D;
import com.gameengine.util.Timer;
import com.gameengine.util.Transform;

public class Physics2D {

	private Vec2 gravity = new Vec2(0, -GameEngineManager.getSceneProperties().getGravityScale());
	private World world = new World(gravity);
	
	private float physicsTime = 0.0F;
	private float physicsTimeStep = 1.0F / 30.0F;
	private int velocityIterations = 8;
	private int positionIterations = 3;
	
	public void addGameObject(@NotNull GameObject obj) {
		Rigidbody2D body = obj.getComponent(Rigidbody2D.class);
		if(body != null) {
			if(body.getRawBody() == null) {
				Transform transform = obj.transform;
				
				BodyDef bodyDef = new BodyDef();
				bodyDef.angle = (float) Math.toRadians(transform.rotation.getYaw());
				bodyDef.position.set(transform.position.x, transform.position.y);
				bodyDef.angularDamping = body.angularDamping;
				bodyDef.linearDamping = body.linearDamping;
				bodyDef.fixedRotation = body.fixedRotation;
				bodyDef.bullet = body.fixedCollision;
				bodyDef.type = body.bodyType;
				
				PolygonShape shape = new PolygonShape();
				CircleCollider circleCollider;
				BoxCollider2D boxCollider;
				
				if((circleCollider = obj.getComponent(CircleCollider.class)) != null) {
					shape.setRadius(circleCollider.getRadius());
				}
				if((boxCollider = obj.getComponent(BoxCollider2D.class)) != null) {
					Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5F);
					Vector2f offset = boxCollider.getOffset();
					Vector2f origin = new Vector2f(boxCollider.getOrigin());
					shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);
					
					Vec2 position = bodyDef.position;
					float xPos = position.x + offset.x;
					float yPos = position.y + offset.y;
					bodyDef.position.set(xPos, yPos);
				}
				
				Body rawBody = this.world.createBody(bodyDef);
				body.setRawBody(rawBody);
				rawBody.createFixture(shape, body.mass);
			}
		}
	}
	
	public void removeGameObject(@NotNull GameObject obj) {
		Rigidbody2D body = obj.getComponent(Rigidbody2D.class);
		if(body == null)
			return;
		if(body.getRawBody() == null)
			return;
		
		world.destroyBody(body.getRawBody());
		body.setRawBody(null);
	}
	
	public void update() {
		physicsTime += Timer.getDefaultTimer().getDeltaTime();
		if(physicsTime >= 0.0F) {
			physicsTime -= physicsTimeStep;
			world.step(physicsTimeStep, velocityIterations, positionIterations);
		}
	}
	
	public void setGravityScale(float gravityValue) {
		gravity.y = -gravityValue;
		world.setGravity(gravity);
	}
	
}
