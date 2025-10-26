package com.gameengine.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.gameengine.Engine;
import com.gameengine.data.Scriptable;
import com.gameengine.enginegui.GUI;
import com.gameengine.enginegui.Inspectable;
import com.gameengine.graphics.AspectRatio;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Framebuffer;
import com.gameengine.languages.Language;
import com.gameengine.program.CameraScript;
import com.gameengine.program.Script;

import imgui.ImGui;

public class Camera implements Scriptable, Inspectable, Nameable {

	public static final float FOV = 70;
	public static final float MIN_FOV = 10;
	public static final float MAX_FOV = 150;
	public static final float NEAR_PLANE = 0.1F;
	public static final float FAR_PLANE = 100;

	protected transient Matrix4f projectionMatrix, viewMatrix;
	protected Position position;
	protected Rotation rotation;
	protected boolean isOrtho = false;
	protected transient CameraScript script;

	protected transient Framebuffer framebuffer;

	protected float aspectRatio;
	protected float farPlane;
	protected float nearPlane;
	protected float fov;

	protected Color bg;
	private String name;

	protected transient Vector2f projectionSize = new Vector2f(6, 3);

	public Camera() {
		this(AspectRatio.ASPECT_16_9.getAspectValue());
	}

	public Camera(float aspectRatio) {
		this(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
	}

	public Camera(float aspectRatio, boolean isOrtho) {
		this(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
		this.isOrtho = isOrtho;
		if (isOrtho) {
			makeProjection();
		}
	}

	public Camera(float fov, float aspectRatio, float nearPlane, float farPlane) {
		this(new Position(0.0F, 0.0F, 0.0F), new Rotation(0.0F, 0.0F, 0.0F), fov, aspectRatio, nearPlane, farPlane);
	}

	public Camera(Position position, Rotation rotation, float fov, float aspectRatio, float nearPlane, float farPlane) {
		this.position = position;
		this.rotation = rotation;

		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;

		this.bg = Color.WHITE;
		this.name = "Camera";

		this.viewMatrix = new Matrix4f();
		makeProjection();

		this.setScript(new CameraScriptTest());
	}

	public void makeProjection() {
		projectionMatrix = new Matrix4f();
		if (isOrtho()) {
			projectionMatrix.identity();
			projectionMatrix.ortho(0.0f, projectionSize.x, 0.0f, projectionSize.y, 0.0f, 100.0f);
		} else {
			float yScale = (float) ((1.0F / Math.tan(Math.toRadians(fov / 2.0F))) * aspectRatio);
			float xScale = yScale / aspectRatio;
			float frustumLength = farPlane - nearPlane;

			projectionMatrix.m00(xScale);
			projectionMatrix.m11(xScale);
			projectionMatrix.m22(-((farPlane + nearPlane) / frustumLength));
			projectionMatrix.m23(-1);
			projectionMatrix.m32(-((2 * farPlane * nearPlane) / frustumLength));
			projectionMatrix.m33(0);
		}
	}

	@NotNull
	public Matrix4f getViewMatrix() {
		viewMatrix.identity();

		viewMatrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));

		Vector3f inverseCameraPos = new Vector3f(-position.x, -position.y, -position.z);

		viewMatrix.translate(inverseCameraPos);
		return this.viewMatrix;
	}

	public void startFrame() {
		GL11.glClearColor(bg.x, bg.y, bg.z, bg.w);
		if (framebuffer != null) {
			framebuffer.bind();
		}
	}

	public void endFrame() {
		if (framebuffer != null) {
			framebuffer.destroy();
		}
	}

	public void gui() {
		Language lang = Engine.getEngineLanguage();

		this.name = GUI.inputText(lang.get("general.name"), this.name);
		if (ImGui.checkbox("Orthographic", isOrtho()))
			setOrtho(!isOrtho());
		ImGui.separator();
		GUI.renderScriptable(this);
		ImGui.separator();
		GUI.renderVector3f("Position", getPosition());
		GUI.renderVector3f("Rotation", getRotation());
		float[] fov = new float[] { getFov() };
		float[] nearPlane = new float[] { getNearPlane() };
		float[] farPlane = new float[] { getFarPlane() };
		if (ImGui.dragFloat("Fov", fov, 0.1F, Camera.MIN_FOV, Camera.MAX_FOV))
			setFov(fov[0]);
		if (ImGui.dragFloat("NearPlane", nearPlane, 0.1F, 0.1F, Float.MAX_VALUE))
			setNearPlane(nearPlane[0]);
		if (ImGui.dragFloat("FarPlane", farPlane, 0.1F, 1.0F, Float.MAX_VALUE))
			setFarPlane(farPlane[0]);
		GUI.colorPicker(bg);
	}

	public void update() {
		updateScript();
	}

	@NotNull
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@NotNull
	public Position getPosition() {
		return position;
	}

	@NotNull
	public Rotation getRotation() {
		return rotation;
	}

	public boolean isOrtho() {
		return isOrtho;
	}

	public void setValues(float fov, float aspectRatio, float nearPlane, float farPlane) {
		if (this.fov != fov && this.aspectRatio != aspectRatio && this.nearPlane != nearPlane
				&& this.farPlane != farPlane) {
			this.fov = fov;
			this.aspectRatio = aspectRatio;
			this.nearPlane = nearPlane;
			this.farPlane = farPlane;
			makeProjection();
		} else {
			this.fov = fov;
			this.aspectRatio = aspectRatio;
			this.nearPlane = nearPlane;
			this.farPlane = farPlane;
		}
	}

	public void setOrtho(boolean isOrtho) {
		if (this.isOrtho != isOrtho) {
			this.isOrtho = isOrtho;
			makeProjection();
		} else {
			this.isOrtho = isOrtho;
		}
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(AspectRatio ratio) {
		setAspectRatio(ratio.getAspectValue());
	}

	public void setAspectRatio(float aspectRatio) {
		if (this.aspectRatio != aspectRatio) {
			this.aspectRatio = aspectRatio;
			makeProjection();
		} else {
			this.aspectRatio = aspectRatio;
		}
	}

	public float getFarPlane() {
		return farPlane;
	}

	public void setFarPlane(float farPlane) {
		if (this.farPlane != farPlane) {
			this.farPlane = farPlane;
			makeProjection();
		} else {
			this.farPlane = farPlane;
		}
	}

	public float getNearPlane() {
		return nearPlane;
	}

	public void setNearPlane(float nearPlane) {
		if (this.nearPlane != nearPlane) {
			this.nearPlane = nearPlane;
			makeProjection();
		} else {
			this.nearPlane = nearPlane;
		}
	}

	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		if (this.fov != fov) {
			this.fov = fov;
			makeProjection();
		} else {
			this.fov = fov;
		}
	}

	@Override
	public void updateScript() {
		if (script != null) {
			try {
				script.onUpdate(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	@Nullable
	public Script getScript() {
		return script;
	}

	@Override
	public void setScript(@Nullable Script script) {
		if (script == null)
			return;
		if (!CameraScript.class.isAssignableFrom(script.getClass()))
			return;
		this.script = (CameraScript) script;
		this.script.onLoad();
	}

	@Nullable
	public Framebuffer getFramebuffer() {
		return framebuffer;
	}

	public void setFramebuffer(@Nullable Framebuffer framebuffer) {
		if (this.framebuffer != null)
			this.framebuffer.destroy();
		this.framebuffer = framebuffer;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}