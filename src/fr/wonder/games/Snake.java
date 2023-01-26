package fr.wonder.games;

import java.util.ArrayList;
import java.util.List;

import fr.wonder.Game;
import fr.wonder.Keys;
import fr.wonder.audio.ClipSettings;
import fr.wonder.audio.Sound;
import fr.wonder.audio.SoundManager;
import fr.wonder.display.Color;
import fr.wonder.display.Graphics;

public class Snake extends Game {

	private static final int W = 10; // width of a cell
	private static final int C = 12; // cell count (in x and y)
	private static final int EYE_SIZE = 2;
	private static final float INITIAL_GAME_SPEED = .3f; // in movements per second
	private static final float DEATH_ANIMATION_DURATION = .2f; // initial delay, in seconds per square
	
	private final ASnake
			p1 = new ASnake(Keys.KEY_P1_LEFT, Keys.KEY_P1_RIGHT, Keys.KEY_P1_DOWN, Keys.KEY_P1_UP, Color.orange),
			p2 = new ASnake(Keys.KEY_P2_LEFT, Keys.KEY_P2_RIGHT, Keys.KEY_P2_DOWN, Keys.KEY_P2_UP, Color.cyan);
	private final ASnake[] snakes = { p1, p2 };

	private float inputCooldown;
	private int fruitX, fruitY;
	private ASnake deadSnake;
	private float deathAnimationCooldown;
	private float nextDeathAnimationCooldown;
	
	private static class Sounds {

		private Sound fruit = SoundManager.loadSound("/games/snake/fruit.wav");
		private Sound death = SoundManager.loadSound("/games/snake/death.wav");
		private Sound music  = SoundManager.loadSound("/games/snake/music.wav", new ClipSettings().setVolume(-10).setLooping(true));
		
	}
	
	private Sounds sounds;
	
	@Override
	public void start(Graphics graphics) {
		sounds = new Sounds();
		
		int S = 1+(W+1)*C;
		graphics.setSize(S, S);
		graphics.setEffect_CRT(10.f);
		
		sounds.music.play();
		reset();
	}

	@Override
	public void drawFrame(Graphics graphics) {
		graphics.clear();
		graphics.drawRect(0, 0, 1, 1+(W+1)*C, Color.darkGray);
		graphics.drawRect((W+1)*C, 0, 1, 1+(W+1)*C, Color.darkGray);
		graphics.drawRect(0, 0, 1+(W+1)*C, 1, Color.darkGray);
		graphics.drawRect(0, (W+1)*C, 1+(W+1)*C, 1, Color.darkGray);
		
		for(ASnake s : snakes)
			s.draw(graphics);
		
		graphics.drawRect(1+(W+1)*fruitX, 1+(W+1)*fruitY, W, W, Color.green);
	}

	@Override
	public void step(float realDelta) {
		if(deadSnake != null) {
			deathAnimationCooldown -= realDelta;
			if(deathAnimationCooldown < 0) {
				nextDeathAnimationCooldown *= .8f;
				deathAnimationCooldown += nextDeathAnimationCooldown;
				deadSnake.body.remove(0);
				deadSnake.body.remove(0);
				if(deadSnake.body.isEmpty())
					reset();
			}
		} else {
			for(ASnake s : snakes)
				s.step(realDelta);
		}
	}
	
	private boolean moveSnake(ASnake snake) {
		int newX = snake.body.get(snake.body.size()-2) + snake.dirX;
		int newY = snake.body.get(snake.body.size()-1) + snake.dirY;
		
		snake.body.add(newX);
		snake.body.add(newY);
		
		if(fruitX == newX && fruitY == newY) {
			updateInputCooldown();
			spawnFruit();
			sounds.fruit.play();
		} else {
			snake.body.remove(0);
			snake.body.remove(0);
		}
		
		if(isSnakeColliding(newX, newY, snake)) {
			// remove the snake's head
			snake.body.remove(snake.body.size()-1);
			snake.body.remove(snake.body.size()-1);
			return true;
		}
		
		return false;
	}
	
	private boolean isSnakeColliding(int newX, int newY, ASnake snake) {
		// check bounds collision
		if(newX < 0 || newX >= C || newY < 0 || newY >= C)
			return true;
		// check self collision
		for(int i = 0; i < snake.body.size()-2; i += 2) {
			if(newX == snake.body.get(i) && newY == snake.body.get(i+1))
				return true;
		}
		// check other collision
		ASnake other = snake == p1 ? p2 : p1;
		for(int i = 0; i < other.body.size(); i += 2) {
			if(newX == other.body.get(i) && newY == other.body.get(i+1))
				return true;
		}
		
		return false;
	}
	
