package com.lycoon.clashbot.commands;

import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import com.lycoon.clashbot.utils.ErrorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class SetCommand {
    public static void dispatch(MessageReceivedEvent event, String... args) {
        String prefix = DatabaseUtils.getServerPrefix(event.getGuild().getIdLong());
        if (args.length <= 2) {
            ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
            ErrorUtils.sendError(event.getChannel(),
                    i18n.getString("wrong.usage"),
                    MessageFormat.format(i18n.getString("info.help"), Command.HELP.formatFullCommand(prefix)));
            return;
        }

        String type = args[1].toLowerCase();
        switch (type) {
            case "player" -> executePlayer(event, args[2]);
            case "clan" -> executeClan(event, args[2]);
            case "lang" -> executeLang(event, args[2], prefix);
            case "prefix" -> executePrefix(event, args[2]);
            default -> {
                ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
                ErrorUtils.sendError(event.getChannel(),
                        i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("info.help"), Command.HELP.formatFullCommand(prefix)));
            }
        }
    }

    public static void executePrefix(MessageReceivedEvent event, String prefix) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            // Insufficient permission
            ErrorUtils.sendError(event.getChannel(),
                    i18n.getString("exception.permission"),
                    i18n.getString("exception.prefix.permission"));
            return;
        } else if (prefix.length() > 3) {
            // Too long prefix
            ErrorUtils.sendError(event.getChannel(), i18n.getString("exception.prefix.long"));
            return;
        }

        DatabaseUtils.setServerPrefix(event.getGuild().getIdLong(), prefix);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(CoreUtils.validColor);
        builder.setTitle(MessageFormat.format(i18n.getString("set.prefix.success"), prefix));
        builder.setFooter(i18n.getString("set.prefix.tip"));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    public static void executePlayer(MessageReceivedEvent event, String tag) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        try {
            // Checks if the player exists
            ClashBotMain.clashAPI.getPlayer(tag);
        } catch (ClashAPIException | IOException e) {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "player");
            return;
        }

        DatabaseUtils.setPlayerTag(event.getAuthor().getIdLong(), tag);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(CoreUtils.validColor);
        builder.setTitle(MessageFormat.format(i18n.getString("set.player.success"), tag));
        builder.setFooter(i18n.getString("set.player.tip"));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    public static void executeClan(MessageReceivedEvent event, String tag) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());

        try {
            // Checks if the clan exists
            ClashBotMain.clashAPI.getClan(tag);
        } catch (ClashAPIException | IOException e) {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "clan");
            return;
        }

        DatabaseUtils.setClanTag(event.getAuthor().getIdLong(), tag);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(CoreUtils.validColor);
        builder.setTitle(MessageFormat.format(i18n.getString("set.clan.success"), tag));
        builder.setFooter(i18n.getString("set.clan.tip"));

        CoreUtils.sendMessage(event, i18n, builder);
    }

    public static void executeLang(MessageReceivedEvent event, String language, String prefix) {
        long id = event.getAuthor().getIdLong();
        if (LangUtils.isSupportedLanguage(language)) {
            DatabaseUtils.setUserLang(id, language);
            Locale lang = new Locale(language);
            ResourceBundle i18n = LangUtils.getTranslations(lang);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(CoreUtils.validColor);
            builder.setTitle(i18n.getString("lang.flag") + " " +
                    MessageFormat.format(i18n.getString("lang.success"), lang.getDisplayLanguage(lang)));
            builder.setDescription(
                    MessageFormat.format(i18n.getString("lang.info.other"),
                            Command.SETLANG.formatFullCommand(prefix)));

            CoreUtils.sendMessage(event, i18n, builder);
        } else {
            Locale lang = LangUtils.getLanguage(id);
            ResourceBundle i18n = LangUtils.getTranslations(lang);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(CoreUtils.invalidColor);
            builder.setTitle(MessageFormat.format(i18n.getString("lang.error"), language));
            builder.appendDescription(i18n.getString("lang.info.supported"));

            int length = LangUtils.LANGUAGES.length;
            double perColumn = Math.ceil(length / 3D);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < length; i++) {
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
