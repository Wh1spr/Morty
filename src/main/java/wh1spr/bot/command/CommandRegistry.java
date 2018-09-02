package wh1spr.bot.command;

import java.util.HashMap;
import java.util.Set;

import wh1spr.bot.dummy.Bot;

public  class CommandRegistry {
	
	protected final Bot bot;
	
	public CommandRegistry(Bot bot) {
		this.bot = bot;
	}
	
	protected HashMap<String, CommandEntry> registry = new HashMap<>();

    public void registerCommand(Command command) {
        CommandEntry entry = new CommandEntry(command);
        registry.put(command.getName().toLowerCase(), entry);
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