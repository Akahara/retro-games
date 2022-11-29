package fr.wonder.display;

import fr.wonder.display.internal.Texture;

public class Sprite {
	
	final Texture texture;
	final float uvX, uvY, uvW, uvH;
	public final int width, height;
	
	Sprite(Texture texture, int x, int y, int w, int h) {
		if(x < 0 || x+w > texture.width)
			throw new IllegalArgumentException("X/W not in bounds");
		if(y < 0 || y+h > texture.height)
			throw new IllegalArgumentException("Y/H not in bounds");
		
		this.texture = texture;
		float o = .05f; // dirty fix to avoid texture atlas bleeding TODO resolve texture altas bleeding
		this.uvX = (float)(x+o) / texture.width;
		this.uvY = (float)(y+o) / texture.height;
		this.uvW = (float)(w-2*o) / texture.width;
		this.uvH = (float)(h-2*o) / texture.height;
		this.width = w;
		this.height = h;
	}
	
}
