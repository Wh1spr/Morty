package wh1spr.morty.command;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class Command {

	public Command(String name, String... aliases) {
		this.name = name;
		for (String alias : aliases) {
			this.aliases.add(alias);
		}
		this.commandInfo = new CommandInfo(this.name, "Undefined", "Undefined", this.aliases);
	}
	
	//Only CommandRegistry can use this, rest must use CommandInfo
	String getName() {
		return this.name;
	}
	List<String> getAliases() {
		return this.aliases;
	}
	
	protected String name = null;
	protected ArrayList<String> aliases = new ArrayList<String>();
	protected CommandInfo commandInfo = null;
	
	public abstract void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args);
	
	public CommandInfo getCommandInfo(){
        return commandInfo;
    }

    public static class CommandInfo {

        public String name;
        public String desc;
        public String usage;
        public ArrayList<String> aliases;

        public CommandInfo(String name, String desc, String usage, ArrayList<String> aliases) {
            this.name = name;
            this.desc = desc;
            this.usage = usage;
            this.aliases = aliases;
        }

    }
}

/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
