package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LangCommand
{
    public static void call(SlashCommandEvent event, String... args)
    {
        execute(event);
    }

    public static void execute(SlashCommandEvent event)
    {
        Locale lang = LangUtils.getLanguage(event.getMember().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle(
                i18n.getString("lang.flag") + "  " + MessageFormat.format(
                        i18n.getString("lang.current"), lang.getDisplayLanguage(lang)));
        builder.setDescription(MessageFormat.format(
                i18n.getString("lang.info.other"), Command.SETLANG.formatCommand()));

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
