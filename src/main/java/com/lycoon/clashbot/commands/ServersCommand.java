package com.lycoon.clashbot.commands;

import com.lycoon.clashbot.core.ClashBotMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class ServersCommand
{
    public static void execute(MessageReceivedEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle("I'm currently running on `" + ClashBotMain.jda.getGuilds().size() + "` server(s).");

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
