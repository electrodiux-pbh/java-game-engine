package com.gameengine.enginegui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import com.gameengine.data.SourceManager;
import com.gameengine.graphics.Keyboard;
import com.gameengine.graphics.Mouse;
import com.gameengine.graphics.Texture;
import com.gameengine.graphics.Window;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImBoolean;

public class GUILayer {

	private Window window;

	private final int[] winWidth = new int[1];
	private final int[] winHeight = new int[1];
	private final int[] fbWidth = new int[1];
	private final int[] fbHeight = new int[1];
	
	private final double[] mousePosX = new double[1];
	private final double[] mousePosY = new double[1];

	private final long[] mouseCursors = new long[Mouse.AMMOUNT_OF_CURSORS];

	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	public GUILayer(Window window) {
		this.window = window;
	}
	
	public void initImGui() {
		ImGui.createContext();
		ImGui.styleColorsClassic();
		try {
			GUI.configureStyle(ImGui.getStyle(), SourceManager.readStringStream(getClass().getResourceAsStream("/default-style.stl")));
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		final ImGuiIO io = ImGui.getIO();

		File initFile = new File(SourceManager.getDataFolder(), "gui.ini");
		try {
			if(!initFile.exists()) {
				initFile.getParentFile().mkdirs();
				initFile.createNewFile();
				SourceManager.copyData(getClass().getResourceAsStream("/default-gui.ini"), new FileOutputStream(initFile));
			}
		} catch (IOException e) { }
		
		io.setIniFilename(initFile.getAbsolutePath());
		io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
		io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors);
		io.setBackendPlatformName("imgui_java_impl_glfw");
		io.setBackendRendererName("imgui_java_impl_lwjgl");

		final int[] keyMap = new int[ImGuiKey.COUNT];
		keyMap[ImGuiKey.Tab] = GLFW.GLFW_KEY_TAB;
		keyMap[ImGuiKey.LeftArrow] = GLFW.GLFW_KEY_LEFT;
		keyMap[ImGuiKey.RightArrow] = GLFW.GLFW_KEY_RIGHT;
		keyMap[ImGuiKey.UpArrow] = GLFW.GLFW_KEY_UP;
		keyMap[ImGuiKey.DownArrow] = GLFW.GLFW_KEY_DOWN;
		keyMap[ImGuiKey.PageUp] = GLFW.GLFW_KEY_PAGE_UP;
		keyMap[ImGuiKey.PageDown] = GLFW.GLFW_KEY_PAGE_DOWN;
		keyMap[ImGuiKey.Home] = GLFW.GLFW_KEY_HOME;
		keyMap[ImGuiKey.End] = GLFW.GLFW_KEY_END;
		keyMap[ImGuiKey.Insert] = GLFW.GLFW_KEY_INSERT;
		keyMap[ImGuiKey.Delete] = GLFW.GLFW_KEY_DELETE;
		keyMap[ImGuiKey.Backspace] = GLFW.GLFW_KEY_BACKSPACE;
		keyMap[ImGuiKey.Space] = GLFW.GLFW_KEY_SPACE;
		keyMap[ImGuiKey.Enter] = GLFW.GLFW_KEY_ENTER;
		keyMap[ImGuiKey.Escape] = GLFW.GLFW_KEY_ESCAPE;
		keyMap[ImGuiKey.KeyPadEnter] = GLFW.GLFW_KEY_KP_ENTER;
		keyMap[ImGuiKey.A] = GLFW.GLFW_KEY_A;
		keyMap[ImGuiKey.C] = GLFW.GLFW_KEY_C;
		keyMap[ImGuiKey.V] = GLFW.GLFW_KEY_V;
		keyMap[ImGuiKey.X] = GLFW.GLFW_KEY_X;
		keyMap[ImGuiKey.Y] = GLFW.GLFW_KEY_Y;
		keyMap[ImGuiKey.Z] = GLFW.GLFW_KEY_Z;
		io.setKeyMap(keyMap);

		mouseCursors[Mouse.CURSOR_ARROW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		mouseCursors[Mouse.CURSOR_TEXT_INPUT] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
		mouseCursors[Mouse.CURSOR_RESIZE_ALL] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
		mouseCursors[Mouse.CURSOR_RESIZE_NS] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
		mouseCursors[Mouse.CURSOR_RESIZE_EW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
		mouseCursors[Mouse.CURSOR_RESIZE_NESW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		mouseCursors[Mouse.CURSOR_RESIZE_NWSE] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		mouseCursors[Mouse.CURSOR_HAND] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
		mouseCursors[Mouse.CURSOR_NOT_ALLOWED] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		try {
			GLFWImage cursorGrab = Texture.loadImageToGLFW("sources/cursors/grab.png");
			mouseCursors[Mouse.CURSOR_GRAB] = GLFW.glfwCreateCursor(cursorGrab, cursorGrab.width(), cursorGrab.height());
			GLFWImage cursorGrabbing = Texture.loadImageToGLFW("sources/cursors/grabbing.png");
			mouseCursors[Mouse.CURSOR_GRABBING] = GLFW.glfwCreateCursor(cursorGrabbing, cursorGrabbing.width(), cursorGrabbing.height());
		} catch (IOException e) {
			e.printStackTrace();
		}

		GLFW.glfwSetKeyCallback(window.getWindowID(), (w, key, scancode, action, mods) -> {
			if (action == GLFW.GLFW_PRESS) {
				io.setKeysDown(key, true);
			} else if (action == GLFW.GLFW_RELEASE) {
				io.setKeysDown(key, false);
			}

			io.setKeyCtrl(io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL));
			io.setKeyShift(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT));
			io.setKeyAlt(io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT));
			io.setKeySuper(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER));
			
			if(EngineViewPort.getWantCaptureKeyboard()) {
				Keyboard.keyCallBack(w, key, scancode, action, mods);
			} else {
				Keyboard.clear();
			}
		});

