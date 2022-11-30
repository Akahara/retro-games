package fr.wonder;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import fr.wonder.display.Display;
import fr.wonder.display.Graphics;
import fr.wonder.games.Pong;
import fr.wonder.games.Snake;

public class Main {
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		boolean fullScreen = System.getenv("WINDOWED") == null;
		boolean debugInfo = System.getenv("NO_DBG") == null;
		String gameName = args.length > 0 ? args[0] : "pong";
		
		System.out.println("Starting (" 
				+ (fullScreen?"fullscreen ":"") 
				+ (debugInfo?"debuginfo ":"") 
				+ ("gamename="+gameName)
				+ ")");
		
		int displayWidth = 5*100, displayHeight = 5*100;
		Display display = new Display(displayWidth, displayHeight, debugInfo);
		Keys.setActiveWindow(display.getWindowHandle());
		Graphics graphics = new Graphics(displayWidth, displayHeight);
		display.addResizeEventHandler(graphics::setDisplaySize);
		
		display.setVisible(true, fullScreen);
		
		Game game;
		
		switch (gameName) {
		case "snake": game = new Snake(); break;
		case "pong":  game = new Pong();  break;
		default:
			throw new IllegalArgumentException("Unknown game: " + gameName);
		}
		
		game.start(graphics);
		while(!display.shouldDispose()) {
			graphics.beginFrame();
			game.drawFrame(graphics);
			game.step(1/60.f);
			graphics.endFrame();
			display.sendFrame();
		}
		
		display.destroy();
	}

}
