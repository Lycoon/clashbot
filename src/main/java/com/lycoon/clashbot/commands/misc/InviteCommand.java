package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class InviteCommand
{
    static final String INVITE_EMOJI = "<:invite:825345488152690718>";

    public static void call(SlashCommandEvent event)
    {
        execute(event);
    }

    public static void execute(SlashCommandEvent event)
    {
        ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.GRAY);
        builder.setDescription(INVITE_EMOJI + " " + MessageFormat.format(
                i18n.getString("cmd.invite.panel"),
                ClashBotMain.INVITE));

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
