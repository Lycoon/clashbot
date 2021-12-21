package com.lycoon.clashbot.core;

import java.util.ArrayList;
import java.util.List;

import com.lycoon.clashapi.models.war.War;

public class RoundWarInfo
{
	List<War> wars = new ArrayList<War>();
	
	public List<War> getWars()
	{
		return wars;
	}
	
	public void setWars(List<War> wars)
	{
		this.wars = wars;
	}
	
	public void addWarInfo(War warInfo)
	{
		wars.add(warInfo);
	}
}