		GLFW.glfwSetCharCallback(window.getWindowID(), (w, c) -> {
			if (c != GLFW.GLFW_KEY_DELETE) {
				io.addInputCharacter(c);
			}
		});

		GLFW.glfwSetMouseButtonCallback(window.getWindowID(), (w, button, action, mods) -> {
			final boolean[] mouseDown = new boolean[5];

			mouseDown[0] = button == GLFW.GLFW_MOUSE_BUTTON_1 && action != GLFW.GLFW_RELEASE;
			mouseDown[1] = button == GLFW.GLFW_MOUSE_BUTTON_2 && action != GLFW.GLFW_RELEASE;
			mouseDown[2] = button == GLFW.GLFW_MOUSE_BUTTON_3 && action != GLFW.GLFW_RELEASE;
			mouseDown[3] = button == GLFW.GLFW_MOUSE_BUTTON_4 && action != GLFW.GLFW_RELEASE;
			mouseDown[4] = button == GLFW.GLFW_MOUSE_BUTTON_5 && action != GLFW.GLFW_RELEASE;

			io.setMouseDown(mouseDown);

			if (!io.getWantCaptureMouse() && mouseDown[1]) {
				ImGui.setWindowFocus(null);
			}
			
			if(EngineViewPort.getWantCaptureMouse()) {
				Mouse.mouseButtonCallBack(w, button, action, mods);
			} else {
				Mouse.clear();
			}
			
		});

		GLFW.glfwSetScrollCallback(window.getWindowID(), (w, xOffset, yOffset) -> {
			io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
			io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
			
			if(EngineViewPort.getWantCaptureMouse()) {
				Mouse.mouseScrollCallBack(w, xOffset, yOffset);
			} else {
				Mouse.clear();
			}
		});

		io.setSetClipboardTextFn(new ImStrConsumer() {
			@Override
			public void accept(final String s) {
				GLFW.glfwSetClipboardString(window.getWindowID(), s);
			}
		});

		io.setGetClipboardTextFn(new ImStrSupplier() {
			@Override
			public String get() {
				return GLFW.glfwGetClipboardString(window.getWindowID());
			}
		});
		
		imGuiGl3.init();
	}
	
	public void render(@NotNull GUIable gui) {

		GLFW.glfwGetWindowSize(window.getWindowID(), winWidth, winHeight);
		GLFW.glfwGetFramebufferSize(window.getWindowID(), fbWidth, fbHeight);
		GLFW.glfwGetCursorPos(window.getWindowID(), mousePosX, mousePosY);

		final ImGuiIO io = ImGui.getIO();
		io.setDisplaySize(winWidth[0], winHeight[0]);
		io.setDisplayFramebufferScale((float) fbWidth[0] / winWidth[0], (float) fbHeight[0] / winHeight[0]);
		io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
		io.setDeltaTime((float) 1F / 60F);

		final int imguiCursor = ImGui.getMouseCursor();
		GLFW.glfwSetCursor(window.getWindowID(), mouseCursors[imguiCursor]);
		GLFW.glfwSetInputMode(window.getWindowID(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);

		ImGui.newFrame();
		setupDockspace();
		gui.gui();
		ImGui.end();
		ImGui.render();

		imGuiGl3.renderDrawData(ImGui.getDrawData());
	}
	
	private void setupDockspace() {
		int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
		
		ImGui.setNextWindowPos(0.0F, 0.0F, ImGuiCond.Always);
		ImGui.setNextWindowSize(window.getWidth(), window.getHeight());
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0F);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0F);
		
		windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
				ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
				ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
		
		ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
		ImGui.popStyleVar(2);
		
		ImGui.dockSpace(ImGui.getID("Dockspace"));
	}

	public void destroyImGui() {
		imGuiGl3.dispose();
		ImGui.destroyContext();
	}
	
}
