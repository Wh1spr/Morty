package wh1spr.bot.commandrework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.Mongo;
import wh1spr.bot.mongodb.MongoUser;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

import static com.mongodb.client.model.Filters.*;

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
	
	/**
	 * Registers a command in this {@link CommandRegistry}
	 * @param cmd The {@link Command} to register
	 * @throws IllegalArgumentException If the given command has conflicting Id/Name values with other commands
	 */
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
		//todo role perms and stuff
	}
	
	/**
	 * Returns the command with given name or id value
	 * @param nameOrId A name, alias, or ID to identify the command
	 * @return command with given name or id value
	 */
	public Command getCommand(String nameOrId) {
		if (nameregistry.containsKey(nameOrId)) return nameregistry.get(nameOrId);
		else if (idregistry.containsKey(nameOrId)) return idregistry.get(nameOrId);
		else return null;
    }

	/**
	 * @return The number of registered commands.
	 */
    public int getNrCommands() {
        return idregistry.size();
    }
    
    /**
     * @return The number of registered commands and aliases.
     */
    public int getNrAliasesAndNames() {
    	return nameregistry.size();
    }

    /**
     * @return A set of registered commands and aliases.
     */
    public Set<String> getRegisteredCommandsAndAliases() {
        return nameregistry.keySet();
    }
    
    /**
     * @return A set of uniquely registered command Ids
     */
    public Set<String> getRegisteredCommandIds() {
    	return idregistry.keySet();
    }
    
    /**
     * @param cmdNameOrId Name, alias or id of the command
     * @param m Member to check
     * @return Wether or not this Member can use the command with given name or id
     */
    public boolean canUse(String cmdNameOrId, Member m) {
    	if (new MongoUser(m.getUser()).isBotBanned()) return false;
    	if (new MongoUser(m.getUser()).isDev()) return true;
    	if (getCommand(cmdNameOrId).getPermission()==null) return false;
    	Iterator<Role> iter = m.getRoles().iterator();
    	while(iter.hasNext()) {
    		Role r = iter.next();
    		if (canUse(cmdNameOrId, r)) return true;
    	}
    	return false;
    }
    /**
     * @param cmdNameOrId Name, alias or id of the command
     * @param r Role to check
     * @return Wether or not Members that have the given Role can use the command with given name or id
     */
    public boolean canUse(String cmdNameOrId, Role r) {
    	if (getCommand(cmdNameOrId).getPermission()==null) return false; //no role has DEV
    	if (rolePermOverrides.containsKey(r.getIdLong())) {
    		HashMap<String, Boolean> overrides = rolePermOverrides.get(r.getIdLong());
    		if (overrides.containsKey(cmdNameOrId)) {
    			return overrides.get(cmdNameOrId);
    		}
    	}
    	return r.hasPermission(getCommand(cmdNameOrId).getPermission());
    }
    /**
     * @param cmdNameOrId Name, alias or id of the command
     * @param u User to check
     * @return Wether or not the given User can use the command with geiven name or id
     */
    public boolean canUse(String cmdNameOrId, User u) {
    	if (getCommand(cmdNameOrId).isGuildOnly()) return false;
    	if (new MongoUser(u).isDev()) return true;
    	if (getCommand(cmdNameOrId).getPermission()==null) return false;
    	return true;
    }
    
    @SuppressWarnings("unchecked")
	public void registerPermissionOverrides(JDA jda) {
    	Iterator<Document> iter = Mongo.getDb().getCollection("guilds").find(exists("roles")).iterator();
    	while(iter.hasNext()) {
    		List<Document> roles = (List<Document>) iter.next().get("roles");
    		roles.forEach(el->{
    			long id = el.getLong("id");
    			if (jda.getRoleById(id)==null) {
    				log.warning(String.format("There is a role with id '%d', but JDA can't find it!"));
    			} else {
    				String perms = el.getString("permoverrides");
        			//parse string
        			for (String p : perms.split(":")) {
        				boolean canuse = false;
        				if (p.startsWith("+")) {
        					canuse = true;
        				} else if (p.startsWith("-")) { //nothing, it's already false
        				} else {log.warning("Wrongly formatted permission found! '" + p + "' in role with id '" + id + "'"); continue;}
        				
        				if (idregistry.containsKey(p.substring(1))) {
        					setPermission(jda.getRoleById(id), p.substring(1), canuse);
        				} else {
        					log.warning(String.format("Role with id '%d' has a command with id '%s', but it was never registered!",
        							id, p.substring(1)));
        				}
        			}
    			}
    		});
    	}
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
    	if (canUse(commandId, role) && canUse) return false; //can already use so no override needed.
    	if (!canUse(commandId, role) && !canUse) return false; //can already not use, so no override needed.
    	if (!this.rolePermOverrides.containsKey(role.getIdLong())) {
    		this.rolePermOverrides.put(role.getIdLong(), new HashMap<String, Boolean>());
    	}
    	if (this.idregistry.containsKey(commandId)) {
    		this.rolePermOverrides.get(role.getIdLong()).put(commandId, canUse);
    		if (setDB) {
    			//TODO push to db
    		}
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
