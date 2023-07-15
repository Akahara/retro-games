package fr.wonder.games.pacman;

import fr.wonder.Game;
import fr.wonder.audio.Sound;
import fr.wonder.audio.SoundManager;
import fr.wonder.display.Graphics;
import fr.wonder.display.SpriteSheet;

public class Pacman  extends Game {
    private static final int W = 20; // width of a cell
    private static final int   PADDLE_HEIGHT = 30;
    private static final int   PADDLE_WIDTH = 8;
    private static final int Cx = 28; // cell count
    private static final int Cy = 36;

    private class Sounds {

        private Sound chomp = SoundManager.loadSound("games/pacman/chomp.wav");
        private Sound death = SoundManager.loadSound("games/pacman/death.wav");
        private Sound eatfruit = SoundManager.loadSound("games/pacman/eatfruit.wav");
        private Sound eatghost = SoundManager.loadSound("games/pacman/eatghost.wav");
        private Sound extralife = SoundManager.loadSound("games/pacman/extralife.wav");
        private Sound intermission = SoundManager.loadSound("games/pacman/intermission.wav");
        private Sound intro = SoundManager.loadSound("games/pacman/intro.wav");
    }

    private Pacman.Sounds sounds;
    private SpriteSheet sprites;
    @Override
    public void start(Graphics graphics) {
        //sounds=new Sounds();
        sprites=graphics.loadSpriteSheet("/games/pacman/sprites/pacman.png", 220, 160).setGrid(20, 20);

    }

    @Override
    public void drawFrame(Graphics graphics) {
        graphics.clear();
    }

    @Override
    public void step(float realDelta) {

    }
}
