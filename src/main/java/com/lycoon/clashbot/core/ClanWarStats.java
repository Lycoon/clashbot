package com.lycoon.clashbot.core;

import com.lycoon.clashapi.cocmodels.clanwar.ClanWarModel;

public class ClanWarStats
{
	int stars;
	double destructionPercentage;
	ClanWarModel clan;
	
	public ClanWarStats(ClanWarModel clan)
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
	
	public ClanWarModel getClan()
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
