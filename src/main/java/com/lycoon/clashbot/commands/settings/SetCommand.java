package com.lycoon.clashbot.commands.settings;

import com.lycoon.clashapi.core.exceptions.ClashAPIException;
import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import com.lycoon.clashbot.utils.ErrorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class SetCommand
{
    public static void call(SlashCommandInteractionEvent event)
    {
        if (event.getSubcommandName() == null) {
            ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());
            ErrorUtils.sendError(event,
                    i18n.getString("wrong.usage"),
                    MessageFormat.format(i18n.getString("info.help"), "prefix"));
            return;
        }

        String type = event.getSubcommandName();
        switch (type)
        {
            case "player" -> executePlayer(event, event.getOption("player_tag").getAsString());
            case "clan" -> executeClan(event, event.getOption("clan_tag").getAsString());
            case "lang" -> executeLang(event, event.getOption("language").getAsString());
            default -> {
                ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());
                ErrorUtils.sendError(event,
                        i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("info.help"), "prefix"));
            }
        }
    }

    public static void executePlayer(SlashCommandInteractionEvent event, String tag) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());

        try {
            // Checks if the player exists
            ClashBotMain.clashAPI.getPlayer(tag);
        } catch (ClashAPIException | IOException e) {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "player");
            return;
        }

        DatabaseUtils.setPlayerTag(event.getMember().getIdLong(), tag);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(CoreUtils.validColor);
        builder.setTitle(MessageFormat.format(i18n.getString("set.player.success"), tag));
        builder.setFooter(i18n.getString("set.player.tip"));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    public static void executeClan(SlashCommandInteractionEvent event, String tag)
    {
        ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());

        try {
            // Checks if the clan exists
            ClashBotMain.clashAPI.getClan(tag);
        } catch (ClashAPIException e) {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "clan");
            return;
        }

        DatabaseUtils.setClanTag(event.getMember().getIdLong(), tag);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(CoreUtils.validColor);
        builder.setTitle(MessageFormat.format(i18n.getString("set.clan.success"), tag));
        builder.setFooter(i18n.getString("set.clan.tip"));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    public static void executeLang(SlashCommandInteractionEvent event, String language)
    {
        long id = event.getMember().getIdLong();
        if (LangUtils.isSupportedLanguage(language))
        {
            DatabaseUtils.setUserLang(id, language);
            Locale lang = new Locale(language);
            ResourceBundle i18n = LangUtils.getTranslations(lang);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(CoreUtils.validColor);
            builder.setTitle(i18n.getString("lang.flag") + " " +
                    MessageFormat.format(i18n.getString("lang.success"), lang.getDisplayLanguage(lang)));
            builder.setDescription(
                    MessageFormat.format(i18n.getString("lang.info.other"),
                            Command.SET_LANG.formatCommand()));

            CoreUtils.sendMessage(event, i18n, builder);
        }
        else
        {
            Locale lang = LangUtils.getLanguage(id);
            ResourceBundle i18n = LangUtils.getTranslations(lang);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(CoreUtils.invalidColor);
            builder.setTitle(MessageFormat.format(i18n.getString("lang.error"), language));
            builder.appendDescription(i18n.getString("lang.info.supported"));

            int length = LangUtils.LANGUAGES.length;
            double perColumn = Math.ceil(length / 3D);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                if (i != 0 && i % perColumn == 0) {
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

            CoreUtils.sendMessage(event, i18n, builder);
        }
    }
}
