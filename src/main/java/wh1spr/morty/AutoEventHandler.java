package wh1spr.morty;

import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

//Handles all automatic events, such as updating the database when someone joins
public class AutoEventHandler extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (!event.getGuild().getId().equals(C.GUILD)) return;
		if (!event.getUser().isBot()) {
			event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(C.ROLE_GUEST)).queue();
			event.getGuild().getTextChannelById(C.CHANNEL_WELCOME).sendMessage("**Please welcome "+ event.getUser().getAsMention() +" to the server!**").queue();
		} else {
			event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(C.ROLE_BOTS)).queue();
		}
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		//remove from database
		if (!event.getGuild().getId().equals(C.GUILD)) return;
		event.getGuild().getTextChannelById(C.CHANNEL_INTRODUCTION).deleteMessageById(Database.getIntroductionId(event.getUser())).complete();
		Database.remove(event.getUser());
		event.getGuild().getTextChannelById(C.CHANNEL_WELCOME).sendMessage("**Goodbye, " + event.getUser().getAsMention() + "**").queue();
	}
	
	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		if (!event.getGuild().getId().equals(C.GUILD)) return;
		List<Role> roles = event.getRoles();
		for (Role role : roles) {
			switch (role.getId()) {
			case C.ROLE_FIRST:
			case C.ROLE_SECOND:
			case C.ROLE_THIRD:
			case C.ROLE_MINOR_VERBREDING:
			case C.ROLE_MINOR_BAI:
				Permission.givePerm(Permission.MEMBER, event.getUser());
				break;
			case C.ROLE_GRAD:
				Permission.givePerm(Permission.GRADUATED, event.getUser());
				break;
			case C.ROLE_WINA:
				Permission.givePerm(Permission.WINA, event.getUser());
				break;
			case C.ROLE_GUEST:
				Permission.givePerm(Permission.GUEST, event.getUser());
				break;
			case C.ROLE_BOTS:
				Permission.givePerm(Permission.BOT, event.getUser());
				break;
			case C.ROLE_GOLDEN_BOT:
				Permission.givePerm(Permission.GOLDEN_BOT, event.getUser());
				break;
			case C.ROLE_ADMIN:
				Permission.givePerm(Permission.ADMIN, event.getUser());
				break;
			}
		}
	}
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		if (!event.getGuild().getId().equals(C.GUILD)) return;
		List<Role> roles = event.getRoles();
		for (Role role : roles) {
			switch (role.getId()) {
			case C.ROLE_FIRST:
			case C.ROLE_SECOND:
			case C.ROLE_THIRD:
			case C.ROLE_MINOR_VERBREDING:
			case C.ROLE_MINOR_BAI:
				Permission.removePerm(Permission.MEMBER, event.getUser());
				break;
			case C.ROLE_GRAD:
				Permission.removePerm(Permission.GRADUATED, event.getUser());
				break;
			case C.ROLE_WINA:
				Permission.removePerm(Permission.WINA, event.getUser());
				break;
			case C.ROLE_GUEST:
				Permission.removePerm(Permission.GUEST, event.getUser());
				break;
			case C.ROLE_BOTS:
				Permission.removePerm(Permission.BOT, event.getUser());
				break;
			case C.ROLE_GOLDEN_BOT:
				Permission.removePerm(Permission.GOLDEN_BOT, event.getUser());
				break;
			case C.ROLE_ADMIN:
				Permission.removePerm(Permission.ADMIN, event.getUser());
				break;
			}
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (!event.getGuild().getId().equals(C.GUILD)) return;
		
		if (event.getChannel().getId().equals(C.CHANNEL_INTRODUCTION)) {
			if (!Database.hasIntroduction(event.getAuthor())) {
				Database.putIntroduction(event.getAuthor(), event.getMessage());
			} else {
				PrivateChannel channel;
				try {
					if (event.getAuthor().isBot()) {
						String origMsgId = Database.getIntroductionId(event.getAuthor());
						event.getChannel().deleteMessageById(origMsgId).queue();
						Database.putIntroduction(event.getAuthor(), event.getMessage());
					} else {
						channel = event.getAuthor().openPrivateChannel().complete(true);
						
						channel.sendMessage(":x: You can not have more than one message in introduction.").queue();
						
						String origMsgId = Database.getIntroductionId(event.getAuthor());
						
						channel.sendMessage("Here is your original message: \n```" + event.getJDA().getTextChannelById(C.CHANNEL_INTRODUCTION).getMessageById(origMsgId).complete().getContent() + "```").queue();
						channel.sendMessage("Here is your new message: \n```" + event.getMessage().getContent() + "```").queue();
						channel.sendMessage("Your original message has been deleted.").queue();
						
						event.getChannel().deleteMessageById(origMsgId).queue();
						Database.putIntroduction(event.getAuthor(), event.getMessage());
					}
				} catch (RateLimitedException e) {/*can't happen*/}
			}
		}
	}
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		if (!event.getGuild().getId().equals(C.GUILD)) return;
		
		if (event.getChannel().getId().equals(C.CHANNEL_INTRODUCTION)) {
			User author = event.getJDA().getUserById(Database.getUserIdByIntroMsgId(event.getMessageId()));
			if (author == null) return;
			Database.removeIntroduction(author);
			author.openPrivateChannel().complete().sendMessage(":x: Your introduction has been removed.").queue();
		}
	}
	
	@Override
	public void onUserNameUpdate(UserNameUpdateEvent event) {
		Database.updateNameFromUser(event.getUser());
	}
	
}
