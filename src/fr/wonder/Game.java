package fr.wonder;

import fr.wonder.display.Graphics;

public abstract class Game {

	public abstract void start(Graphics graphics);
	public abstract void drawFrame(Graphics graphics);
	public abstract void step(float realDelta);
	
}
