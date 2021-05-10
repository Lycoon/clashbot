package com.lycoon.clashbot.event;

import com.lycoon.clashbot.commands.*;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.lycoon.clashbot.core.ClashBotMain.LOGGER;

public class EventListener extends ListenerAdapter {
    static boolean isCommand(String arg, String prefix, Command cmd) {
        return arg.equalsIgnoreCase(cmd.formatCommand(prefix));
    }

    static boolean isAdminCommand(String arg) {
        return arg.equalsIgnoreCase(AdminCommand.ADMIN.formatCommand());
    }

    static boolean isMentioned(MessageReceivedEvent event, String message) {
        long id = event.getJDA().getSelfUser().getIdLong();
        return message.startsWith("<@" + id + ">") || message.startsWith("<@!" + id + ">");
    }

    static void taggingBot(MessageReceivedEvent event, String prefix) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.GRAY);
        builder.setDescription(CoreUtils.INFO_EMOJI + " " +
                MessageFormat.format(i18n.getString("bot.mention"),
                        Command.HELP.formatCommand(prefix)));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT))
            return;

        String message = event.getMessage().getContentRaw();
        String prefix = DatabaseUtils.getServerPrefix(event.getGuild().getIdLong());

        if (isMentioned(event, message)) {
            taggingBot(event, prefix);
            return;
        } else if (!message.startsWith(prefix))
            return;

        String[] args = message.split(" ");

        if (isCommand(args[0], prefix, Command.SETLANG)) // !set
            SetCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.LANG)) // !lang
            LangCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.PLAYER)) // !player
            PlayerCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.CLAN)) // !player
            ClanCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.WAR)) // !war
            WarCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.WARLOG)) // !warlog
            WarlogCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.WARLEAGUE_ROUND)) // !warleague
            WarLeagueCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.INFO)) // !info
            InfoCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.HELP)) // !help
            HelpCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.CLEAR)) // !clear
            ClearCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.INVITE)) // !invite
            InviteCommand.dispatch(event, args);
        else if (isCommand(args[0], prefix, Command.STATS)) // !stats
            StatsCommand.dispatch(event, args);
        else if (isAdminCommand(args[0])) // !admin
            ServersCommand.dispatch(event, args);
        else
            return;

        LOGGER.info(event.getAuthor().getAsTag() + " issued: " + message);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        TextChannel defaultChannel = event.getGuild().getDefaultChannel();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle("Hi, thanks for inviting me!");
        builder.setDescription("Run `!help` to get the list of all available commands :scroll:");

        try { Objects.requireNonNull(defaultChannel).sendMessage(builder.build()).queue(); }
        catch (MissingAccessException ignored) {}
    }
}
