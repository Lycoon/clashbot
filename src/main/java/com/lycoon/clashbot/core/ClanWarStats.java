package com.lycoon.clashbot.core;

import com.lycoon.clashapi.models.war.WarClan;

public class ClanWarStats
{
	int stars;
	double destructionPercentage;
	WarClan clan;
	
	public ClanWarStats(WarClan clan)
	{
		stars = 0;
		destructionPercentage = 0;
		this.clan = clan;
	}
	
	public int getStars()
	{
		return stars;
	}
	
	public double getDestruction()
	{
		return destructionPercentage;
	}
	
	public WarClan getClan()
	{
		return clan;
	}
	
	public void addStars(int value)
	{
		stars += value;
	}
	
	public void addDestruction(double value)
	{
		destructionPercentage += value;
	}
}
