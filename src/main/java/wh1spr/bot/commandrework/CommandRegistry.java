package wh1spr.bot.commandrework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class CommandRegistry {
	
	private static Logger log = LoggerCache.getLogger("REGISTRY");
	
	public CommandRegistry() {
		log.info("New CommandRegistry created");
	}
	
	protected HashMap<String, Command> nameregistry = new HashMap<>();
	protected HashMap<String, Command> idregistry = new HashMap<>();
	// command ID, default permission
	protected HashMap<String, Permission> defaultPerms = new HashMap<String, Permission>();
	// role id, (command Id, y/n)
	protected HashMap<Long, HashMap<String, Boolean>> rolePermOverrides = new HashMap<Long, HashMap<String, Boolean>>();
	
	public void registerCommand(Command cmd) {
		if (idregistry.containsKey(cmd.getId())) 
			throw new IllegalArgumentException(String.format("Command ID '%s' is already in use by '%s'",cmd.getId(), idregistry.get(cmd.getId()).getName()));
		if (nameregistry.containsKey(cmd.getName()))
			throw new IllegalArgumentException(String.format("Command Name '%s' is already in use by '%s'", cmd.getName(), nameregistry.get(cmd.getName()).getId()));
		if (nameregistry.containsKey(cmd.getId()) || idregistry.containsKey(cmd.getName()))
			throw new IllegalArgumentException(String.format("Command %s is in name/id conflict with another command.", cmd.getId()));
		
		idregistry.put(cmd.getId(), cmd);
		nameregistry.put(cmd.getName().toLowerCase(), cmd);
		defaultPerms.put(cmd.getId(), cmd.getPermission());
		log.info(String.format("Registered command %s as '%s'", cmd.getId(), cmd.getName()));
		
		for (String alias : cmd.aliases) {
			if (nameregistry.containsKey(alias.toLowerCase())) log.warning(String.format("Alias '%s' is already in use by '%s'", alias, nameregistry.get(alias).getName()));
			else {
				log.debug(String.format("Registered command alias '%s' for %s", alias, cmd.getId()));
				nameregistry.put(alias, cmd);
			}
		}
	}
	
	public Command getCommand(String nameOrId) {
		if (nameregistry.containsKey(nameOrId)) return nameregistry.get(nameOrId);
		else if (idregistry.containsKey(nameOrId)) return idregistry.get(nameOrId);
		else return null;
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
    
    //TODO if isdeveloper => MongoUser
    public boolean canUse(String cmdNameOrId, Member m) {
    	//isdev => true
    	Iterator<Role> iter = m.getRoles().iterator();
    	while(iter.hasNext()) {
    		Role r = iter.next();
    		if (canUse(cmdNameOrId, r)) return true;
    	}
    	return false;
    }
    public boolean canUse(String cmdNameOrId, Role r) {
    	if (rolePermOverrides.containsKey(r.getIdLong())) {
    		HashMap<String, Boolean> overrides = rolePermOverrides.get(r.getIdLong());
    		if (overrides.containsKey(cmdNameOrId)) {
    			return overrides.get(cmdNameOrId);
    		}
    	}
    	return r.hasPermission(getCommand(cmdNameOrId).getPermission());
    }
    public boolean canUse(String cmdNameOrId, User u) {
    	if (getCommand(cmdNameOrId).isGuildOnly()) return false;
    	//isdev => false
    	return true;
    }
    
    public void registerPermissionOverrides() {
    	//TODO get from database
    }
    
    /**
     * Sets wether or not a role with roleId can use command with commandId.
     * @param role Role to set permission for
     * @param commandId Command ID of the command
     * @param canUse Wether or not role can use ID
     * @param setDB Wether or not to push change to database
     * @return Wether or not something has been changed in memory or in database.
     */
    public boolean setPermission(Role role, String commandId, boolean canUse, boolean setDB) {
    	if (canUse(commandId, role)) return false; //can already use so no override needed.
    	if (this.rolePermOverrides.containsKey(role.getIdLong())) {
    		this.rolePermOverrides.put(role.getIdLong(), new HashMap<String, Boolean>());
    	}
    	if (this.idregistry.containsKey(commandId)) {
    		this.rolePermOverrides.get(role.getIdLong()).put(commandId, canUse);
    		if (setDB) return true; //TODO push to db
        	return true;
    	} else {
    		return false;
    	}
    }
    /**
     * @see CommandRegistry#setPermission(Role, String, boolean, boolean)
     */
    public boolean setPermission(Role role, String commandId, boolean canUse) {
    	return setPermission(role, commandId, canUse, false);
    }
	

}
