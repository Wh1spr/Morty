package wh1spr.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioSender implements AudioSendHandler {
	
	private AudioPlayer audioPlayer;
	private AudioFrame lastFrame;
	
	public AudioSender(AudioPlayer player) {
		this.audioPlayer = player;
	}
	
	
	@Override
	public boolean canProvide() {
		if (lastFrame == null) lastFrame = audioPlayer.provide();
	  	return lastFrame != null;
	}
	
	@Override
	public byte[] provide20MsAudio() {
		if (lastFrame == null) return lastFrame.getData();
		byte[] data = lastFrame!=null ? lastFrame.getData() : null;
		lastFrame = null;
		return data;
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}
}
