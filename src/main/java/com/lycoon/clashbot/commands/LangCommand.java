package com.lycoon.clashbot.commands;

import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LangCommand
{
    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        execute(event);
    }

    public static void execute(MessageReceivedEvent event)
    {
        String prefix = DatabaseUtils.getServerPrefix(event.getGuild().getIdLong());
        Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle(
                i18n.getString("lang.flag") + "  " + MessageFormat.format(
                        i18n.getString("lang.current"), lang.getDisplayLanguage(lang)));
        builder.setDescription(MessageFormat.format(
                i18n.getString("lang.info.other"), Command.SETLANG.formatFullCommand(prefix)));

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
