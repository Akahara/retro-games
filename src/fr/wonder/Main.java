package fr.wonder;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import fr.wonder.display.Display;
import fr.wonder.display.Graphics;
import fr.wonder.games.Pong;
import fr.wonder.games.Snake;

public class Main {
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		Display display = new Display(500, 500);
		Keys.setActiveWindow(display.getWindowHandle());
		Graphics graphics = new Graphics(display.getWinWidth(), display.getWinHeight());
		display.addResizeEventHandler(graphics::setDisplaySize);
		
		boolean fullScreen = System.getenv("WINDOWED") == null;
		display.setVisible(true, fullScreen);
		
		String gameName = args.length > 0 ? args[0] : "pong";
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
