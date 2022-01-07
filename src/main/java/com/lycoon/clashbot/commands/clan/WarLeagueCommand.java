package com.lycoon.clashbot.commands.clan;

import static com.lycoon.clashbot.utils.DrawUtils.*;
import static com.lycoon.clashbot.utils.FileUtils.*;
import static com.lycoon.clashbot.utils.ErrorUtils.*;
import static com.lycoon.clashbot.utils.DatabaseUtils.*;
import static com.lycoon.clashbot.utils.CoreUtils.*;
import static com.lycoon.clashbot.utils.GameUtils.*;

import com.lycoon.clashapi.models.war.War;
import com.lycoon.clashapi.models.war.WarClan;
import com.lycoon.clashapi.models.warleague.WarLeagueClan;
import com.lycoon.clashapi.models.warleague.WarLeagueRound;
import com.lycoon.clashapi.models.warleague.WarLeagueGroup;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.core.ClanWarStats;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.RoundWarInfo;
import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WarLeagueCommand {
    private final static int ROUND_WIDTH = 932;
    private final static int ROUND_HEIGHT = 333;
    private final static float FONT_SIZE = 16f;

    public static void call(SlashCommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getOptions().isEmpty())
            {
                ResourceBundle i18n = LangUtils.getTranslations(event.getMember().getIdLong());
                sendError(event,
                        i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("tip.usage"), "prefix"));
                return;
            }

            if (event.getOptions().size() == 1)
                executeRound(event, event.getOption("page").getAsString());
            else
                executeRound(event, event.getOption("page").getAsString(), event.getOption("clan_tag").getAsString());
        });
    }

    public static List<War> getWars(WarLeagueGroup leagueGroup, int roundIndex) {
        List<War> wars = new ArrayList<>();
        WarLeagueRound round = leagueGroup.getRounds().get(roundIndex);

        for (String warTag : round.getWarTags()) {
            War warInfo = null;
            try {
                warInfo = ClashBotMain.clashAPI.getWarLeagueWar(warTag);
            } catch (IOException | ClashAPIException ignored) {
            }

            wars.add(warInfo);
        }
        return wars;
    }

    public static void drawRound(Graphics2D g2d, SlashCommandEvent event, List<War> wars, ResourceBundle i18n, int roundIndex) {
        Font font = g2d.getFont().deriveFont(FONT_SIZE);

        // Round label
        Rectangle stateLabel = new Rectangle(365, 30, 200, 25);
        Rectangle roundLabel = new Rectangle(35, 32, 200, 25);
        Rectangle timeRect = new Rectangle(645, 32, 300, 25);

        int[] timeLeft;
        War firstWar = wars.get(0);

        switch (firstWar.getState()) {
            case "preparation" -> {
                timeLeft = getTimeLeft(firstWar.getStartTime());
                g2d.drawImage(getImageFromFile("backgrounds/cwl/cwl-preparation.png"), 0, 0, null);
                drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.timeleft"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 19f, Color.BLACK);
                drawCenteredString(g2d, stateLabel, font.deriveFont(24f), i18n.getString("war.preparation"));
            }
            case "warEnded" -> {
                timeLeft = getTimeLeft(firstWar.getEndTime());
                g2d.drawImage(getImageFromFile("backgrounds/cwl/cwl-ended.png"), 0, 0, null);
                drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.since"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 19f, Color.BLACK);
                drawCenteredString(g2d, stateLabel, font.deriveFont(24f), i18n.getString("war.ended"));
            }
            case "notInWar" -> {
                sendError(event, i18n.getString("exception.warleague.notinwar"));
                return;
            }
            default -> {
                // inWar
                timeLeft = getTimeLeft(firstWar.getEndTime());
                g2d.drawImage(getImageFromFile("backgrounds/cwl/cwl-inwar.png"), 0, 0, null);
                drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.timeleft"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 19f, Color.BLACK);
                drawCenteredString(g2d, stateLabel, font.deriveFont(24f), i18n.getString("war.inwar"));
            }
        }
        drawSimpleCenteredString(g2d, MessageFormat.format(i18n.getString("round.index"), roundIndex + 1), roundLabel, 22f, Color.BLACK);

        // Wars
        for (int i = 0; i < wars.size(); i++) {
            War war = wars.get(i);
            WarClan clan1 = war.getClan();
            WarClan clan2 = war.getOpponent();

            if (war.getState().equals("warEnded")) {
                if (clan2.getStars() > clan1.getStars() ||
                        (clan2.getStars() == clan1.getStars() &&
                                clan2.getDestructionPercentage() > clan1.getDestructionPercentage())) {
                    WarClan tmp = clan1;
                    clan1 = clan2;
                    clan2 = tmp;
                }
            }

            if (clan1 != null && clan2 != null) {
                // Drawing stars
                Rectangle rectStarClan1 = new Rectangle(383, 93 + i * 60, 50, 20);
                Rectangle rectStarClan2 = new Rectangle(503, 93 + i * 60, 50, 20);
                drawCenteredString(g2d, rectStarClan1, font.deriveFont(18f), String.valueOf(clan1.getStars()));
                drawCenteredString(g2d, rectStarClan2, font.deriveFont(18f), String.valueOf(clan2.getStars()));

                if (firstWar.getState().equals("inWar")) {
                    // Drawing clan names
                    drawShadowedStringLeft(g2d, clan1.getName(), 255, 113 + i * 60, 16f, Color.WHITE);
                    drawShadowedString(g2d, clan2.getName(), 670, 113 + i * 60, 16f);

                    // Drawing clan badges
                    g2d.drawImage(getImageFromUrl(clan1.getBadgeUrls().getSmall()), 270, 85 + i * 60, 40, 40, null);
                    g2d.drawImage(getImageFromUrl(clan2.getBadgeUrls().getSmall()), 620, 85 + i * 60, 40, 40, null);

                    // Drawing clan attacks
                    drawShadowedString(g2d, String.valueOf(clan2.getAttacks()), 350, 109 + i * 60, 12f);
                    drawShadowedString(g2d, String.valueOf(clan1.getAttacks()), 569, 109 + i * 60, 12f);
                } else {
                    // Drawing clan names
                    drawShadowedStringLeft(g2d, clan1.getName(), 300, 113 + i * 60, 16f, Color.WHITE);
                    drawShadowedString(g2d, clan2.getName(), 625, 113 + i * 60, 16f);

                    // Drawing clan badges
                    g2d.drawImage(getImageFromUrl(clan1.getBadgeUrls().getSmall()), 320, 85 + i * 60, 40, 40, null);
                    g2d.drawImage(getImageFromUrl(clan2.getBadgeUrls().getSmall()), 570, 85 + i * 60, 40, 40, null);
                }
            }
        }
    }

    public static void updateStats(WarClan clan, HashMap<String, ClanWarStats> stats) {
        if (clan != null) {
            if (stats.containsKey(clan.getTag())) {
                ClanWarStats stats1 = stats.get(clan.getTag());
                stats1.addStars(clan.getStars());
                stats1.addDestruction(clan.getDestructionPercentage());
                stats.put(clan.getTag(), stats1);
            } else
                stats.put(clan.getTag(), new ClanWarStats(clan));
        }
    }

    public static void drawStats(List<RoundWarInfo> rounds) {
        HashMap<String, ClanWarStats> stats = new HashMap<>();
        for (RoundWarInfo roundWars : rounds) {
            for (int j = 0; j < roundWars.getWars().size(); j++) {
                War warInfo = roundWars.getWars().get(j);
                updateStats(warInfo.getClan(), stats);
                updateStats(warInfo.getOpponent(), stats);
            }
        }
    }

    public static WarLeagueGroup getLeagueGroup(SlashCommandEvent event, Locale lang, String[] args) {
        // If rate limitation has exceeded
        if (!checkThrottle(event, lang))
            return null;

        WarLeagueGroup leagueGroup = null;
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        String tag = args.length > 1 ? args[1] : getClanTag(event.getMember().getIdLong());

        if (tag == null) {
            sendError(event, i18n.getString("set.clan.error"),
                    MessageFormat.format(i18n.getString("cmd.general.tip"), Command.SET_CLAN.formatCommand()));
            return null;
        }

        try {
            leagueGroup = ClashBotMain.clashAPI.getWarLeagueGroup(tag);
        } catch (IOException ignored) {
        } catch (ClashAPIException e) {
            sendExceptionError(event, i18n, e, tag, "warleague");
            return null;
        }
        return leagueGroup;
    }

    public static void executeRound(SlashCommandEvent event, String... args) {
        Locale lang = LangUtils.getLanguage(event.getMember().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        // Checking index validity
        int index = checkIndex(event, i18n, args[0], 7);
        if (index == -1)
            return;

        WarLeagueGroup warLeague = getLeagueGroup(event, lang, args);
        if (warLeague == null)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(ROUND_WIDTH, ROUND_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = initGraphics(ROUND_WIDTH, ROUND_HEIGHT, image);
        Font font = getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        // Rounds
        List<War> roundWars = getWars(warLeague, index - 1);
        drawRound(g2d, event, roundWars, i18n, index - 1);
        if (roundWars.get(0).getState().equals("notInWar"))
            return;

        sendImage(event, image);
        g2d.dispose();
    }

    public static WarClan getWinner(WarClan clan1, WarClan clan2) {
        if (clan1.getStars() > clan2.getStars())
            return clan1;
        else if (clan1.getStars() == clan2.getStars()) {
            // when stars are equal
            if (clan1.getDestructionPercentage() > clan2.getDestructionPercentage())
                return clan1;
            else if (clan1.getDestructionPercentage() < clan2.getDestructionPercentage())
                return clan2;
            return null; // when stars and destruction percentages are equal
        }
        return clan2;
    }

    public static int getWinStars(WarClan clan1, WarClan clan2, War war) {
        if (!war.getState().equals("warEnded")) {
            if (getWinner(clan1, clan2) == null)
                return 0;

            if (Objects.equals(getWinner(clan1, clan2), clan1))
                return 10;
        }
        return 0;
    }

    public static void executeAll(SlashCommandEvent event, String... args) {
        Locale lang = LangUtils.getLanguage(event.getMember().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        WarLeagueGroup warLeague = getLeagueGroup(event, lang, args);
        if (warLeague == null)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(ROUND_WIDTH, ROUND_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = initGraphics(ROUND_WIDTH, ROUND_HEIGHT, image);
        Font font = getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        HashMap<String, Integer> stars = new HashMap<>();
        HashMap<String, Integer> destruction = new HashMap<>();

        for (WarLeagueClan clan : warLeague.getClans()) {
            stars.put(clan.getName(), 0);
            destruction.put(clan.getName(), 0);
        }
        for (int i = 0; i < 7; i++) {
            List<War> wars = getWars(warLeague, i);
            for (War war : wars) {
                WarClan clan1 = war.getClan();
                WarClan clan2 = war.getOpponent();

                System.out.println(clan1.getName() + " " + clan1.getStars() + " vs. " + clan2.getName() + " " + clan2.getStars());

                stars.put(clan1.getName(), stars.get(clan1.getName()) + clan1.getStars() + getWinStars(clan1, clan2, war));
                stars.put(clan2.getName(), stars.get(clan2.getName()) + clan2.getStars() + getWinStars(clan2, clan1, war));
                destruction.put(clan1.getName(), (int) (destruction.get(clan1.getName()) + clan1.getDestructionPercentage()));
                destruction.put(clan2.getName(), (int) (destruction.get(clan2.getName()) + clan2.getDestructionPercentage()));
            }
            System.out.println();
        }

        for (WarLeagueClan clan : warLeague.getClans()) {
            System.out.println(clan.getName() + ": " + stars.get(clan.getName()) + " étoiles (" + destruction.get(clan.getName()) + "%)");
        }

        sendImage(event, image);
        g2d.dispose();
    }

    public static void executeClan(SlashCommandEvent event, String... args) {

    }
}
