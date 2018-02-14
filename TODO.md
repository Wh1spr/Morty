# The Big Todo List
Basically everything I want to do for the bot, mostly features, overhauls, redos...

1. Make a logger. Log every event when it occurs, except messages of course. I mean kicks, bans, things that morty does (like adding a user to the database, updating the database, changing his name,...)
 * Dummy is in place

2. Make things more general, so Morty can be used in other servers easily. Database will expand because of this, mostly just guildId that's extra.

3. Make a useful `.help` command.

4. Make the current commands more elegant.

5. Redo the database, so it's easier to manipulate and obtain data from. (make the Database type instanced probably)

6. Redo permissions, again for more general use of Morty.

7. Make it possible to have multiple bots running, controlled partially by Morty and by a `.properties` file.
 * `.properties` file is in place.

8. Use external files to get critical, but confidential information, like the login keys
 * `.properties` file is in place.

9. Make guild-independent economy with custom major.minor names, values and operations.
 * The operations will be hardcoded in each bot specifically in a class `EconOperations.java` (doesn't exist yet).
 * custom commands will be used to add/remove/disable/edit operations
 * Still in the thought process, this could change.
