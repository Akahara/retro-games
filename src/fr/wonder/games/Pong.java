package fr.wonder.games;

import fr.wonder.Game;
import fr.wonder.Keys;
import fr.wonder.audio.ClipSettings;
import fr.wonder.audio.Sound;
import fr.wonder.audio.SoundManager;
import fr.wonder.display.Color;
import fr.wonder.display.Graphics;
import fr.wonder.display.SpriteSheet;

public class Pong extends Game {

	private static final float PI = (float)Math.PI;
	
	private static final int W = 300, H = 150;
	private static final int TEXT_W = 3*10/5, TEXT_H = 10;
	private static final int PADDLE_HEIGHT = 30;
	private static final int PADDLE_WIDTH = 8;
	private static final float PADDLE_SPEED = (H-PADDLE_HEIGHT)/.5f;
	private static final int BALL_SIZE = 3;
	private static final float INITIAL_BALL_SPEED = W/2.f;
	private static final float TERMINAL_BALL_SPEED = W/1.f;
	private static final float RESET_COOLDOWN = 1.f;
	
	private float inputCooldown;
	private float paddleLeft  = (H-PADDLE_HEIGHT)/2.f;  // in range 0..H-paddleSize
	private float paddleRight = (H-PADDLE_HEIGHT)/2.f; // in range 0..H-paddleSize
	private float ballX, ballY, ballVX, ballVY;
	private float ballSpeed;
	private int bounces = 0;
	private int scoreLeft = 0, scoreRight = 0;
	
	private class Sounds {

		private Sound bounce = SoundManager.loadSound("/games/pong/bounce.wav");
		private Sound wall   = SoundManager.loadSound("/games/pong/wall.wav");
		private Sound music  = SoundManager.loadSound("/games/pong/music.wav", new ClipSettings().setVolume(-10).setLooping(true));
		
	}
	
	private Sounds sounds;
	private SpriteSheet textSprites;
	
	@Override
	public void start(Graphics graphics) {
		sounds = new Sounds();
		textSprites = graphics.loadSpriteSheet("/games/pong/text.png", 11*3, 5).setGrid(3, 5);
		
		graphics.setSize(W, H);
		graphics.setEffect_CRT(4.f);
		sounds.music.play();
		reset();
	}
	
	private void reset() {
		inputCooldown = RESET_COOLDOWN;
		ballX = (W-BALL_SIZE)/2.f;
		ballY = (H-BALL_SIZE)/2.f;
		ballVX = 0;
		ballVY = 0;
		ballSpeed = INITIAL_BALL_SPEED;
	}
	
	@Override
	public void drawFrame(Graphics graphics) {
		graphics.clear();
		
		int c = 30;
		int qs = H/c;
		for(int i = 0; i < c; i += 3) {
			graphics.drawRect((W-qs)/2, i*H/c, qs, qs, Color.white);
		}

		graphics.drawRect(0, (int)paddleLeft, PADDLE_WIDTH, PADDLE_HEIGHT, Color.white);
		graphics.drawRect(W-PADDLE_WIDTH, (int)paddleRight, PADDLE_WIDTH, PADDLE_HEIGHT, Color.white);
		graphics.drawRect((int)ballX, (int)ballY, BALL_SIZE, BALL_SIZE, Color.white);

		graphics.drawSprite(textSprites.getSprite(scoreLeft %10), W/2-qs-TEXT_W, H-TEXT_H, TEXT_W, TEXT_H);
		graphics.drawSprite(textSprites.getSprite(scoreRight%10), W/2+qs,        H-TEXT_H, TEXT_W, TEXT_H);
	}
	
	@Override
	public void step(float realDelta) {
		if(inputCooldown > 0) {
			inputCooldown -= realDelta;
			return;
		}
		
		if(ballVX == 0) {
			if(Keys.isAnyDown(Keys.KEY_P1_DOWN, Keys.KEY_P1_UP, Keys.KEY_P2_DOWN, Keys.KEY_P2_UP)) {
				float theta;
				float[] angles;
				angles = new float[] { PI/3, PI/4, PI/5, PI/6 };
				theta = angles[(int)(Math.random()*angles.length)];
				angles = new float[] { theta, -theta, PI-theta, PI+theta };
				theta = angles[(int)(Math.random()*angles.length)];
				ballVX = (float)Math.cos(theta);
				ballVY = (float)Math.sin(theta);
			} else {
				return;
			}
		}

		if(Keys.isKeyDown(Keys.KEY_P1_DOWN)) paddleLeft  -= realDelta * PADDLE_SPEED;
		if(Keys.isKeyDown(Keys.KEY_P1_UP))   paddleLeft  += realDelta * PADDLE_SPEED;
		if(Keys.isKeyDown(Keys.KEY_P2_DOWN)) paddleRight -= realDelta * PADDLE_SPEED;
		if(Keys.isKeyDown(Keys.KEY_P2_UP))   paddleRight += realDelta * PADDLE_SPEED;
		
		// clamp paddles positions
		if(paddleLeft  < 0) paddleLeft  = 0; else if(paddleLeft  > H-PADDLE_HEIGHT) paddleLeft  = H-PADDLE_HEIGHT;
		if(paddleRight < 0) paddleRight = 0; else if(paddleRight > H-PADDLE_HEIGHT) paddleRight = H-PADDLE_HEIGHT;

		ballX += realDelta * ballVX * ballSpeed;
		ballY += realDelta * ballVY * ballSpeed;
		// bounce left
		if(ballX < PADDLE_WIDTH && paddleLeft < ballY+BALL_SIZE && paddleLeft+PADDLE_HEIGHT > ballY) {
			ballX = PADDLE_WIDTH;
			ballVX = -ballVX;
			triggerBounce();
		}
		// bounce right
		if(ballX+BALL_SIZE > W-PADDLE_WIDTH && paddleRight < ballY+BALL_SIZE && paddleRight+PADDLE_HEIGHT > ballY) {
			ballX = W-BALL_SIZE-PADDLE_WIDTH;
			ballVX = -ballVX;
			triggerBounce();
		}
		
		// bounce bottom
		if(ballY < 0) {
			ballY = 0;
			ballVY = -ballVY;
			sounds.bounce.play();
		}
		// bounce top
		if(ballY+BALL_SIZE > H) {
			ballY = H-BALL_SIZE;
			ballVY = -ballVY;
			sounds.bounce.play();
		}

		// miss
		if(ballX < 0) {
			scoreRight++;
			reset();
		} else if(ballX+BALL_SIZE > W) {
			scoreLeft++;
			reset();
		}
	}
	
	private void triggerBounce() {
		bounces++;
		ballSpeed = INITIAL_BALL_SPEED + (TERMINAL_BALL_SPEED-INITIAL_BALL_SPEED) * (1-(float)Math.exp(-bounces/10.f));
		sounds.wall.play();
	}

}
