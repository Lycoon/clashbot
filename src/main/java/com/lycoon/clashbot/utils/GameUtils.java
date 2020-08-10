package com.lycoon.clashbot.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lycoon.clashapi.cocmodels.player.Troop;
import com.lycoon.clashbot.lang.LangUtils;

public class GameUtils
{
	public static String getCurrentSeason()
	{
		ZonedDateTime utcDateZoned = ZonedDateTime.now(ZoneId.of("Etc/UTC"));
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MMMM YYYY").withLocale(LangUtils.currentLang);
		return utcDateZoned.format(pattern);
	}
	
	public static Troop getTroopByName(List<Troop> troops, String name)
	{
		for (int i=0; i < troops.size(); i++)
		{
			if (troops.get(i).getName().equals(name))
				return troops.get(i);
		}
		return null;
	}
}
