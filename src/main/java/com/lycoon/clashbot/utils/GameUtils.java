package com.lycoon.clashbot.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.lycoon.clashapi.cocmodels.player.Troop;

public class GameUtils
{
	public static String getCurrentSeason(Locale lang)
	{
		ZonedDateTime utcDateZoned = ZonedDateTime.now(ZoneId.of("Etc/UTC"));
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MMMM YYYY").withLocale(lang);
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
