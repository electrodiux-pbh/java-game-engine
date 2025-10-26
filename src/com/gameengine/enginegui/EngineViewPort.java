package com.gameengine.enginegui;

import java.nio.IntBuffer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import com.gameengine.Engine;
import com.gameengine.GameEngineManager;
import com.gameengine.Scene;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Framebuffer;
import com.gameengine.graphics.Mouse;
import com.gameengine.languages.Language;
import com.gameengine.util.Camera;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class EngineViewPort {
	
	public static final Color DEFAULT_BG_COLOR = Color.WHITE;

	private static float leftX, rightX, topY, bottomY;
	private static boolean isFocus;
	
	public static void gameViewport(@NotNull Camera camera, @Nullable Scene scene) {
		if(camera.getFramebuffer() != null)
			gameViewport(camera.getFramebuffer(), camera, scene);
	}
	
	public static void gameViewport(@NotNull Framebuffer buffer, @NotNull Camera camera, @Nullable Scene scene) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.viewport.game") + "###game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar)) {
			ImGui.beginMenuBar();
			if(GameEngineManager.getCurrentScene() != null) {
				playStopBarButton();
			}
			ImGui.endMenuBar();
			
			IntBuffer guiVboId = MemoryUtil.memAllocInt(1);
			GL30.glGetIntegerv(GL30.GL_FRAMEBUFFER, guiVboId);
			
			buffer.bind();
			if(scene != null && camera != null) {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				camera.startFrame();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				scene.render();
			} else {
				GL11.glClearColor(DEFAULT_BG_COLOR.x, DEFAULT_BG_COLOR.y, DEFAULT_BG_COLOR.z, DEFAULT_BG_COLOR.w);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			}
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, guiVboId.get(0));
			
			MemoryUtil.memFree(guiVboId);
			
			viewport(buffer);
		}
		ImGui.end();
	}
	
	public static void editorViewport(@NotNull EngineCamera camera, @Nullable Scene scene) {
		if(camera.getFramebuffer() != null)
			editorViewport(camera.getFramebuffer(), camera, scene);
	}
	
	public static void editorViewport(@NotNull Framebuffer buffer, @NotNull EngineCamera camera, @Nullable Scene scene) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.viewport.editor") + "###editor", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar)) { 
			ImGui.beginMenuBar();
			if(GameEngineManager.getCurrentScene() != null) {
				playStopBarButton();
				ImGui.separator();
				cameraBarButton();
			}
			ImGui.endMenuBar();
			
			IntBuffer guiVboId = MemoryUtil.memAllocInt(1);
			GL30.glGetIntegerv(GL30.GL_FRAMEBUFFER, guiVboId);
			
			buffer.bind();
			if(scene != null && camera != null) {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				camera.startFrame();
				camera.update();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				scene.engineRender(Engine.getEngineProperties().getCamera());
				EditorDebugDraw.render(Engine.getEngineProperties().getCamera());
			} else {
				GL11.glClearColor(DEFAULT_BG_COLOR.x, DEFAULT_BG_COLOR.y, DEFAULT_BG_COLOR.z, DEFAULT_BG_COLOR.w);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			}
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, guiVboId.get(0));
			
			MemoryUtil.memFree(guiVboId);
			
			viewport(buffer);
		}
		ImGui.end();
		
	}
	
	private static void playStopBarButton() {
		if(ImGui.menuItem(Engine.getEngineLanguage().get("window.viewport.button.play") + "##play", "", Engine.isRunningGameRuntime(), !Engine.isRunningGameRuntime())) {
			Engine.startRunningGameRuntime();
		}
		if(ImGui.menuItem(Engine.getEngineLanguage().get("window.viewport.button.stop"), "##stop", !Engine.isRunningGameRuntime(), Engine.isRunningGameRuntime())) {
			Engine.stopRunningGameRuntime();
		}
	}
	
	private static void cameraBarButton() {
		Language lang = Engine.getEngineLanguage();
		EngineCamera cam = Engine.getEngineProperties().getCamera();
		if(ImGui.beginMenu(lang.get("window.viewport.button.editor-camera") + "##editor-camera")) {
			if(ImGui.beginMenu(lang.get("general.properties"))) {
				cam.gui();
				ImGui.endMenu();
			}
			if(ImGui.beginMenu(lang.get("window.viewport.grid.mode") + "##grid")) {
				if(ImGui.menuItem("2D", "", false, cam.is3D())) {
					cam.set2D();
				}
				if(ImGui.menuItem("3D", "", false, cam.is2D())) {
					cam.set3D();
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu(lang.get("window.viewport.grid") + "##grid")) {
				if(ImGui.menuItem(lang.get("window.viewport.grid.lines") + "##lines", "", Engine.getEngineProperties().isGridLines())) {
					Engine.getEngineProperties().setGridLines(!Engine.getEngineProperties().isGridLines());
				}
				float[] linesWidth = new float[] { Engine.getEngineProperties().getLinesWidth() };
				if(ImGui.dragFloat(lang.get("window.viewport.grid.lines.width") + "##width", linesWidth, 0.1F, EditorDebugDraw.MIN_LINE_WIDTH, EditorDebugDraw.MAX_LINE_WIDTH)) {
					Engine.getEngineProperties().setLinesWidth(linesWidth[0]);
				}
				int[] gridRadius = new int[] { Engine.getEngineProperties().getGridRadius() };
				if(ImGui.dragInt(lang.get("window.viewport.grid.radius") + "##radius", gridRadius, 1, 5, 50)) {
					Engine.getEngineProperties().setGridRadius(gridRadius[0]);
				}
				ImGui.endMenu();
			}
			ImGui.endMenu();
		}
		if(ImGui.menuItem("2D", "", false, cam.is3D())) {
			cam.set2D();
		}
		if(ImGui.menuItem("3D", "", false, cam.is2D())) {
			cam.set3D();
		}
	}
	
	private static void viewport(@NotNull Framebuffer buffer) {
		isFocus = ImGui.isWindowFocused();
		
		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPosition = getCenteredPositionForViewport(windowSize);
		
		ImGui.setCursorPos(windowPosition.x, windowPosition.y);
		
		ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;
		
		int textureId = buffer.getTextureID();
		ImGui.image(textureId, windowSize.x, windowSize.y, 0.0F, 1.0F, 1.0F, 0.0F);
	}
	
	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Engine.getProjectProperties().getAspectRatioValue();
		if(aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Engine.getProjectProperties().getAspectRatioValue();
		}
		
		return new ImVec2(aspectWidth, aspectHeight);
	}
	
	private static ImVec2 getCenteredPositionForViewport(@NotNull ImVec2 aspectSize) {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		float viewportX = (windowSize.x / 2.0F) - (aspectSize.x / 2.0F);
		float viewportY = (windowSize.y / 2.0F) - (aspectSize.y / 2.0F);
		
		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}
	
	public static boolean getWantCaptureMouse() {
		return Mouse.getX() >= leftX && Mouse.getX() <= rightX && Mouse.getY() >= bottomY && Mouse.getY() <= topY && isFocus;
	}
	
	public static boolean getWantCaptureKeyboard() {
		return getWantCaptureMouse();
	}
	
}
