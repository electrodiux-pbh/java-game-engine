package com.gameengine.audio;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.libc.LibCStdlib;

public class Sound {

	private int bufferId = -1;
	private int sourceId = -1;
	private String filePath = null;
	
	public Sound() { }
	
	public Sound(@NotNull String filePath, boolean loops) throws AudioException {
		
		this.filePath = filePath;
		
		MemoryStack.stackPush();
		IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);
		MemoryStack.stackPush();
		IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);
		
		ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(filePath, channelsBuffer, sampleRateBuffer);
		
		if(rawAudioBuffer == null) {
			MemoryStack.stackPop();
			MemoryStack.stackPop();
			throw new AudioException("Could not load sound '" + filePath + "'");
		}
		
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		
		MemoryStack.stackPop();
		MemoryStack.stackPop();
		
		int format = -1;
		
		switch(channels) {
		case 1:
			format = AL10.AL_FORMAT_MONO16;
			break;
		case 2:
			format = AL10.AL_FORMAT_STEREO16;
			break;
		}
		
		bufferId = AL10.alGenBuffers();
		AL10.alBufferData(bufferId, format, rawAudioBuffer, sampleRate);
		
		sourceId = AL10.alGenSources();
		
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loops ? 1 : 0);
		AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.3F);
		
		LibCStdlib.free(rawAudioBuffer);
		
	}
	
	public void delete() {
		AL10.alDeleteSources(sourceId);
		AL10.alDeleteBuffers(bufferId);
	}
	
	public void play() {
		play(true);
	}
	
	public void play(boolean restart) {
		if(!isPlaying()) {
			if(restart)
				setAudioPosition(0);
			AL10.alSourcePlay(sourceId);
		}
	}
	
	public void stop() {
		if(isPlaying()) {
			AL10.alSourceStop(sourceId);
		}
	}
	
	public void setAudioPosition(int position) {
		AL10.alSourcei(sourceId, AL10.AL_POSITION, position);
	}
	
	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
}
