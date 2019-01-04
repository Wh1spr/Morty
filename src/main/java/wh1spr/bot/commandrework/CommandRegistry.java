package wh1spr.bot.commandrework;

import java.util.HashMap;
import java.util.Set;

import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class CommandRegistry {
	
	private Logger log = LoggerCache.getLogger("REGISTRY");
	
	public CommandRegistry() {
		log.info("New CommandRegistry created");
	}
	
	protected HashMap<String, Command> nameregistry = new HashMap<>();
	protected HashMap<String, Command> idregistry = new HashMap<>();
	
	public void registerCommand(Command cmd) {
		if (idregistry.get(cmd.getId()) != null) 
			throw new IllegalArgumentException(String.format("Command ID '%s' is already in use by '%s'",cmd.getId(), idregistry.get(cmd.getId()).getId()));
		if (nameregistry.get(cmd.getName()) != null)
			throw new IllegalArgumentException(String.format("Command Name '%s' is already in use by '%s'", cmd.getName(), nameregistry.get(cmd.getName()).getId()));
		
		idregistry.put(cmd.getId(), cmd);
		nameregistry.put(cmd.getName().toLowerCase(), cmd);
		log.info(String.format("Registered command %s as '%s'", cmd.getId(), cmd.getName()));
		
		for (String alias : cmd.aliases) {
			if (nameregistry.containsKey(alias.toLowerCase())) log.warning(String.format("Alias '%s' is already in use by '%s'", alias, nameregistry.get(alias).getName()));
			else {
				log.debug(String.format("Registered command alias '%s' for %s", alias, cmd.getId()));
				nameregistry.put(alias, cmd);
			}
		}
	}
	
	public Command getCommand(String name) {
        return nameregistry.get(name);
    }

    public int getNrCommands() {
        return idregistry.size();
    }
    
    public int getNrAliasesAndNames() {
    	return nameregistry.size();
    }

    public Set<String> getRegisteredCommandsAndAliases() {
        return nameregistry.keySet();
    }
    
    public Set<String> getRegisteredCommandIds() {
    	return idregistry.keySet();
    }
	

}
