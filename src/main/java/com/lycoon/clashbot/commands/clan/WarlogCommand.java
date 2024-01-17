package com.lycoon.clashbot.commands.clan;

import static com.lycoon.clashbot.utils.DrawUtils.*;
import static com.lycoon.clashbot.utils.FileUtils.*;
import static com.lycoon.clashbot.utils.ErrorUtils.*;
import static com.lycoon.clashbot.utils.DatabaseUtils.*;
import static com.lycoon.clashbot.utils.CoreUtils.*;
import static com.lycoon.clashbot.utils.GameUtils.*;

import com.lycoon.clashapi.core.exceptions.ClashAPIException;
import com.lycoon.clashapi.models.war.WarlogClan;
import com.lycoon.clashapi.models.war.WarlogEntry;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashapi.models.war.enums.WarResult;
import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.core.CacheComponents;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class WarlogCommand {
    public static ResourceBundle i18n;
    public static Locale lang;

    private final static int PADDING = 3;
    private final static int SIZE = 5;
    private final static int WAR_ITEM_HEIGHT = 74;
    private final static int WIDTH = 932;
    private final static float FONT_SIZE = 16f;

    private final static Color backgroundColor = new Color(0xe7e7e1);
    private final static Color winsColor = new Color(0xd5edba);
    private final static Color lossesColor = new Color(0xf2c8c7);
    private final static Color drawsColor = new Color(0xcccccc);
    private final static Color totalColor = new Color(0xfefed1);
    private final static Color versusColor = new Color(0xffffc0);
    private final static Color percentageColor = new Color(0x5e5d60);

    public static void call(SlashCommandInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getOptions().isEmpty())
            {
                i18n = LangUtils.getTranslations(event.getMember().getIdLong());
                sendError(event,
                        i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("tip.usage"), "prefix"));
                return;
            }

            if (event.getOptions().size() == 1)
                execute(event, event.getOption("page").getAsString());
            else
                execute(event, event.getOption("page").getAsString(), event.getOption("clan_tag").getAsString());
        });
    }

    public static Image getImageStatus(WarResult result)
    {
        if (result == null)
            return CacheComponents.warlogCWL; // CWL
        else if (result == WarResult.WIN)
            return CacheComponents.warlogWon; // won
        else if (result == WarResult.LOSE)
            return CacheComponents.warlogLost; // lost

        return CacheComponents.warlogTie; // tie
    }

    public static void drawWar(Graphics2D g2d, WarlogEntry war, int y)
    {
        boolean isCWL = war.getResult() == null;

        // Member background
        Image result = getImageStatus(war.getResult());
        g2d.drawImage(result, 0, y, null);

        // Clan badges
        WarlogClan clan = war.getClan();
        WarlogClan enemy = war.getOpponent();
        g2d.drawImage(getImageFromUrl(clan.getBadgeUrls().getMedium()), 395, y + 5, 70, 70, null);
        g2d.drawImage(getImageFromUrl(enemy.getBadgeUrls().getMedium()), 470, y + 5, 70, 70, null);

        // Clan names
        drawShadowedStringLeft(g2d, clan.getName(), 375, y + 28, 20f);
        drawShadowedString(g2d, enemy.getName() == null ? "..." : enemy.getName(), 555, y + 28, 20f);

        // Stars
        g2d.drawImage(CacheComponents.newStar, 355, y + 39, 22, 22, null);
        g2d.drawImage(CacheComponents.newStar, 555, y + 39, 22, 22, null);
        drawShadowedStringLeft(g2d, String.valueOf(clan.getStars()), 350, y + 58, 16f);
        drawShadowedString(g2d, String.valueOf(enemy.getStars()), 581, y + 58, 16f);

        // Destruction percentage
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(lang);
        DecimalFormat df = new DecimalFormat("#.##", dfs);
        drawSimpleStringLeft(g2d, df.format(war.getClan().getDestructionPercentage()) + "%", 300, y + 59, 16f, percentageColor);
        drawSimpleString(g2d, df.format(war.getOpponent().getDestructionPercentage()) + "%", 630, y + 59, 16f, percentageColor);

        // Experience earned
        g2d.drawImage(getImageFromFile("icons/exp-badge.png"), 24, y + 20, 20, 20, null);
        drawSimpleString(g2d, "+" + clan.getExpEarned(), 48, y + 35, 14f, Color.BLACK);

        // End time
        int[] timeLeft = getTimeLeft(war.getEndTime());
        drawSimpleString(g2d, MessageFormat.format(i18n.getString("warlog.days.ago"), timeLeft[0] / 24), 24, y + 60, 12f, Color.WHITE);

        // CWL indication
        if (isCWL) {
            Rectangle cwlRect = new Rectangle(0, y + 20, WIDTH, 15);
            drawCenteredString(g2d, cwlRect, g2d.getFont().deriveFont(18f), "CWL", versusColor);
        }

        // Versus team size
        Rectangle teamSizeRect = new Rectangle(0, y + (isCWL ? 45 : 35), WIDTH, 15);
        drawCenteredString(g2d, teamSizeRect, g2d.getFont().deriveFont(14f), MessageFormat.format(i18n.getString("team.size.versus"), war.getTeamSize()), versusColor);
    }

    public static List<WarlogEntry> getWarlog(SlashCommandInteractionEvent event, Locale lang, String[] args)
    {
        // If rate limitation has exceeded
        if (!checkThrottle(event, lang))
            return null;

        List<WarlogEntry> warlog = null;
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        String tag = args.length > 1 ? args[1] : getClanTag(event.getMember().getIdLong());

        if (tag == null) {
            sendError(event, i18n.getString("set.clan.error"),
                    MessageFormat.format(i18n.getString("cmd.general.tip"), Command.SET_CLAN.formatCommand()));
            return null;
        }

        try {
            warlog = ClashBotMain.clashAPI.getWarlog(tag);
        } catch (IOException ignored) {
        } catch (ClashAPIException e) {
            sendExceptionError(event, i18n, e, tag, "warlog");
            return null;
        }
        return warlog;
    }

    public static void execute(SlashCommandInteractionEvent event, String... args)
    {
        lang = LangUtils.getLanguage(event.getMember().getIdLong());
        i18n = LangUtils.getTranslations(lang);

        List<WarlogEntry> warlog = getWarlog(event, lang, args);
        if (warlog == null)
            return;

        // Checking index validity
        int index = checkIndex(event, i18n, args[0], warlog.size() / SIZE);
        if (index == -1)
            return;

        // Checking if there are any clan wars
        if (warlog.isEmpty()) {
            sendError(event, i18n.getString("no.warlog"));
            return;
        }

        // Computing stats
        int total = 0, wins = 0, losses = 0, draws = 0;
        for (WarlogEntry war : warlog)
        {
            if (war.getResult() == null)
                continue;

            switch (war.getResult())
            {
                case WIN -> wins++;
                case LOSE -> losses++;
                default -> draws++;
            }
        }
        total = wins + losses + draws;

        // Computing height
        int height = 15 + 88 + 15 + (WAR_ITEM_HEIGHT + PADDING) * SIZE + 12;

        // Initializing image
        BufferedImage image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = initGraphics(WIDTH, height, image);
        Font font = getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        // Color background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, height);

        // Top background
        g2d.drawImage(getImageFromFile("backgrounds/warlog/stats-panel-full.png"), 0, 15, null);

        // Drawing stats
        drawShadowedString(g2d, i18n.getString("warlog.stats"), 80, 52, 20f);
        drawShadowedString(g2d, MessageFormat.format(i18n.getString("warlog.coverage"), warlog.size()), 80, 70, 10f);
        drawShadowedString(g2d, i18n.getString("wins"), 340, 40, 16f, 2, winsColor);
        drawShadowedString(g2d, i18n.getString("losses"), 495, 40, 16f, 2, lossesColor);
        drawShadowedString(g2d, i18n.getString("draws"), 645, 40, 16f, 2, drawsColor);
        drawShadowedString(g2d, i18n.getString("total"), 790, 40, 16f, 2, totalColor);

        drawShadowedString(g2d, String.valueOf(wins), 340, 70, 18f);
        drawShadowedString(g2d, String.valueOf(losses), 495, 70, 18f);
        drawShadowedString(g2d, String.valueOf(draws), 645, 70, 18f);
        drawShadowedString(g2d, String.valueOf(total), 790, 70, 18f);

        // Drawing wars
        for (int i = 0; i < SIZE; i++)
            drawWar(g2d, warlog.get((index - 1) * SIZE + i), 118 + i * WAR_ITEM_HEIGHT + i * PADDING);

        sendImage(event, image);
        g2d.dispose();
    }
}
