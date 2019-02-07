# Guide to the reworked Command.java

## Available methods for Creation

- Constructor
`public Command(String id, Permission perm, String name, String... aliases)`
  - `id` is a string that uniquely identifies this command
  *e.g.: `economy.balance`, `dev.eval`,...*
  - `perm` is a standard Discord Permission as defined by JDA. For `DEV` perm, `perm` is `null`
  - `name` is the "main" way to call the command in Discord
  - `aliases` are zero or multiple aliases to call the command in Discord
- `protected setUsage(String)` - sets the usage string
  *e.g.: `PREFIXeval <nashorn code>`, `PREFIXban @user <reason>`*
- `protected setDescription(String)` - sets the description of the command as used by the help command.
- `protected setCategory(String)` - sets the category of the command as used by the help command. *Should be evident when given the ID. Default is "Misc".*
- `protected setGuildOnly(boolean)` - sets wether or not this command can be used in PM. *Default is false.*
- `protected setMaelstromOnly(boolean)` - sets wether or not this command can only be used in Maelstrom server. *Default is false*

## Available methods after Creation

- Getters
  `getId()`, `getPermission()`, `getName()`, `getAliases()`, `getUsage()`, `getDescription()`, `getCategory()`
- `enable()`,`disable()`
- `isGuildOnly()`, `isMaelstromOnly()`

## Command Calls
    abstract onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args)
  > Called when command is used in a Guild Channel

	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args)
  > Called when command is used in a Private Channel.
  > **Default is to call onCall() with guild set to `null`**
