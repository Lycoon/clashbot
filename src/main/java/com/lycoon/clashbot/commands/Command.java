package com.lycoon.clashbot.commands;

public enum Command
{
	INVITE    (CommandCategory.GENERAL,     "invite",    "cmd.invite.desc"),
	CLEAR     (CommandCategory.GENERAL,     "clear",     "cmd.clear.desc"),
	LANG      (CommandCategory.GENERAL,     "lang",      "cmd.lang.desc"),
	INFO      (CommandCategory.GENERAL,     "info",      "cmd.info.desc"),
	HELP      (CommandCategory.GENERAL,     "help",      "cmd.help.desc"),
	SETTAG    (CommandCategory.GENERAL,     "set",       "cmd.settag.desc",          "<clan|player> <tag>"),
	SETLANG   (CommandCategory.GENERAL,     "set",       "cmd.setlang.desc",         "lang <language>"),
	WARLEAGUE_ROUND (CommandCategory.CLAN,  "warleague", "cmd.warleague.round.desc", "round <index> [clanTag]"),
	WARLEAGUE_ALL (CommandCategory.CLAN,    "warleague", "cmd.warleague.all.desc",   "all [clanTag]"),
	WARLEAGUE_CLAN (CommandCategory.CLAN,   "warleague", "cmd.warleague.clan.desc",  "[clanTag]"),
	WARLOG    (CommandCategory.CLAN,        "warlog",    "cmd.warlog.desc",          "[clanTag]"),
	WAR       (CommandCategory.CLAN,        "war",       "cmd.war.desc",             "[clanTag]"),
	PLAYER    (CommandCategory.PLAYER,      "player",    "cmd.player.desc",          "[playerTag]");
	
	public static final String PREFIX = "!";
	
	final CommandCategory category;
	final String name, desc;
	String usage;
	
	Command(CommandCategory category, String name, String desc, String usage)
	{
		this.category = category;
		this.name = name;
		this.desc = desc;
		this.usage = usage;
	}
	
	Command(CommandCategory category, String name, String desc)
	{
		this.category = category;
		this.name = name;
		this.desc = desc;
	}
	
	public String toString()
	{
		return name;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	public CommandCategory getCategory()
	{
		return category;
	}
	
	public String formatCommand()
	{
		return PREFIX + name;
	}
	
	public String formatFullCommand()
	{
		return formatCommand() + (usage == null ? "" : " " + usage);
	}
}
