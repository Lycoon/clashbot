package com.lycoon.clashbot.core;

import com.lycoon.clashbot.commands.*;
import com.lycoon.clashbot.commands.clan.ClanCommand;
import com.lycoon.clashbot.commands.clan.WarCommand;
import com.lycoon.clashbot.commands.clan.WarLeagueCommand;
import com.lycoon.clashbot.commands.clan.WarlogCommand;
import com.lycoon.clashbot.commands.misc.*;
import com.lycoon.clashbot.commands.settings.SetCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Objects;

public class EventListener extends ListenerAdapter
{
    static boolean isCommand(String arg, Command cmd) {
        return arg.equalsIgnoreCase(cmd.toString());
    }

    static MessageEmbed warnNotInGuild()
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.YELLOW);
        builder.setTitle("Commands must be executed in guilds");
        builder.setDescription("Clashbot does not currently support commands in DMs. Please invite me in your guild or join one where I am in. I will be glad to help.");

        return builder.build();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild())
        {
            event.replyEmbeds(warnNotInGuild()).queue();
            return;
        }

        event.deferReply().queue();

        String cmd = event.getName();
        if (isCommand(cmd, Command.SET_LANG))
            SetCommand.call(event);
        else if (isCommand(cmd, Command.PLAYER))
            PlayerCommand.call(event);
        else if (isCommand(cmd, Command.CLAN))
            ClanCommand.call(event);
        else if (isCommand(cmd, Command.WAR))
            WarCommand.call(event);
        else if (isCommand(cmd, Command.WARLOG))
            WarlogCommand.call(event);
        else if (isCommand(cmd, Command.WARLEAGUE))
            WarLeagueCommand.call(event);
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
        else {
            event.getHook().deleteOriginal().queue();
            return;
        }

        ClashBotMain.LOGGER.info(event.getMember().getEffectiveName() + " (" + event.getMember().getId() + ") executed " + event.getName() + " command");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        DefaultGuildChannelUnion defaultChannel = event.getGuild().getDefaultChannel();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle("Hi, thanks for inviting me!");
        builder.setDescription("Run `/help` to get the list of all available commands :scroll:");

        try { Objects.requireNonNull(defaultChannel).asTextChannel().sendMessage(builder.build()).queue(); }
        catch (MissingAccessException ignored) {}
    }
}
