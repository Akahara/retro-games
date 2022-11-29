package fr.wonder.audio;

import javax.sound.sampled.Clip;

public class Sound {

	private final Clip clip;
	
	Sound(Clip clip) {
		this.clip = clip;
	}
	
	public void play() {
		clip.setFramePosition(0);
		clip.start();
	}
	
}
