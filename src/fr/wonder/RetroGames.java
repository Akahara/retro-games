package fr.wonder;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import fr.wonder.display.Display;
import fr.wonder.display.Graphics;
import fr.wonder.games.Pong;
import fr.wonder.games.Snake;

public class RetroGames {
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		boolean debugInfo = System.getenv("NO_DBG") == null;
		String gameName = getGameToRun(args);
		
		Display display = new Display(debugInfo);
		Keys.setActiveWindow(display.getWindowHandle());
		Graphics graphics = new Graphics(display.getWinWidth(), display.getWinHeight());
		display.addResizeEventHandler(graphics::setDisplaySize);
		display.setVisible(true);
		
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
	
	private static String getGameToRun(String[] args) {
		// To run a specific game during development,
		// set your run configuration's args to [ "<game name>" ]
		// or alternatively set your DBG_GAME environment variable
		// to the game's name
		
		if(args.length > 0)
			return args[0];
		String fromEnv = System.getenv("DBG_GAME");
		if(fromEnv != null)
			return fromEnv;
		return "snake";
	}

}
