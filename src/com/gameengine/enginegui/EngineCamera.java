package com.gameengine.enginegui;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.gameengine.Engine;
import com.gameengine.graphics.AspectRatio;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Keyboard;
import com.gameengine.graphics.Mouse;
import com.gameengine.program.CameraScript;
import com.gameengine.util.Camera;
import com.gameengine.util.Position;
import com.gameengine.util.Rotation;
import com.gameengine.util.Timer;

import imgui.ImGui;

public class EngineCamera extends Camera {

	public static final Color DEFAULT_ENGINE_CAMERA_BG_COLOR = new Color(0.18823F, 0.27058F, 0.42352F);
	
	private transient EngineCameraScript script;
	private boolean camera3D;
	
	public EngineCamera() {
		this(AspectRatio.ASPECT_16_9.getAspectValue());
	}
	
	public EngineCamera(float aspectRatio) {
		this(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
	}
	
	public EngineCamera(float aspectRatio, boolean isOrtho) {
		this(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
		super.setOrtho(isOrtho);
		if(isOrtho) {
			makeProjection();
		}
	}
	
	public EngineCamera(float fov, float aspectRatio, float nearPlane, float farPlane) {
		this(new Position(0.0F, 0.0F, 0.0F), new Rotation(0.0F, 0.0F, 0.0F), fov, aspectRatio, nearPlane, farPlane);
	}
	
	public EngineCamera(Position position, Rotation rotation, float fov, float aspectRatio, float nearPlane, float farPlane) {
		super(position, rotation, fov, aspectRatio, nearPlane, farPlane);
		super.bg = DEFAULT_ENGINE_CAMERA_BG_COLOR;
		set3D();
	}
	
	@Override
	public void gui() {
		if(ImGui.checkbox("Orthographic", isOrtho()))
			setOrtho(!isOrtho());
		ImGui.separator();
		if(ImGui.checkbox("2D", is2D())) {
			set2D();
		}
		if(ImGui.checkbox("3D", is3D())) {
			set3D();
		}
		ImGui.separator();
		GUI.renderVector3f("Position", getPosition());
		GUI.renderVector3f("Rotation", getRotation());
		float[] fov = new float[] { getFov() };
		float[] nearPlane = new float[] { getNearPlane() };
		float[] farPlane = new float[] { getFarPlane() };
		if(ImGui.dragFloat("Fov", fov, 0.1F, Camera.MIN_FOV, Camera.MAX_FOV))
			setFov(fov[0]);
		if(ImGui.dragFloat("NearPlane", nearPlane, 0.1F, 0.1F, Float.MAX_VALUE))
			setNearPlane(nearPlane[0]);
		if(ImGui.dragFloat("FarPlane", farPlane, 0.1F, 1.0F, Float.MAX_VALUE))
			setFarPlane(farPlane[0]);
		GUI.colorPicker(super.bg);
	}
	
	public void set3D() {
		if(is2D()) {
			camera3D = true;
			setScript(new EngineCameraScript3D());
		}
	}
	
	public void set2D() {
		if(is3D()) {
			camera3D = false;
			
			Rotation rot = getRotation();
			rot.x = 0;
			rot.y = 0;
			
			setScript(new EngineCameraScript2D());
		}
	}
	
	public boolean is3D() {
		return camera3D;
	}
	
	@Override
	public void updateScript() {
		if(script != null) {
			try {
				script.onUpdate(this);
				script.onRender(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setScript(@Nullable EngineCameraScript script) {
		this.script = script;
		this.script.onLoad();
	}
	
	public boolean is2D() {
		return !camera3D;
	}
	
	private static abstract class EngineCameraScript extends CameraScript {
		
		public abstract void onRender(Camera camera);
		
	}
	
	private static class EngineCameraScript2D extends EngineCameraScript {
		
		public float mouseSensitivity = 0.02F;
		
		@Override
		public void onUpdate(Camera camera) {
			Position position = camera.getPosition();
				
			if(Mouse.isDraggin()) {
				position.x += Mouse.getDX() * mouseSensitivity;
				position.y -= Mouse.getDY() * mouseSensitivity;
				Mouse.setCursor(Mouse.CURSOR_GRABBING);
			}
			position.z -= Mouse.getScrollY();
		}

		@Override
		public void onRender(Camera camera) {
			if(Engine.getEngineProperties().isGridLines()) {
				int Xmin = -Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().x;
				int Xmax = Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().x;
				int Ymin = -Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().y;
				int Ymax = Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().y;
				for(int x = Xmin; x < Xmax + 1; x++) {
					EditorDebugDraw.addLine3D(new Vector3f(x, Ymax, 0), new Vector3f(x, Ymin, 0));
				}
				for(int y = Ymin; y < Ymax + 1; y++) {
					EditorDebugDraw.addLine3D(new Vector3f(Xmax, y, 0), new Vector3f(Xmin, y, 0));
				}
			}
		}
		
	}
	
	private static class EngineCameraScript3D extends EngineCameraScript {
		
		private static final double HALF_PI = Math.PI / 2.0D;
		
		public float mouseSensitivity = 0.1F;
		public float force = 5;
		
		@Override
		public void onUpdate(Camera camera) {
			Position position = camera.getPosition();
			Rotation rotation = camera.getRotation();
			float force = this.force * Timer.deltaTime();
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_W)) {
				position.x += Math.sin(Math.toRadians(rotation.x)) * force;
				position.z -= Math.cos(Math.toRadians(rotation.x)) * force;
				position.y -= Math.sin(Math.toRadians(rotation.y)) * force;
			}
				
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_S)) {
				position.x -= Math.sin(Math.toRadians(rotation.x)) * force;
				position.z += Math.cos(Math.toRadians(rotation.x)) * force;
				position.y += Math.sin(Math.toRadians(rotation.y)) * force;
			}
				
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_A)) {
				position.x -= Math.sin(Math.toRadians(rotation.x) + HALF_PI) * force;
				position.z += Math.cos(Math.toRadians(rotation.x) + HALF_PI) * force;
			}
				
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_D)) {
				position.x -= Math.sin(Math.toRadians(rotation.x) - HALF_PI) * force;
				position.z += Math.cos(Math.toRadians(rotation.x) - HALF_PI) * force;
			}
				
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
				position.y += force;
			}
				
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
				position.y -= force;
			}
				
			if(Mouse.isDraggin()) {
				rotation.x += Mouse.getDX() * mouseSensitivity;
				rotation.y += Mouse.getDY() * mouseSensitivity;
				if(rotation.y > 90)
					rotation.y = 90;
				if(rotation.y < -90)
					rotation.y = -90;
				Mouse.setCursor(Mouse.CURSOR_GRABBING);
			}
			
			if(Mouse.getScrollY() != 0) {
				position.x += Math.sin(Math.toRadians(rotation.x)) * Mouse.getScrollY();
				position.z -= Math.cos(Math.toRadians(rotation.x)) * Mouse.getScrollY();
				position.y -= Math.sin(Math.toRadians(rotation.y)) * Mouse.getScrollY();
			}
		}
		
		@Override
		public void onRender(Camera camera) {
			if(Engine.getEngineProperties().isGridLines()) {
				int Xmin = -Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().x;
				int Xmax = Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().x;
				int Zmin = -Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().z;
				int Zmax = Engine.getEngineProperties().getGridRadius() + (int) camera.getPosition().z;
				for(int x = Xmin; x < Xmax + 1; x++) {
					EditorDebugDraw.addLine3D(new Vector3f(x, 0, Zmax), new Vector3f(x, 0, Zmin));
				}
				for(int z = Zmin; z < Zmax + 1; z++) {
					EditorDebugDraw.addLine3D(new Vector3f(Xmax, 0, z), new Vector3f(Xmin, 0, z));
				}
			}
		}
	}
	
}
