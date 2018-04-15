package wh1spr.bot.command;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.dummy.Bot;

public  class CommandRegistry {
	
	protected final Bot bot;
	
	public CommandRegistry(Bot bot) {
		this.bot = bot;
	}
	
	protected HashMap<String, CommandEntry> registry = new HashMap<>();

    public void registerCommand(Command command) {
        CommandEntry entry = new CommandEntry(command);
        registry.put(command.getName(), entry);
        for (String alias : command.getAliases()) {
            registry.put(alias, entry);
        }
    }

    public CommandEntry getCommand(String name) {
        return registry.get(name);
    }

    public int getSize() {
        return registry.size();
    }

    public Set<String> getRegisteredCommandsAndAliases() {
        return registry.keySet();
    }

    public void removeCommand(String name) {
        CommandEntry entry = new CommandEntry(new Command(name) {
            @Override
            public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args ) {
            	warning(message);
            }
            @Override
            public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args ) {
            	warning(message);
            }
        });
        
        //Disables all aliases AND you can disable it by using an alias.
        Command old = registry.get(name).command;
        List<String> aliases = old.getAliases();
        String cmdname = old.getName();
        
        bot.getCommandRegistry().registry.put(cmdname, entry);
        aliases.forEach(e->bot.getCommandRegistry().registry.put(e, entry));
    }

    public class CommandEntry {

        public Command command;
        public String name;

        CommandEntry(Command command) {
            this.command = command;
            this.name = command.getName();
        }

        public String getName() {
            return name;
        }

        public void setCommand(Command command) {
            this.command = command;
        }
    }
	
}

// I'm using a very similar method to this guy here, and I found it so useful I put this in here as well
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