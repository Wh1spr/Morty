package wh1spr.bot.morty;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import wh1spr.bot.dummy.AutoEventHandlerDummy;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.dummy.MRoles;

public class AutoEventHandlerMorty extends AutoEventHandlerDummy {

	public AutoEventHandlerMorty(Bot bot) {
		super(bot);
	}

	private final String welcome = "433718059254153216";
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		
		if (event.getGuild().getId().equals(Bot.MAELSTROM) && !event.getUser().isBot() && !event.getUser().isFake()) {
			event.getGuild().getTextChannelById(welcome).sendMessage("**Please welcome "+ event.getUser().getAsMention() +" to the server!**").queue();
			event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(MRoles.GUEST)).queue();
		}
		
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		if (event.getGuild().getId().equals(Bot.MAELSTROM)) {
			event.getGuild().getTextChannelById(welcome).sendMessage("**Goodbye, " + event.getUser().getAsMention() + "**").queue();
			
			// Take out of introduction, economy in database
		}
	}
	
	
	// This will be put in IntroductionCommand, adds eventlistener to given bot
//	@Override
//	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
//		if (!event.getGuild().getId().equals(C.GUILD)) return;
//		
//		if (event.getChannel().getId().equals(C.CHANNEL_INTRODUCTION)) {
//			if (!Database.hasIntroduction(event.getAuthor())) {
//				Database.putIntroduction(event.getAuthor(), event.getMessage());
//			} else {
//				PrivateChannel channel;
//				try {
//					if (event.getAuthor().isBot()) {
//						String origMsgId = Database.getIntroductionId(event.getAuthor());
//						event.getChannel().deleteMessageById(origMsgId).queue();
//						Database.putIntroduction(event.getAuthor(), event.getMessage());
//					} else {
//						channel = event.getAuthor().openPrivateChannel().complete(true);
//						
//						channel.sendMessage(":x: You can not have more than one message in introduction.").queue();
//						
//						String origMsgId = Database.getIntroductionId(event.getAuthor());
//						
//						channel.sendMessage("Here is your original message: \n```" + event.getJDA().getTextChannelById(C.CHANNEL_INTRODUCTION).getMessageById(origMsgId).complete().getContentDisplay() + "```").queue();
//						channel.sendMessage("Here is your new message: \n```" + event.getMessage().getContentDisplay() + "```").queue();
//						channel.sendMessage("Your original message has been deleted.").queue();
//						
//						event.getChannel().deleteMessageById(origMsgId).queue();
//						Database.putIntroduction(event.getAuthor(), event.getMessage());
//					}
//				} catch (RateLimitedException e) {/*can't happen*/}
//			}
//		}
//	}
//	
//	@Override
//	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
//		if (!event.getGuild().getId().equals(C.GUILD)) return;
//		
//		if (event.getChannel().getId().equals(C.CHANNEL_INTRODUCTION)) {
//			User author = event.getJDA().getUserById(Database.getUserIdByIntroMsgId(event.getMessageId()));
//			if (author == null) return;
//			Database.removeIntroduction(author);
//			author.openPrivateChannel().complete().sendMessage(":x: Your introduction has been removed.").queue();
//		}
//	}
}
