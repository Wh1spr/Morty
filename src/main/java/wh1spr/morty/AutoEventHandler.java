package wh1spr.morty;

import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

//Handles all automatic events, such as updating the database when someone joins
public class AutoEventHandler extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
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
		Database.remove(event.getUser());
		event.getGuild().getTextChannelById(C.CHANNEL_WELCOME).sendMessage("**Goodbye, " + event.getUser().getAsMention() + "**").queue();
	}
	
	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
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
			case C.ROLE_MOD:
				Permission.givePerm(Permission.MODERATOR, event.getUser());
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
			case C.ROLE_MOD:
				Permission.removePerm(Permission.MODERATOR, event.getUser());
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
	
	public void setLink(boolean bool) {
		AutoEventHandler.link = bool;
	}
	public void setReverse(boolean bool) {
		AutoEventHandler.reverse = bool;
	}
	
	public static boolean link = true;
	public static boolean reverse = false;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getChannel().getId().equals("353934296559648791") && link && !event.getAuthor().getId().equals(C.SELF)) {
			String newMessage = "**" + event.getAuthor().getName() + "** - " + event.getMessage().getContent(); //INFORMATICA#SPAM -> MAELSTROM#YGGDRASIL
			event.getJDA().getTextChannelById("356539822799847425").sendMessage(newMessage).queue();
		} else if (event.getChannel().getId().equals("356539822799847425") && reverse && link && !event.getAuthor().getId().equals(C.SELF)) {
			String newMessage = "**" + event.getAuthor().getName() + "** - " + event.getMessage().getContent(); //INFORMATICA#SPAM <- MAELSTROM#YGGDRASIL
			event.getJDA().getTextChannelById("353934296559648791").sendMessage(newMessage).queue();
		}
	}
	
}
