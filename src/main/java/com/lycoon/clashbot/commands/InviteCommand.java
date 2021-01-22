package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InviteCommand
{
    private static final String INVITE = "https://discord.com/api/oauth2/authorize?client_id=734481969630543883&permissions=51200&scope=bot";
    private static final String INVITE_EMOJI = "<:invite:793313994844274699>";

    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        execute(event);
    }

    public static void execute(MessageReceivedEvent event)
    {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.GRAY);
        builder.setDescription(INVITE_EMOJI + " " + MessageFormat.format(
                i18n.getString("cmd.invite.panel"),
                INVITE));

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
