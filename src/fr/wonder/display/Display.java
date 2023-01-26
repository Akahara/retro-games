package fr.wonder.display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

public class Display {
	
	private long window;
	private int winWidth, winHeight;
	
	private final List<BiConsumer<Integer, Integer>> resizeEventHandlers = new ArrayList<>();
	
	public Display(boolean debugInfo) {
		if(debugInfo)
			GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW !");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
		glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
//		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode videoMode = glfwGetVideoMode(monitor);
		glfwWindowHint(GLFW_RED_BITS, videoMode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate());
		winWidth  = videoMode.width();
		winHeight = videoMode.height();
		
		window = glfwCreateWindow(winWidth, winHeight, "Retro games", monitor, NULL);

		if (window == NULL)
			throw new IllegalStateException("Unable to create a window !");

		glfwMakeContextCurrent(window);
		glfwFocusWindow(window);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		glfwSwapInterval(1); // enable VSync

		GL.createCapabilities();
		
		if(debugInfo)
			GLUtil.setupDebugMessageCallback(System.err);
		
		glEnable(GL_BLEND);
		
		glViewport(0, 0, winWidth, winHeight);
		glClearColor(0, 0, 0, 1);
		
		glfwSetWindowSizeCallback(window, (win, w, h) -> {
			glViewport(0, 0, w, h);
			winWidth = w;
			winHeight = h;
			for(BiConsumer<Integer, Integer> handler : resizeEventHandlers)
				handler.accept(w, h);
		});
		
		glfwSetKeyCallback(window, (win, key, scanCode, action, mods) -> {
			if(action == GLFW_PRESS && key == GLFW_KEY_ESCAPE) {
				glfwSetWindowShouldClose(window, true);
			}
		});
	}
	
	public long getWindowHandle() {
		return window;
	}
	
	public void addResizeEventHandler(BiConsumer<Integer, Integer> handler) {
		resizeEventHandlers.add(handler);
	}

	public void destroy() {
		Callbacks.glfwFreeCallbacks(window);
		GLFWErrorCallback ec = glfwSetErrorCallback(null);
		if(ec != null) ec.free();
		GL.setCapabilities(null);
		GL.destroy();
		glfwDestroyWindow(window);
		glfwTerminate();
		window = 0;
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			glfwShowWindow(window);
		} else {
			glfwHideWindow(window);
		}
	}
	
	public boolean shouldDispose() {
		return glfwWindowShouldClose(window);
	}

	public void sendFrame() {
		glfwSwapBuffers(window);
		glfwPollEvents();
	}
	
	public void setWindowTitle(String title) {
		glfwSetWindowTitle(window, title);
	}
	
	public int getWinWidth() {
		return winWidth;
	}
	
	public int getWinHeight() {
		return winHeight;
	}

}
