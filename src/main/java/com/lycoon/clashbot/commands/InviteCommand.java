package com.lycoon.clashbot.commands;

import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class InviteCommand
{
    public static final String INVITE = "https://discord.com/api/oauth2/authorize?client_id=734481969630543883&permissions=2147780672&scope=bot";
    static final String INVITE_EMOJI = "<:invite:825345488152690718>";

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

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
