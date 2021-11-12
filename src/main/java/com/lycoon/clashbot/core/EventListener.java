package com.lycoon.clashbot.core;

import com.lycoon.clashbot.commands.*;
import com.lycoon.clashbot.commands.clan.ClanCommand;
import com.lycoon.clashbot.commands.clan.WarCommand;
import com.lycoon.clashbot.commands.clan.WarLeagueCommand;
import com.lycoon.clashbot.commands.clan.WarlogCommand;
import com.lycoon.clashbot.commands.misc.*;
import com.lycoon.clashbot.commands.settings.AdminCommand;
import com.lycoon.clashbot.commands.settings.SetCommand;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.lycoon.clashbot.core.ClashBotMain.LOGGER;

public class EventListener extends ListenerAdapter {
    static boolean isCommand(String arg, Command cmd) {
        return arg.equalsIgnoreCase(cmd.toString());
    }

    static boolean isOldCommand(String arg, String prefix, Command cmd) {
        return arg.equalsIgnoreCase(prefix + cmd.toString());
    }

    static boolean isAdminCommand(String arg) {
        return arg.equalsIgnoreCase(AdminCommand.ADMIN.formatCommand());
    }


    static boolean isMentioned(MessageReceivedEvent event, String message) {
        long id = event.getJDA().getSelfUser().getIdLong();
        return message.startsWith("<@" + id + ">") || message.startsWith("<@!" + id + ">");
    }

    static void taggingBot(SlashCommandEvent event) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.GRAY);
        builder.setDescription(CoreUtils.INFO_EMOJI + " " +
                MessageFormat.format(i18n.getString("bot.mention"),
                        "prefix"));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    static void warnOldCommands(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.YELLOW);
        builder.setTitle("Switching to slash commands");
        builder.setDescription("Starting from January 1st 2022, old Clashbot command system will be dropped in favour of slash commands. " +
                "This is done in compliance with Discord terms as they address privacy concerns dealing with interactions in a more elegant way.\n\n" +
                "⚠ Click [here](" + ClashBotMain.INVITE + ") to reinvite with new permissions\n" +
                "➡ Start using slash commands typing `/help` in the chat");

        event.getChannel().sendMessage(builder.build()).queue();
    }

    static MessageEmbed warnNotInGuild(SlashCommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.YELLOW);
        builder.setTitle("Commands must be executed in guilds");
        builder.setDescription("Clashbot does not currently support commands in DMs. Please invite me in your guild or join one where I am in. I will be glad to help.");

        return builder.build();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.isFromGuild())
        {
            event.replyEmbeds(warnNotInGuild(event)).queue();
            return;
        }

        System.out.println(event.getName() + " BEFORE");
        String cmd = event.getName();
        event.deferReply().queue();
        System.out.println(event.getName() + " AFTER");

        if (isCommand(cmd, Command.SETLANG))
            SetCommand.call(event);
        else if (isCommand(cmd, Command.PLAYER))
            PlayerCommand.call(event);
        else if (isCommand(cmd, Command.CLAN))
            ClanCommand.call(event);
        else if (isCommand(cmd, Command.WAR))
            WarCommand.call(event);
        else if (isCommand(cmd, Command.WARLOG))
            WarlogCommand.call(event);
        else if (isCommand(cmd, Command.LANG))
            LangCommand.call(event);
        else if (isCommand(cmd, Command.INFO))
            InfoCommand.call(event);
        else if (isCommand(cmd, Command.HELP))
            HelpCommand.call(event);
        else if (isCommand(cmd, Command.CLEAR))
            ClearCommand.call(event);
        else if (isCommand(cmd, Command.INVITE))
            InviteCommand.call(event);
        else if (isCommand(cmd, Command.STATS))
            StatsCommand.call(event);
    }

    /**
     * TO DELETE: On 1st December 2021
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT))
            return;

        String message = event.getMessage().getContentRaw();
        String prefix = DatabaseUtils.getServerPrefix(event.getGuild().getIdLong());

        if (!message.startsWith(prefix))
            return;

        String[] args = message.split(" ");
        if (isOldCommand(args[0], prefix, Command.SETLANG) || isOldCommand(args[0], prefix, Command.LANG)
                || isOldCommand(args[0], prefix, Command.PLAYER) || isOldCommand(args[0], prefix, Command.CLAN)
                || isOldCommand(args[0], prefix, Command.WAR) || isOldCommand(args[0], prefix, Command.WARLOG)
                || isOldCommand(args[0], prefix, Command.WARLEAGUE_ROUND) || isOldCommand(args[0], prefix, Command.INFO)
                || isOldCommand(args[0], prefix, Command.HELP) || isOldCommand(args[0], prefix, Command.CLEAR)
                || isOldCommand(args[0], prefix, Command.INVITE) || isOldCommand(args[0], prefix, Command.STATS))
        {
            warnOldCommands(event);
        }

        LOGGER.info(event.getAuthor().getAsTag() + " issued: " + message);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        TextChannel defaultChannel = event.getGuild().getDefaultChannel();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle("Hi, thanks for inviting me!");
        builder.setDescription("Run `/help` to get the list of all available commands :scroll:");

        try { Objects.requireNonNull(defaultChannel).sendMessage(builder.build()).queue(); }
        catch (MissingAccessException ignored) {}
    }
}
