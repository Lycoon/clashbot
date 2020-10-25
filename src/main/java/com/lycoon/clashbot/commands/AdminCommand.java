package com.lycoon.clashbot.commands;

public enum AdminCommand
{
    SERVERS       ("servers");

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
        for (int i=0; i < ADMINS.length; i++)
        {
            if (Long.valueOf(ADMINS[i]) == id)
                return true;
        }
        return false;
    }
}
