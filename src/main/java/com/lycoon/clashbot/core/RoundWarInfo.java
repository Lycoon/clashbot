package com.lycoon.clashbot.core;

import java.util.ArrayList;
import java.util.List;

import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;

public class RoundWarInfo
{
	List<WarInfo> wars = new ArrayList<WarInfo>();
	
	public List<WarInfo> getWars()
	{
		return wars;
	}
	
	public void setWars(List<WarInfo> wars)
	{
		this.wars = wars;
	}
	
	public void addWarInfo(WarInfo warInfo)
	{
		wars.add(warInfo);
	}
}
