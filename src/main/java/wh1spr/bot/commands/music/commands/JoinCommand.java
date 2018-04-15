package wh1spr.bot.commands.music.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import wh1spr.bot.commands.music.GuildMusicManager;
import wh1spr.bot.commands.music.Music;
import wh1spr.bot.dummy.Perm;

public class JoinCommand extends AudioCommand {

	public JoinCommand(Music m, String name, String... aliases) {
		super(m, name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, guild.getMember(invoker))) {return;}
		
		VoiceChannel vchannel;
		
		GuildMusicManager mng = getMusic().getGuildM(guild);
		
		if (mng.player.getPlayingTrack() != null || !mng.scheduler.queue.isEmpty()) {
			channel.sendMessage("I'm still playing in another channel, I can't switch right now!").queue();
			return;
		} else if (args.size() == 0) {
			vchannel = guild.getVoiceChannels().get(0);
		} else {
			String name = message.getContentStripped().split(" ", 2)[1];
			vchannel = guild.getVoiceChannelsByName(name, true).get(0);
		}
		if (vchannel == null) {
			channel.sendMessage("There is no channel with name ```" + message.getContentStripped().split(" ", 2)[1] + "```").queue();
		}
        guild.getAudioManager().setSendingHandler(mng.getSendHandler());
        guild.getAudioManager().openAudioConnection(vchannel); 
		
	}

}
