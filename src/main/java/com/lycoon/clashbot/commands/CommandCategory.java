package com.lycoon.clashbot.commands;

public enum CommandCategory
{
	GENERAL ("category.general"),
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
