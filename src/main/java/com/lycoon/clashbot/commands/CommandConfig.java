package com.lycoon.clashbot.commands;

import com.lycoon.clashbot.core.ClashBotMain;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandConfig {
    private final JDA jda;
    private final Guild guild;
    private final String CLASHBOT_GUILD = "817384284507209768";

    public CommandConfig(JDA jda) {
        this.jda = jda;
        this.guild = jda.getGuildById(CLASHBOT_GUILD);
    }

    public void createCommands()
    {
        ClashBotMain.LOGGER.info("Creating commands...");

        // Miscellaneous
        CommandData infoCommand = new CommandData("info", "Shows bot information");
        jda.upsertCommand(infoCommand).complete();

        CommandData inviteCommand = new CommandData("invite", "Shows bot's invite link");
        jda.upsertCommand(inviteCommand).complete();

        CommandData langCommand = new CommandData("lang", "Shows your current language");
        jda.upsertCommand(langCommand).complete();

        CommandData helpCommand = new CommandData("help", "Shows commands and their usage");
        jda.upsertCommand(helpCommand).complete();

        // Settings
        CommandData clearCommand = new CommandData("clear", "Deletes all the data the bot database has about you");
        jda.upsertCommand(clearCommand).complete();

        CommandData setCommand = new CommandData("set", "Shows clan profile");
        SubcommandData setClanSubcommand = new SubcommandData("clan", "Sets default clan tag");
        setClanSubcommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", true);

        SubcommandData setPlayerSubcommand = new SubcommandData("player", "Sets default player tag");
        setPlayerSubcommand.addOption(OptionType.STRING, "player_tag", "Player tag from the profile starting with a #", true);

        SubcommandData setLangSubcommand = new SubcommandData("lang", "Sets default language");
        setLangSubcommand.addOption(OptionType.STRING, "language", "Default language code", true);

        setCommand.addSubcommands(setClanSubcommand, setPlayerSubcommand, setLangSubcommand);
        jda.upsertCommand(setCommand).complete();

        // Clan
        CommandData clanCommand = new CommandData("clan", "Shows clan profile");
        clanCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        jda.upsertCommand(clanCommand).complete();

        CommandData warCommand = new CommandData("war", "Shows current war occurring in the clan");
        warCommand.addOption(OptionType.INTEGER, "page", "Page number you want to access", true);
        warCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        jda.upsertCommand(warCommand).complete();

        CommandData warlogCommand = new CommandData("warlog", "Shows clan warlog");
        warlogCommand.addOption(OptionType.INTEGER, "page", "Page number you want to access", true);
        warlogCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        jda.upsertCommand(warlogCommand).complete();

        CommandData warleagueCommand = new CommandData("warleague", "Shows current warleague occurring in the clan");
        warleagueCommand.addOption(OptionType.INTEGER, "page", "Page number you want to access", true);
        warleagueCommand.addOption(OptionType.STRING, "clan_tag", "Clan tag from the profile starting with a #", false);
        jda.upsertCommand(warleagueCommand).complete();

        // Player
        CommandData playerCommand = new CommandData("player", "Shows player profile");
        playerCommand.addOption(OptionType.STRING, "player_tag", "Player tag from the profile starting with a #", false);
        jda.upsertCommand(playerCommand).complete();

        ClashBotMain.LOGGER.info("Commands created successfully");
    }
}
