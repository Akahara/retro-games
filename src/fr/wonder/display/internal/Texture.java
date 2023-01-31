package fr.wonder.display.internal;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Texture {
	
	static {
		stbi_set_flip_vertically_on_load(true);
	}

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
	
	public static Texture loadTexture(ByteBuffer rawImageContent) throws IOException {
		int id;
		int[] widthB = new int[1], heightB = new int[1], channelsB = new int[1];
		ByteBuffer buf = null;
		try {
			buf = stbi_load_from_memory(rawImageContent, widthB, heightB, channelsB, 4);
			String error = stbi_failure_reason();
			
			if(error != null)
				throw new IOException("Invalid image: " + error);
			
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
		} finally {
			if(buf != null)
				stbi_image_free(buf);
		}
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
