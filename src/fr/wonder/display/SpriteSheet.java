package fr.wonder.display;

import fr.wonder.display.internal.Texture;

public class SpriteSheet {
	
	private Texture texture;
	private int gridCellWidth, gridCellHeight;
	
	SpriteSheet(Texture texture) {
		this.texture = texture;
	}
	
	public SpriteSheet setGrid(int cellWidth, int cellHeight) {
		if(texture.width % cellWidth != 0)
			throw new IllegalArgumentException("Cannot divide " + texture.width + " by " + cellWidth + " evenly");
		if(texture.height % cellHeight != 0)
			throw new IllegalArgumentException("Cannot divide " + texture.height + " by " + cellHeight + " evenly");
		this.gridCellWidth = cellWidth;
		this.gridCellHeight = cellHeight;
		return this;
	}
	
	public Sprite getSprite(int x, int y, int width, int height) {
		return new Sprite(texture, x, y, width, height);
	}
	
	public Sprite getSprite(int spriteIndex) {
		if(gridCellWidth == 0)
			throw new IllegalArgumentException("Grid not set, call #setGrid first");
		int gridX = spriteIndex % (texture.width/gridCellWidth);
		int gridY = spriteIndex / (texture.width/gridCellWidth);
		if(spriteIndex < 0 || spriteIndex > texture.width*texture.height/gridCellWidth/gridCellHeight)
			throw new IllegalArgumentException("Sprite index out of bounds " + spriteIndex);
		return new Sprite(texture, gridX*gridCellWidth, gridY*gridCellHeight, gridCellWidth, gridCellHeight);
	}
	
	/** Should be avoided, retro games use spritesheets in spirit */
	public Sprite asSprite() {
		return new Sprite(texture, 0, 0, texture.width, texture.height);
	}
	
}
