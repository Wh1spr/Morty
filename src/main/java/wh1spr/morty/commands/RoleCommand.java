package wh1spr.morty.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.managers.GuildController;
import wh1spr.morty.C;
import wh1spr.morty.command.Command;

// I know there's a whole lot of copy pasting here but honestly idc because it works as it's supposed to and it gives me more control over what exactly happens.
public class RoleCommand extends Command {

	public RoleCommand(String name, String... aliases) {
		super(name, aliases);
		this.commandInfo = new CommandInfo(this.name, "Manages a user's roles.", "", this.aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		// Anyone can use the base command.
		
		List<Role> roles = invoker.getRoles();
		GuildController cont = guild.getController();
		
		List<Role> toAdd = new ArrayList<>();
		List<Role> toRemove = new ArrayList<>();
		
		// .role add <phase/minor/graduated>
		if (args.size() == 2) {
			if (args.get(0).toLowerCase().equals("add")) {
				switch (args.get(1).toLowerCase()) {
				case "first":
					if (roles.contains(guild.getRoleById(C.ROLE_FIRST))) {
						channel.sendMessage(":x: You already have this role " + invoker.getAsMention() + ".").queue();
					} else if (roles.contains(guild.getRoleById(C.ROLE_SECOND)) || roles.contains(guild.getRoleById(C.ROLE_THIRD)) || roles.contains(guild.getRoleById(C.ROLE_GRAD))) {
						channel.sendMessage(":x: You already have a role higher than this one, "+invoker.getAsMention()+". If you need to change it, please remove it first").queue();
					} else {
						message.addReaction("✅").queue();
						toAdd.add(guild.getRoleById(C.ROLE_FIRST));
						toRemove.add(guild.getRoleById(C.ROLE_GUEST));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "second":
					if (roles.contains(guild.getRoleById(C.ROLE_SECOND))) {
						channel.sendMessage(":x: You already have this role " + invoker.getAsMention() + ".").queue();
					} else if (roles.contains(guild.getRoleById(C.ROLE_THIRD)) || roles.contains(guild.getRoleById(C.ROLE_GRAD))) {
						channel.sendMessage(":x: You already have a role higher than this one, "+invoker.getAsMention()+". If you need to change it, please remove it first.").queue();
					} else {
						message.addReaction("✅").queue();
						toAdd.add(guild.getRoleById(C.ROLE_SECOND));
						toRemove.add(guild.getRoleById(C.ROLE_GUEST));
						toRemove.add(guild.getRoleById(C.ROLE_FIRST));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "third":
					if (roles.contains(guild.getRoleById(C.ROLE_THIRD))) {
						channel.sendMessage(":x: You already have this role " + invoker.getAsMention() + ".").queue();
					} else if (roles.contains(guild.getRoleById(C.ROLE_GRAD))) {
						channel.sendMessage(":x: You already have a role higher than this one, "+invoker.getAsMention()+". If you need to change it, please remove it first.").queue();
					} else {
						message.addReaction("✅").queue();
						toAdd.add(guild.getRoleById(C.ROLE_THIRD));
						toRemove.add(guild.getRoleById(C.ROLE_GUEST));
						toRemove.add(guild.getRoleById(C.ROLE_FIRST));
						toRemove.add(guild.getRoleById(C.ROLE_SECOND));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "grad":
					if (roles.contains(guild.getRoleById(C.ROLE_GRAD))) {
						channel.sendMessage(":x: You already have this role " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toAdd.add(guild.getRoleById(C.ROLE_GRAD));
						toRemove.add(guild.getRoleById(C.ROLE_GUEST));
						toRemove.add(guild.getRoleById(C.ROLE_FIRST));
						toRemove.add(guild.getRoleById(C.ROLE_SECOND));
						toRemove.add(guild.getRoleById(C.ROLE_THIRD));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "verbreding":
					if (roles.contains(guild.getRoleById(C.ROLE_MINOR_VERBREDING))) {
						channel.sendMessage(":x: You already have this role " + invoker.getAsMention() + ".").queue();
					} else if (roles.contains(guild.getRoleById(C.ROLE_GUEST))) {
						channel.sendMessage(":x: Please assign a phase first, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toAdd.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "bai":
					if (roles.contains(guild.getRoleById(C.ROLE_MINOR_BAI))) {
						channel.sendMessage(":x: You already have this role " + invoker.getAsMention() + ".").queue();
					} else if (roles.contains(guild.getRoleById(C.ROLE_GUEST))) {
						channel.sendMessage(":x: Please assign a phase first, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toAdd.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				default:
					channel.sendMessage(":x: I do not recognize that role, " + invoker.getAsMention() + ". Usage can be found in #info.").queue();
					break;
				}
			} else if (args.get(0).toLowerCase().equals("remove")) {
				switch(args.get(1).toLowerCase()) {
				case "first":
					if (!roles.contains(guild.getRoleById(C.ROLE_FIRST))) {
						channel.sendMessage(":x: You don't have this role, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						toRemove.add(guild.getRoleById(C.ROLE_FIRST));
						
						String highestRoleId = roles.get(0).getId();
						if (highestRoleId != C.ROLE_ADMIN || highestRoleId != C.ROLE_MOD) {
							toAdd.add(guild.getRoleById(C.ROLE_GUEST));
						}
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "second":
					if (!roles.contains(guild.getRoleById(C.ROLE_SECOND))) {
						channel.sendMessage(":x: You don't have this role, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						toRemove.add(guild.getRoleById(C.ROLE_SECOND));
						
						String highestRoleId = roles.get(0).getId();
						if (highestRoleId != C.ROLE_ADMIN || highestRoleId != C.ROLE_MOD) {
							toAdd.add(guild.getRoleById(C.ROLE_GUEST));
						}
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "third":
					if (!roles.contains(guild.getRoleById(C.ROLE_THIRD))) {
						channel.sendMessage(":x: You don't have this role, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						toRemove.add(guild.getRoleById(C.ROLE_THIRD));
						
						String highestRoleId = roles.get(0).getId();
						if (highestRoleId != C.ROLE_ADMIN || highestRoleId != C.ROLE_MOD) {
							toAdd.add(guild.getRoleById(C.ROLE_GUEST));
						}
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "grad":
					if (!roles.contains(guild.getRoleById(C.ROLE_GRAD))) {
						channel.sendMessage(":x: You don't have this role, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toRemove.add(guild.getRoleById(C.ROLE_GRAD));
						
						String highestRoleId = roles.get(0).getId();
						if (highestRoleId != C.ROLE_ADMIN || highestRoleId != C.ROLE_MOD) {
							toAdd.add(guild.getRoleById(C.ROLE_GUEST));
						}
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "verbreding":
					if (!roles.contains(guild.getRoleById(C.ROLE_MINOR_VERBREDING))) {
						channel.sendMessage(":x: You don't have this role, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_VERBREDING));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				case "bai":
					if (!roles.contains(guild.getRoleById(C.ROLE_MINOR_BAI))) {
						channel.sendMessage(":x: You don't have this role, " + invoker.getAsMention() + ".").queue();
					} else {
						message.addReaction("✅").queue();
						toRemove.add(guild.getRoleById(C.ROLE_MINOR_BAI));
						cont.modifyMemberRoles(invoker, toAdd, toRemove).queue();
					}
					break;
				default:
					channel.sendMessage(":x: I do not recognize that role, " + invoker.getAsMention() + ". Usage can be found in #info.").queue();
					break;
				}
			}
		}
	}

}
