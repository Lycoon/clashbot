package com.lycoon.clashbot.commands;

import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.DBUtils;
import com.lycoon.clashbot.utils.ErrorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class SetCommand
{
    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        if (args.length <= 2)
        {
            ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
            ErrorUtils.sendError(event.getChannel(), i18n.getString("wrong.usage"),
                    MessageFormat.format(i18n.getString("tip.usage.two"),
                            Command.SETLANG.formatFullCommand(),
                            Command.SETTAG.formatFullCommand()));
            return;
        }

        String type = args[1].toLowerCase();
        if (type.equals("player"))
            executePlayer(event, args[2]);
        else if (type.equals("clan"))
            executeClan(event, args[2]);
        else if (type.equals("lang"))
            executeLang(event, args[2]);
        else if (type.equals("prefix"))
            executePrefix(event, args[2]);
        else
        {
            ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
            ErrorUtils.sendError(event.getChannel(), i18n.getString("wrong.usage"),
                    MessageFormat.format(i18n.getString("tip.usage.two"),
                            Command.SETLANG.formatFullCommand(),
                            Command.SETTAG.formatFullCommand()));
        }
    }

    public static void executePrefix(MessageReceivedEvent event, String prefix)
    {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) // insufficient permission
        {
            ErrorUtils.sendError(event.getChannel(),
                    i18n.getString("exception.permission"),
                    "You don't have the required `MANAGE_SERVER` permission to change the server prefix.");
            return;
        }

        DBUtils.setServerPrefix(event.getGuild().getIdLong(), prefix);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle(MessageFormat.format(i18n.getString("set.prefix.success"), prefix));
        builder.setFooter(i18n.getString("set.prefix.tip"));

        event.getChannel().sendMessage(builder.build()).queue();
        builder.clear();
    }

    public static void executePlayer(MessageReceivedEvent event, String tag)
    {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());

        // Checks if the player exists
        try
        {
            ClashBotMain.clashAPI.getPlayer(tag);
        } catch (ClashAPIException | IOException e)
        {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "player");
            return;
        }

        DBUtils.setPlayerTag(event.getAuthor().getIdLong(), tag);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle(MessageFormat.format(i18n.getString("set.player.success"), tag));
        builder.setFooter(i18n.getString("set.player.tip"));

        event.getChannel().sendMessage(builder.build()).queue();
        builder.clear();
    }

    public static void executeClan(MessageReceivedEvent event, String tag)
    {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());

        // Checks if the clan exists
        try
        {
            ClashBotMain.clashAPI.getClan(tag);
        } catch (ClashAPIException | IOException e)
        {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "clan");
            return;
        }

        DBUtils.setClanTag(event.getAuthor().getIdLong(), tag);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle(MessageFormat.format(i18n.getString("set.clan.success"), tag));
        builder.setFooter(i18n.getString("set.clan.tip"));

        event.getChannel().sendMessage(builder.build()).queue();
        builder.clear();
    }

    public static void executeLang(MessageReceivedEvent event, String language)
    {
        long id = event.getAuthor().getIdLong();
        if (LangUtils.isSupportedLanguage(language))
        {
            DBUtils.setUserLang(id, language);
            Locale lang = new Locale(language);
            ResourceBundle i18n = LangUtils.getTranslations(lang);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);
            builder.setTitle(i18n.getString("lang.flag") +
                    " " + MessageFormat.format(i18n.getString("lang.success"), lang.getDisplayLanguage(lang)));
            builder.setDescription(i18n.getString("lang.info.other"));

            event.getChannel().sendMessage(builder.build()).queue();
            builder.clear();
        }
        else
        {
            Locale lang = LangUtils.getLanguage(id);
            ResourceBundle i18n = LangUtils.getTranslations(lang);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle(MessageFormat.format(i18n.getString("lang.error"), language));
            builder.appendDescription(i18n.getString("lang.info.supported"));

            int length = LangUtils.LANGUAGES.length;
            double perColumn = Math.ceil(length / 3D);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                if (i != 0 && i % perColumn == 0)
                {
                    builder.addField(str.toString(), "", true);
                    str = new StringBuilder();
                }

                String curr = LangUtils.LANGUAGES[i];
                Locale localeLang = new Locale(curr);
                str.append("▫ ").append(localeLang.getDisplayLanguage(lang)).append(" (`").append(curr).append("`)\n");

                if (i == length - 1)
                    builder.addField(str.toString(), "", true);
            }
            builder.setFooter(i18n.getString("lang.suggest.contact"));
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }
}
