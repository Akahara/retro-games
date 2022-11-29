package fr.wonder.audio;

public class ClipSettings {

	public float volume = 0;
	public float pitch = 0;
	public boolean looping = false;
	
	/** @param volume decibels to add/substract to the initial sound's volume */
	public ClipSettings setVolume(float volume) {
		this.volume = volume;
		return this;
	}
	
	public ClipSettings setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}
	
	public ClipSettings setLooping(boolean looping) {
		this.looping = looping;
		return this;
	}
	
}
