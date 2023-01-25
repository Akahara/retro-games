package fr.wonder.display.internal;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;

public class Texture {

	public final int width, height;
	public final int id;
	
	private Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public static Texture loadTexture(String path) throws IOException {
		try (InputStream is = Texture.class.getResourceAsStream(path)) {
			if (is == null)
				throw new IOException("Resource " + path + " does not exist");
			return loadTexture(is);
		}
	}
	
	public static Texture loadTexture(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return loadTexture(is);
		}
	}
	
	public static Texture loadTexture(InputStream stream) throws IOException {
		return loadTexture(BufferUtils.readAllToBuffer(stream));
	}
	
	public static Texture loadTexture(ByteBuffer image) {
		int id;
		int[] widthB = new int[1], heightB = new int[1], channelsB = new int[1];
		STBImage.stbi_set_flip_vertically_on_load(true);
		ByteBuffer buf = STBImage.stbi_load_from_memory(image, widthB, heightB, channelsB, 4);
		
		int width = widthB[0], height = heightB[0];

		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		unbind();
		
		return new Texture(id, width, height);
	}
	
	public static Texture fromBuffer(int width, int height, ByteBuffer rawImageBuffer) {
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_FLOAT, rawImageBuffer);
		unbind();
		return new Texture(id, width, height);
	}

	public void bind(int slot) {
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public static void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void dispose() {
		glDeleteTextures(id);
	}
}
