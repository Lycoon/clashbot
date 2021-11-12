package com.lycoon.clashbot.commands;

public enum Command
{
    STATS(CommandCategory.MISC, "stats", "cmd.stats.desc"),
    INVITE(CommandCategory.MISC, "invite", "cmd.invite.desc"),
    LANG(CommandCategory.MISC, "lang", "cmd.lang.desc"),
    INFO(CommandCategory.MISC, "info", "cmd.info.desc"),
    HELP(CommandCategory.MISC, "help", "cmd.help.desc"),
    CLEAR(CommandCategory.SETTINGS, "clear", "cmd.clear.desc"),
    SETTAG(CommandCategory.SETTINGS, "set", "cmd.settag.desc", "<clan|player> <tag>"),
    SETLANG(CommandCategory.SETTINGS, "set", "cmd.setlang.desc", "lang <language>"),
    CLAN(CommandCategory.CLAN, "clan", "cmd.clan.desc", "[clanTag]"),
    WARLEAGUE_ROUND(CommandCategory.CLAN, "warleague", "cmd.warleague.round.desc", "round <page> [clanTag]"),
    //WARLEAGUE_ALL   (CommandCategory.CLAN,     "warleague", "cmd.warleague.all.desc",   "all [clanTag]"),
    //WARLEAGUE_CLAN  (CommandCategory.CLAN,     "warleague", "cmd.warleague.clan.desc",  "[clanTag]"),
    WARLOG(CommandCategory.CLAN, "warlog", "cmd.warlog.desc", "<page> [clanTag]"),
    WAR(CommandCategory.CLAN, "war", "cmd.war.desc", "<page> [clanTag]"),
    PLAYER(CommandCategory.PLAYER, "player", "cmd.player.desc", "[playerTag]");

    final CommandCategory category;
    final String name, desc;
    String usage;

    Command(CommandCategory category, String name, String desc, String usage)
    {
        this.category = category;
        this.name = name;
        this.desc = desc;
        this.usage = usage;
    }

    Command(CommandCategory category, String name, String desc)
    {
        this.category = category;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String getDescription()
    {
        return desc;
    }

    public CommandCategory getCategory()
    {
        return category;
    }

    public String formatCommand()
    {
        return "/" + name + (usage == null ? "" : " " + usage);
    }
}
