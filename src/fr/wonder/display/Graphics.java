package fr.wonder.display;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import fr.wonder.display.internal.FrameBuffer;
import fr.wonder.display.internal.IndexBuffer;
import fr.wonder.display.internal.Shader;
import fr.wonder.display.internal.Shader.ShaderType;
import fr.wonder.display.internal.ShaderProgram;
import fr.wonder.display.internal.Texture;
import fr.wonder.display.internal.VertexArray;
import fr.wonder.display.internal.VertexBuffer;
import fr.wonder.display.internal.VertexBufferLayout;

public class Graphics {
	
	private final VertexArray quadVAO;
	private final ShaderProgram quadShader;
	private final ShaderProgram blitShader;
	private final Texture whiteTexture;
	private FrameBuffer frameBuffer;
	
	private int width, height;
	private int displayWidth, displayHeight;
	
	private float blitX, blitY, blitW, blitH;
	
	private static class Effects {
		
		float CRTStrength = 0;
		float jaggedStrength = 0;
		float cornerStretchStrength = 0;
		
	}
	
	private final Effects effects = new Effects();
	
	public Graphics(int displayWidth, int displayHeight) throws IOException {
		this.quadVAO = new VertexArray();
		this.quadVAO.setBuffer(new VertexBuffer(), new VertexBufferLayout());
		this.quadVAO.setIndices(new IndexBuffer(0, 1, 2, 2, 3, 0));
		this.quadShader = new ShaderProgram(
				new Shader("/shaders/quad.vs", ShaderType.VERTEX),
				new Shader("/shaders/quad.fs", ShaderType.FRAGMENT)
		);
		this.blitShader = new ShaderProgram(
				new Shader("/shaders/blit.vs", ShaderType.VERTEX),
				new Shader("/shaders/blit.fs", ShaderType.FRAGMENT)
		);
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		setSize(displayWidth, displayHeight);
		ByteBuffer whiteTextureBuf = ByteBuffer.allocateDirect(4*4*1*1);
		whiteTextureBuf.order(ByteOrder.LITTLE_ENDIAN);
		whiteTextureBuf.putFloat(1).putFloat(1).putFloat(1).putFloat(1);
		whiteTextureBuf.position(0);
		this.whiteTexture = Texture.fromBuffer(1, 1, whiteTextureBuf);
	}

	public void setSize(int w, int h) {
		this.quadShader.bind();
		this.width = w;
		this.height = h;
		this.frameBuffer = new FrameBuffer(w, h, false);
		this.quadShader.setUniformMat4f("u_camera", new float[] {
			2.f/w, 0,     0, -1,
			0,     2.f/h, 0, -1,
			0,     0,     1,  0,
			0,     0,     0,  1,
		});
		updateBlitCoords();
	}
	
	public void setDisplaySize(int width, int height) {
		this.displayWidth = width;
		this.displayHeight = height;
		updateBlitCoords();
	}
	
	private void updateBlitCoords() {
		// letterbox stretching
		
		// width and height ratios
		float rw = (float)displayWidth/width;
		float rh = (float)displayHeight/height;
		float mr = Math.min(rw, rh);
		// do not try to find how I found these expressions
		blitW = rw/mr;
		blitH = rh/mr;
		blitX = (1-blitW)/2.f;
		blitY = (1-blitH)/2.f;
	}
	
	public void beginFrame() {
		// technically this could be called only once
		// as we do not need to unbind the fbo to write
		// to the screen buffer
		FrameBuffer.unbind();
		glViewport(0, 0, displayWidth, displayHeight);
		glClear(GL_COLOR_BUFFER_BIT);
		frameBuffer.bind();
	}
	
	public void endFrame() {
		FrameBuffer.unbind();
		glViewport(0, 0, displayWidth, displayHeight);
		quadVAO.bind();
		frameBuffer.bindTexture(0);
		blitShader.bind();
		blitShader.setUniform2f("u_resolution", displayWidth, displayHeight);
		blitShader.setUniform4f("u_transform", blitX, blitY, blitW, blitH);
		blitShader.setUniform1f("u_CRTStrength", effects.CRTStrength);
		blitShader.setUniform1f("u_jaggedStrength", effects.jaggedStrength);
		blitShader.setUniform1f("u_cornerStretch", effects.cornerStretchStrength);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, NULL);
	}
	
	/**
	 * width and height parameters exist just to make sure the caller knows the
	 * size of the texture.
	 */
	public SpriteSheet loadSpriteSheet(String path, int width, int height) {
		Texture texture;
		try {
			texture = Texture.loadTexture(path);
		} catch (IOException e) {
			throw new IllegalStateException("Could not load texture " + path, e);
		}
		if(texture.width != width || texture.height != height)
			throw new IllegalArgumentException(String.format("Got size %dx%d, expected %dx%d", texture.width, texture.height, width, height));
		return new SpriteSheet(texture);
	}

	public void setEffect_CRT(float strength) { this.effects.CRTStrength = strength; }
	public void setEffect_cornerStretch(float strength) { this.effects.cornerStretchStrength = strength; }
	public void setEffect_jaggedStretch(float strength) { this.effects.jaggedStrength = strength; }
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT);
	}

	public void drawRect(int x, int y, int w, int h, Color color) {
		quadVAO.bind();
		whiteTexture.bind(0);
		quadShader.bind();
		quadShader.setUniform4f("u_transform", x, y, w, h);
		quadShader.setUniform4f("u_color", color.getRed()/255.f, color.getGreen()/255.f, color.getBlue()/255.f, 1.f);
//		quadShader.setUniform1i("u_texture", 0);
		quadShader.setUniform4f("u_uv", 0, 0, 1, 1);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, NULL);
	}
	
	public void drawSprite(Sprite sprite, int x, int y) {
		drawSprite(sprite, x, y, sprite.width, sprite.height);
	}
	
	public void drawSprite(Sprite sprite, int x, int y, int w, int h) {
		quadVAO.bind();
		sprite.texture.bind(0);
		quadShader.bind();
		quadShader.setUniform4f("u_transform", x, y, w, h);
		quadShader.setUniform4f("u_color", 1,1,1,1);
//		quadShader.setUniform1i("u_texture", 0);
		quadShader.setUniform4f("u_uv", sprite.uvX, sprite.uvY, sprite.uvW, sprite.uvH);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, NULL);
	}
	
}
