package wh1spr.bot.commands.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.TextChannel;

public class AudioScheduler extends AudioEventAdapter {
	
	private boolean repeating = false;
    final AudioPlayer player;
    public final Queue<AudioTrack> queue;
    AudioTrack lastTrack;
    final TextChannel channel;

    /**
     * @param player The audio player this scheduler uses
     */
    public AudioScheduler(AudioPlayer player, TextChannel channel) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<AudioTrack>();
        this.channel = channel;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
    	if (!player.startTrack(track, true)) {
    		queue.offer(track);
    	} else {
    		if (channel != null) if (player.getPlayingTrack() != null) channel.sendMessage("**Playing: ** " + player.getPlayingTrack().getInfo().title).queue();
    	}
    }

    public boolean noMessageOnNext = false;
    
    /**
     * Start the next track, stopping the current on if it is playing.
     */
    public void nextTrack() {
    	
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
        
        if (!noMessageOnNext) if (channel != null) if (player.getPlayingTrack() != null) channel.sendMessage("**Playing: ** " + player.getPlayingTrack().getInfo().title).queue();
        else noMessageOnNext = false;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext)
        {
            if (repeating)
                player.startTrack(lastTrack.makeClone(), false);
            else
                nextTrack();
        }

    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void shuffle() {
    	List<AudioTrack> tracks = new ArrayList<AudioTrack>();
    	while (!queue.isEmpty()) {
    		tracks.add(queue.poll());
    	}
    	Collections.shuffle(tracks);
    	
    	Iterator<AudioTrack> trackiterator = tracks.iterator();
    	while(trackiterator.hasNext()) {
    		queue.offer(trackiterator.next());
    	}
    }

}
