package com.gameengine.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

public class AudioContext {

	private long audioContext;
	private long audioDevice;
	
	public void init() throws AudioException {
		String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		audioDevice = ALC10.alcOpenDevice(defaultDeviceName);
		
		int[] attributes = { 0 };
		audioContext = ALC10.alcCreateContext(audioDevice, attributes);
		ALC10.alcMakeContextCurrent(audioContext);
		
		ALCCapabilities aclCapabilities = ALC.createCapabilities(audioDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(aclCapabilities);
		
		if(!alCapabilities.OpenAL10) {
			throw new AudioException("Audio library not supported.");
		}
	}
	
	public void destroy() {
		ALC10.alcDestroyContext(audioContext);
		ALC10.alcCloseDevice(audioDevice);
	}

	public long getAudioDevice() {
		return audioDevice;
	}

	public long getAudioContext() {
		return audioContext;
	}
	
}
