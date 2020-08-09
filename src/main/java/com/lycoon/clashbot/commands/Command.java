package com.lycoon.clashbot.commands;

public enum Command
{
	PLAYER ("player"),
	WARLEAGUE("warleague"),
	WAR("war"),
	LANG ("lang"),
	INFO ("info");
	
	private String name;
	private Command(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return name;
	}
}
