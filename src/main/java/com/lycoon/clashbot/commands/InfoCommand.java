package com.lycoon.clashbot.commands;

import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class InfoCommand {
    private static final String TWITTER = "@LycoonMC";
    private static final String DISCORD = "Lycoon#7542";
    private static final String WEBSITE = "https://lycoon.github.io/clashbot/";
    private static final String BOT_CODE = "https://github.com/Lycoon/clashbot";
    private static final String API_CODE = "https://github.com/Lycoon/clash-api";
    private static final String DISCORD_INVITE = "https://discord.gg/Cy86PDA";
    private static final String PAYPAL = "https://www.patreon.com/clashbot";
    private static final String VERSION = "1.2.2";

    public static void dispatch(MessageReceivedEvent event, String... args) {
        execute(event);
    }

    public static void execute(MessageReceivedEvent event) {
        ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.GRAY);
        builder.setTitle(i18n.getString("cmd.info.panel"));
        builder.setDescription(
                i18n.getString("info.description") + "\n\n"
                        + MessageFormat.format(i18n.getString("info.author"), TWITTER, DISCORD) + "\n"
                        + MessageFormat.format(i18n.getString("info.website"), WEBSITE) + "\n"
                        + MessageFormat.format(i18n.getString("info.discord.invite"), DISCORD_INVITE) + "\n"
                        + MessageFormat.format(i18n.getString("info.support"), PAYPAL) + "\n\n"
                        + "Version " + VERSION
        );

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
