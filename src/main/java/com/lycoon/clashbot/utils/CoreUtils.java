package com.lycoon.clashbot.utils;

import com.lycoon.clashbot.commands.misc.InviteCommand;
import com.lycoon.clashbot.core.ClashBotMain;

import static com.lycoon.clashbot.core.ClashBotMain.LOGGER;

import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class CoreUtils {
    public static final Color validColor = Color.decode("#48bd73");
    public static final Color invalidColor = Color.decode("#c24646");
    public static final String INFO_EMOJI = "<:info:825346959514533928>";

    static Duration rateTime = Duration.ofSeconds(6);
    static long threshold = rateTime.toMillis();
    static HashMap<Long, ZonedDateTime> generating = new HashMap<>();

    public static void addUserToGenerating(long id) {
        generating.put(id, ZonedDateTime.now());
    }

    public static void removeUserFromGenerating(long id) {
        generating.remove(id);
    }

    public static long getLastTimeDifference(long id) {
        if (generating.containsKey(id)) {
            Duration diff = Duration.between(generating.get(id), ZonedDateTime.now());
            return diff.toMillis();
        }
        return threshold;
    }

    public static boolean checkThrottle(SlashCommandEvent event, Locale lang) {
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        NumberFormat nf = NumberFormat.getNumberInstance(lang);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.#");

        long timeDifference = getLastTimeDifference(event.getMember().getIdLong());
        boolean isValid = timeDifference >= threshold;

        if (!isValid)
            ErrorUtils.sendError(event,
                    i18n.getString("exception.rate.exceeded"),
                    MessageFormat.format(
                            i18n.getString("exception.rate.exceeded.left"),
                            df.format((threshold - timeDifference) / 1000.0)));

        return isValid;
    }

    public static boolean isOwner(long id) {
        for (int i = 0; i < ClashBotMain.owners.length; i++)
            if (id == ClashBotMain.owners[i])
                return true;

        return false;
    }

    public static void sendMessage(SlashCommandEvent event, ResourceBundle i18n, EmbedBuilder builder) {
        try {
            event.getChannel().sendMessage(builder.build()).queue();
        } catch (InsufficientPermissionException e) {
            LOGGER.debug(e.getMessage());
            event.getMember().getUser().openPrivateChannel().queue(
                    // Success
                    (channel) ->
                            ErrorUtils.sendError(event, INFO_EMOJI + " " +
                                    i18n.getString("exception.permission.title"), MessageFormat.format(
                                    i18n.getString("exception.permission.tip"),
                                    event.getGuild().getName(), InviteCommand.INVITE)),

                    // Failure
                    (err) -> LOGGER.debug(err.getMessage()));
        }

        builder.clear();
    }
}
