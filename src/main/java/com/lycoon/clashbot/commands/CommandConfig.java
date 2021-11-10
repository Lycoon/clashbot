package com.lycoon.clashbot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandConfig {
    private final JDA jda;
    private final Guild guild;
    private final String CLASHBOT_GUILD = "757249777439080500";

    public CommandConfig(JDA jda) {
        this.jda = jda;
        this.guild = jda.getGuildById(CLASHBOT_GUILD);
    }

    public void createCommands()
    {
        // Miscellaneous
        CommandData infoCommand = new CommandData("info", "Shows bot information.");
        guild.upsertCommand(infoCommand).queue();

        CommandData inviteCommand = new CommandData("invite", "Shows bot's invite link.");
        guild.upsertCommand(inviteCommand).queue();

        CommandData langCommand = new CommandData("lang", "Shows your current language.");
        guild.upsertCommand(langCommand).queue();

        CommandData statsCommand = new CommandData("stats", "Shows various data about bot.");
        guild.upsertCommand(statsCommand).queue();

        // Settings
        CommandData clearCommand = new CommandData("clear", "Deletes all the data the bot database has about you.");
        guild.upsertCommand(clearCommand).queue();

        CommandData setCommand = new CommandData("set", "Shows clan profile.");
        SubcommandData setClanSubcommand = new SubcommandData("clan", "Sets default clan tag");
        setClanSubcommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", true);

        SubcommandData setPlayerSubcommand = new SubcommandData("player", "Sets default player tag");
        setPlayerSubcommand.addOption(OptionType.STRING, "player_tag", "Player tag from the profile starting with a #", true);

        SubcommandData setLangSubcommand = new SubcommandData("lang", "Sets default language");
        setLangSubcommand.addOption(OptionType.STRING, "language", "Default language code", true);

        setCommand.addSubcommands(setClanSubcommand, setPlayerSubcommand, setLangSubcommand);
        guild.upsertCommand(setCommand).queue();

        // Clan
        CommandData clanCommand = new CommandData("clan", "Shows clan profile.");
        clanCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        guild.upsertCommand(clanCommand).queue();

        CommandData warCommand = new CommandData("war", "Shows current war occurring in the clan.");
        warCommand.addOption(OptionType.INTEGER, "page", "Page number you want to access", true);
        warCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        guild.upsertCommand(warCommand).queue();

        CommandData warlogCommand = new CommandData("warlog", "Shows clan warlog.");
        warlogCommand.addOption(OptionType.INTEGER, "page", "Page number you want to access", true);
        warlogCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        guild.upsertCommand(warlogCommand).queue();

        CommandData warleagueCommand = new CommandData("warleague", "Shows current warleague occurring in the clan.");
        warleagueCommand.addOption(OptionType.INTEGER, "page", "Page number you want to access", true);
        warleagueCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        guild.upsertCommand(warleagueCommand).queue();

        // Player
        CommandData playerCommand = new CommandData("player", "Shows player profile.");
        playerCommand.addOption(OptionType.STRING, "player_tag", "Player tag from the profile starting with a #", false);
        guild.upsertCommand(playerCommand).queue();
    }
}
