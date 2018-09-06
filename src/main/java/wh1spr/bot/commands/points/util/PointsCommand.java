package wh1spr.bot.commands.points.util;

import wh1spr.bot.command.Command;

public abstract class PointsCommand extends Command {

	public PointsCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, aliases);
		if (tm == null) throw new IllegalArgumentException("PointTypeManager tm cannot be null.");
		this.tm = tm;
		this.setMaelstromOnly(false);
	}

	private PointTypeManager tm = null;
	protected PointTypeManager getTypeManager() {
		return this.tm;
	}
}
