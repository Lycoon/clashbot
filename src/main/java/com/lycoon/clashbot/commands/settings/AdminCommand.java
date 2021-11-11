package com.lycoon.clashbot.commands.settings;

public enum AdminCommand
{
    ADMIN       ("admin");

    public static final String PREFIX = "!";
    public static final String[] ADMINS = {"138282927502000128", "198485955701768192"};
    final String name;

    AdminCommand(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public String formatCommand()
    {
        return PREFIX + name;
    }

    public static boolean isAdmin(long id)
    {
        for (String admin : ADMINS) {
            if (Long.parseLong(admin) == id)
                return true;
        }
        return false;
    }
}