	private void triggerSnakeDeath(ASnake deadSnake) {
		this.deadSnake = deadSnake;
		nextDeathAnimationCooldown = DEATH_ANIMATION_DURATION;
		deathAnimationCooldown = nextDeathAnimationCooldown;
		sounds.death.play();
	}
	
	private void reset() {
		deadSnake = null;
		
		for(ASnake snake : snakes) {
			snake.body.clear();
			snake.dirX = 0;
			snake.dirY = 0;
			snake.currentInputCooldown = 0;
		}
		for(int i = 0; i < 3; i++) {
			p1.body.add(C/2-2); p1.body.add(C/2);
			p2.body.add(C/2+2); p2.body.add(C/2);
		}
		for(ASnake snake : snakes)
			snake.updateEyePos(0, 1);
		spawnFruit();
		updateInputCooldown();
	}
	
	private void spawnFruit() {
		do {
			fruitX = (int) (Math.random()*C);
			fruitY = (int) (Math.random()*C);
		} while(anySnakeCoversFruit());
	}
	
	private boolean anySnakeCoversFruit() {
		for(ASnake snake : snakes) {
			for(int i = 0; i < snake.body.size(); i += 2) {
				if(snake.body.get(i) == fruitX && snake.body.get(i+1) == fruitY) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void updateInputCooldown() {
		inputCooldown = INITIAL_GAME_SPEED / (1 + (p1.body.size()+p2.body.size())/50.f);
	}
	
	private class ASnake {
		
		private final List<Integer> body = new ArrayList<>(); // body (xy pairs), the body's tail is at the begining of the list
		private int dirX, dirY;
		private float e1x, e1y, e2x, e2y, e1tx, e1ty, e2tx, e2ty; // eyes positions and target positions
		private float currentInputCooldown;
		
		private final int keyLeft, keyRight, keyDown, keyUp;
		private final Color color;
		
		ASnake(int keyLeft, int keyRight, int keyDown, int keyUp, Color color) {
			this.keyLeft = keyLeft;
			this.keyRight = keyRight;
			this.keyDown = keyDown;
			this.keyUp = keyUp;
			this.color = color;
		}
	
		private void draw(Graphics graphics) {
			Color color = deadSnake == this ? Color.red : this.color;
			for(int i = 0; i < body.size(); i += 2)
				graphics.drawRect(1+(W+1)*body.get(i), 1+(W+1)*body.get(i+1), W, W, color);
	
			e1x = e1x + (e1tx-e1x)*.3f;
			e1y = e1y + (e1ty-e1y)*.3f;
			e2x = e2x + (e2tx-e2x)*.3f;
			e2y = e2y + (e2ty-e2y)*.3f;
			graphics.drawRect((int)e1x, (int)e1y, EYE_SIZE, EYE_SIZE, Color.white);
			graphics.drawRect((int)e2x, (int)e2y, EYE_SIZE, EYE_SIZE, Color.white);
		}
		
		private void step(float realDelta) {
			int previousDirX = body.get(body.size()-2) - body.get(body.size()-4);
			int previousDirY = body.get(body.size()-1) - body.get(body.size()-3);
			
			if(Keys.isKeyDown(keyDown) && previousDirY != +1) {
				dirX = 0; dirY = -1;
			} else if(Keys.isKeyDown(keyUp) && previousDirY != -1) {
				dirX = 0; dirY = +1;
			} else if(Keys.isKeyDown(keyLeft) && previousDirX != +1) {
				dirX = -1; dirY = 0;
			} else if(Keys.isKeyDown(keyRight) && previousDirX != -1) {
				dirX = +1; dirY = 0;
			}
			
			if(dirX == 0 && dirY == 0)
				return;
			
			currentInputCooldown -= realDelta;
			while(currentInputCooldown < 0) {
				currentInputCooldown += inputCooldown;
				if(moveSnake(this)) {
					triggerSnakeDeath(this);
					return;
				}
			}
			
			updateEyePos(dirX, dirY);
		}
		
		private void updateEyePos(int dirX, int dirY) {
			int lx = 1+(W+1)*body.get(body.size()-2);
			int ly = 1+(W+1)*body.get(body.size()-1);
			int dx = dirY;
			int dy = -dirX;
			e1tx = lx + W*.5f + W*.3f*(dirX + dx) - EYE_SIZE*.5f; e1ty = ly + W*.5f + W*.3f*(dirY + dy) - EYE_SIZE*.5f;
			e2tx = lx + W*.5f + W*.3f*(dirX - dx) - EYE_SIZE*.5f; e2ty = ly + W*.5f + W*.3f*(dirY - dy) - EYE_SIZE*.5f;
		}
		
	}
	
}
