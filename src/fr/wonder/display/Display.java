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
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

public class Display {
	
	private long window;
	private int winWidth, winHeight;
	
	private final List<BiConsumer<Integer, Integer>> resizeEventHandlers = new ArrayList<>();
	
	public Display(int resolutionX, int resolutionY, boolean debugInfo) {
		if(debugInfo)
			GLFWErrorCallback.createPrint(System.err).set();
		
		winWidth = resolutionX;
		winHeight = resolutionY;
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW !");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
		
		window = glfwCreateWindow(resolutionX, resolutionY, "Shader Display", NULL, NULL);

		if (window == NULL)
			throw new IllegalStateException("Unable to create a window !");

		glfwMakeContextCurrent(window);
		glfwFocusWindow(window);
		glfwSwapInterval(1);

		GL.createCapabilities();
		
		if(debugInfo)
			GLUtil.setupDebugMessageCallback(System.err);
		
		glEnable(GL_BLEND);
		
		glViewport(0, 0, resolutionX, resolutionY);
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
	
	public void setVisible(boolean visible, boolean fullScreen) {
		if(visible) {
			glfwShowWindow(window);
			if(fullScreen) {
				long monitor = glfwGetPrimaryMonitor();
				int[] width = new int[1], height = new int[1];
				int[] xpos = new int[1], ypos = new int[1];
				glfwGetMonitorWorkarea(monitor, xpos, ypos, width, height);
				glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, width[0], height[0], GLFW_DONT_CARE);
			}
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
