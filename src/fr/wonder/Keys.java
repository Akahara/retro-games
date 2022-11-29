package fr.wonder;

import static org.lwjgl.glfw.GLFW.*;

public class Keys {
	
	private static long window;
	
	public static void setActiveWindow(long windowHandle) {
		window = windowHandle;
	}

	public static final int KEY_P1_UP = GLFW_KEY_UP;
	public static final int KEY_P1_DOWN = GLFW_KEY_DOWN;
	public static final int KEY_P1_LEFT = GLFW_KEY_LEFT;
	public static final int KEY_P1_RIGHT = GLFW_KEY_RIGHT;
	public static final int KEY_P2_UP = GLFW_KEY_W;
	public static final int KEY_P2_DOWN = GLFW_KEY_S;
	public static final int KEY_P2_LEFT = GLFW_KEY_A;
	public static final int KEY_P2_RIGHT = GLFW_KEY_D;
	
	public static boolean isKeyDown(int key) {
		return glfwGetKey(window, key) != GLFW_RELEASE;
	}
	
	public static boolean isAnyDown(int... keys) {
		for(int k : keys)
			if(isKeyDown(k))
				return true;
		return false;
	}
	
}
