package com.lycoon.clashbot.commands;

public enum CommandCategory
{
	MISC ("category.misc"),
	SETTINGS ("category.settings"),
	CLAN    ("category.clan"),
	PLAYER  ("category.player");
	
	private final String name;
	
	CommandCategory(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return name;
	}
}
