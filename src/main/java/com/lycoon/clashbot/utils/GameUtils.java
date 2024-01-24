package com.lycoon.clashbot.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.lycoon.clashapi.models.player.Troop;
import com.lycoon.clashapi.models.player.enums.Village;

public class GameUtils
{
    public static int getPositive(float value) {
        return (int) (value * (value < 0 ? -1 : 1));
    }

    public static String getCurrentSeason(Locale lang)
    {
        ZonedDateTime utcDateZoned = ZonedDateTime.now(ZoneId.of("Etc/UTC"));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(lang);
        return utcDateZoned.format(pattern);
    }

    public static int[] getTimeLeft(String toParse)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS'Z'");
        LocalDateTime endDate = LocalDateTime.parse(toParse, formatter);
        ZonedDateTime zonedTime = endDate.atZone(ZoneId.of("UTC"));
        Duration diff = Duration.between(ZonedDateTime.now(), zonedTime);
        long seconds = diff.getSeconds();

        long hours = seconds / 3600;
        seconds -= hours * 3600;
        long minutes = seconds / 60;
        seconds -= minutes * 60;

        return new int[]{getPositive(hours), getPositive(minutes), getPositive(seconds)};
    }

    public static Troop getTroopByName(List<Troop> troops, String name)
    {
        return troops.stream()
                .filter(troop -> troop.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static Troop getBuilderTroopByName(List<Troop> troops, String name)
    {
        return troops.stream()
                .filter(troop -> troop.getVillage() == Village.BUILDER_BASE && troop.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static Troop getHomeTroopByName(List<Troop> troops, String name)
    {
        return troops.stream()
                .filter(troop -> troop.getVillage() == Village.HOME_VILLAGE && troop.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
