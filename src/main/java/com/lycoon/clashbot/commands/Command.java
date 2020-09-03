package com.lycoon.clashbot.commands;

public enum Command
{
	CLEAR     (CommandCategory.GENERAL, "clear", "cmd.clear.desc"),
	LANG      (CommandCategory.GENERAL, "lang", "cmd.lang.desc"),
	INFO      (CommandCategory.GENERAL, "info", "cmd.info.desc"),
	HELP      (CommandCategory.GENERAL, "help", "cmd.help.desc"),
	SET       (CommandCategory.GENERAL, "set", "cmd.set.desc", "<clan|player|lang> <value>"),
	WARLEAGUE (CommandCategory.CLAN, "warleague", "cmd.warleague.desc", "[clanTag]"),
	WARLOG    (CommandCategory.CLAN, "warlog", "cmd.warlog.desc", "[clanTag]"),
	WAR       (CommandCategory.CLAN, "war", "cmd.war.desc", "[clanTag]"),
	PLAYER    (CommandCategory.PLAYER, "player", "cmd.player.desc", "[playerTag]");
	
	public static final String PREFIX = "!";
	
	private CommandCategory category;
	private String name, desc, usage;
	
	private Command(CommandCategory category, String name, String desc, String usage)
	{
		this.category = category;
		this.name = name;
		this.desc = desc;
		this.usage = usage;
	}
	
	private Command(CommandCategory category, String name, String desc)
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
	
	public String getUsage()
	{
		return usage;
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
