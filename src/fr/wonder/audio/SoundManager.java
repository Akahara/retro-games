package fr.wonder.audio;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {
	
	private static final ClipSettings defaultSettings = new ClipSettings();
	
	public static Sound loadSound(String path) {
		return loadSound(path, defaultSettings);
	}
		
	public static Sound loadSound(String path, ClipSettings settings) {
		try {
			return new Sound(loadClip(path, settings));
		} catch (IOException e) {
			System.err.println("Could not load sound");
			e.printStackTrace();
			return new NoSound();
		}
	}
	
	private static Clip loadClip(String path, ClipSettings settings) throws IOException {
		try (BufferedInputStream is = new BufferedInputStream(SoundManager.class.getResourceAsStream(path));
			 AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(is)) {
			Clip audioClip = AudioSystem.getClip();
			audioClip.open(audioInputStream);
			
			if(settings.volume != 0) {
				FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(settings.volume);
			}

			if(settings.looping) {
				audioClip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			
			return audioClip;
		} catch (UnsupportedAudioFileException | LineUnavailableException | IllegalArgumentException e) {
			throw new IOException("Cannot load a sound", e);
		}
	}
	
}

class NoSound extends Sound {
	
	public NoSound() {
		super(null);
	}
	
	@Override
	public void play() {}
	
}
