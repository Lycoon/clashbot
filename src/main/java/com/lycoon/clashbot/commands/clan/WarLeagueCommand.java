package com.lycoon.clashbot.commands.clan;

import com.lycoon.clashapi.cocmodels.clanwar.ClanWarModel;
import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashapi.cocmodels.clanwar.league.Round;
import com.lycoon.clashapi.cocmodels.clanwar.league.WarLeagueGroup;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.core.ClanWarStats;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.RoundWarInfo;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

    public static void dispatch(MessageReceivedEvent event, String... args) {
        CompletableFuture.runAsync(() -> {
            if (args.length <= 1) {
                WarLeagueCommand.executeClan(event);
                return;
            }

            if (args[1].equals("round")) {
                if (args.length > 3)
                    WarLeagueCommand.executeRound(event, args[2], args[3]);
                else if (args.length == 3)
                    WarLeagueCommand.executeRound(event, args[2]);
                else {
                    String prefix = DatabaseUtils.getServerPrefix(event.getGuild().getIdLong());
                    ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
                    ErrorUtils.sendError(event.getChannel(),
                            i18n.getString("wrong.usage"),
                            MessageFormat.format(i18n.getString("tip.usage"),
                                    Command.WARLEAGUE_ROUND.formatFullCommand(prefix)));
                }
            } else if (args[1].equals("all")) {
                if (args.length > 2)
                    WarLeagueCommand.executeAll(event, args[2]);
                else
                    WarLeagueCommand.executeAll(event);
            } else
                WarLeagueCommand.executeClan(event, args[1]);
        });
    }

    public static List<WarInfo> getWars(WarLeagueGroup leagueGroup, int roundIndex) {
        List<WarInfo> wars = new ArrayList<>();
        Round round = leagueGroup.getRounds().get(roundIndex);

        for (String warTag : round.getWarTags()) {
            WarInfo warInfo = null;
            try {
                warInfo = ClashBotMain.clashAPI.getCWLWar(warTag);
            } catch (IOException | ClashAPIException ignored) {
            }

            wars.add(warInfo);
        }
        return wars;
    }

    public static void drawRound(Graphics2D g2d, MessageReceivedEvent event, List<WarInfo> wars, ResourceBundle i18n, int roundIndex) {
        Font font = g2d.getFont().deriveFont(FONT_SIZE);

        // Round label
        Rectangle stateLabel = new Rectangle(365, 30, 200, 25);
        Rectangle roundLabel = new Rectangle(35, 32, 200, 25);
        Rectangle timeRect = new Rectangle(645, 32, 300, 25);

        int[] timeLeft;
        WarInfo firstWar = wars.get(0);

        switch (firstWar.getState()) {
            case "preparation" -> {
                timeLeft = GameUtils.getTimeLeft(firstWar.getStartTime());
                g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/cwl-preparation.png"), 0, 0, null);
                DrawUtils.drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.timeleft"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 19f, Color.BLACK);
                DrawUtils.drawCenteredString(g2d, stateLabel, font.deriveFont(24f), i18n.getString("war.preparation"));
            }
            case "warEnded" -> {
                timeLeft = GameUtils.getTimeLeft(firstWar.getEndTime());
                g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/cwl-ended.png"), 0, 0, null);
                DrawUtils.drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.since"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 19f, Color.BLACK);
                DrawUtils.drawCenteredString(g2d, stateLabel, font.deriveFont(24f), i18n.getString("war.ended"));
            }
            case "notInWar" -> {
                ErrorUtils.sendError(event.getChannel(), i18n.getString("exception.warleague.notinwar"));
                return;
            }
            default -> {
                // inWar
                timeLeft = GameUtils.getTimeLeft(firstWar.getEndTime());
                g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/cwl-inwar.png"), 0, 0, null);
                DrawUtils.drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.timeleft"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 19f, Color.BLACK);
                DrawUtils.drawCenteredString(g2d, stateLabel, font.deriveFont(24f), i18n.getString("war.inwar"));
            }
        }
        DrawUtils.drawSimpleCenteredString(g2d, MessageFormat.format(i18n.getString("round.index"), roundIndex + 1), roundLabel, 22f, Color.BLACK);

        // Wars
        for (int i = 0; i < wars.size(); i++) {
            WarInfo war = wars.get(i);
            ClanWarModel clan1 = war.getClan();
            ClanWarModel clan2 = war.getEnemy();

            if (war.getState().equals("warEnded")) {
                if (clan2.getStars() > clan1.getStars() ||
                        (clan2.getStars().intValue() == clan1.getStars().intValue() &&
                                clan2.getDestructionPercentage() > clan1.getDestructionPercentage())) {
                    ClanWarModel tmp = clan1;
                    clan1 = clan2;
                    clan2 = tmp;
                }
            }

            if (clan1 != null && clan2 != null) {
                // Drawing stars
                Rectangle rectStarClan1 = new Rectangle(383, 93 + i * 60, 50, 20);
                Rectangle rectStarClan2 = new Rectangle(503, 93 + i * 60, 50, 20);
                DrawUtils.drawCenteredString(g2d, rectStarClan1, font.deriveFont(18f), clan1.getStars().toString());
                DrawUtils.drawCenteredString(g2d, rectStarClan2, font.deriveFont(18f), clan2.getStars().toString());

                if (firstWar.getState().equals("inWar")) {
                    // Drawing clan names
                    DrawUtils.drawShadowedStringLeft(g2d, clan1.getName(), 255, 113 + i * 60, 16f, Color.WHITE);
                    DrawUtils.drawShadowedString(g2d, clan2.getName(), 670, 113 + i * 60, 16f);

                    // Drawing clan badges
                    g2d.drawImage(FileUtils.getImageFromUrl(clan1.getBadgeUrls().getSmall()), 270, 85 + i * 60, 40, 40, null);
                    g2d.drawImage(FileUtils.getImageFromUrl(clan2.getBadgeUrls().getSmall()), 620, 85 + i * 60, 40, 40, null);

                    // Drawing clan attacks
                    DrawUtils.drawShadowedString(g2d, clan2.getAttacks().toString(), 350, 109 + i * 60, 12f);
                    DrawUtils.drawShadowedString(g2d, clan1.getAttacks().toString(), 569, 109 + i * 60, 12f);
                } else {
                    // Drawing clan names
                    DrawUtils.drawShadowedStringLeft(g2d, clan1.getName(), 300, 113 + i * 60, 16f, Color.WHITE);
                    DrawUtils.drawShadowedString(g2d, clan2.getName(), 625, 113 + i * 60, 16f);

                    // Drawing clan badges
                    g2d.drawImage(FileUtils.getImageFromUrl(clan1.getBadgeUrls().getSmall()), 320, 85 + i * 60, 40, 40, null);
                    g2d.drawImage(FileUtils.getImageFromUrl(clan2.getBadgeUrls().getSmall()), 570, 85 + i * 60, 40, 40, null);
                }
            }
        }
    }

    public static void updateStats(ClanWarModel clan, HashMap<String, ClanWarStats> stats) {
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
                WarInfo warInfo = roundWars.getWars().get(j);
                updateStats(warInfo.getClan(), stats);
                updateStats(warInfo.getEnemy(), stats);
            }
        }
    }

    public static WarLeagueGroup getLeagueGroup(MessageReceivedEvent event, Locale lang, String[] args) {
        // If rate limitation has exceeded
        if (!CoreUtils.checkThrottle(event, lang))
            return null;

        WarLeagueGroup leagueGroup = null;
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        String tag = args.length > 1 ? args[1] : DatabaseUtils.getClanTag(event.getAuthor().getIdLong());

        if (tag == null) {
            ErrorUtils.sendError(event.getChannel(), i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
            return null;
        }

        try {
            leagueGroup = ClashBotMain.clashAPI.getCWLGroup(tag);
        } catch (IOException ignored) {
        } catch (ClashAPIException e) {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "warleague");
            return null;
        }
        return leagueGroup;
    }

    public static void executeRound(MessageReceivedEvent event, String... args) {
        Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        // Checking index validity
        int index = ErrorUtils.checkIndex(event, i18n, args[0], 7);
        if (index == -1)
            return;

        WarLeagueGroup warLeague = getLeagueGroup(event, lang, args);
        if (warLeague == null)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(ROUND_WIDTH, ROUND_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = DrawUtils.initGraphics(ROUND_WIDTH, ROUND_HEIGHT, image);
        Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        // Rounds
        List<WarInfo> roundWars = getWars(warLeague, index - 1);
        drawRound(g2d, event, roundWars, i18n, index - 1);

        FileUtils.sendImage(event, image);
        g2d.dispose();
    }

    public static ClanWarModel getWinner(ClanWarModel clan1, ClanWarModel clan2) {
        if (clan1.getStars() > clan2.getStars())
            return clan1;
        else if (clan1.getStars().intValue() == clan2.getStars().intValue()) {
            // when stars are equal
            if (clan1.getDestructionPercentage() > clan2.getDestructionPercentage())
                return clan1;
            else if (clan1.getDestructionPercentage() < clan2.getDestructionPercentage())
                return clan2;
            return null; // when stars and destruction percentages are equal
        }
        return clan2;
    }

    public static int getWinStars(ClanWarModel clan1, ClanWarModel clan2, WarInfo war) {
        if (!war.getState().equals("warEnded")) {
            if (getWinner(clan1, clan2) == null)
                return 0;

            if (Objects.equals(getWinner(clan1, clan2), clan1))
                return 10;
        }
        return 0;
    }

    public static void executeAll(MessageReceivedEvent event, String... args) {
        Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);

        WarLeagueGroup warLeague = getLeagueGroup(event, lang, args);
        if (warLeague == null)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(ROUND_WIDTH, ROUND_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = DrawUtils.initGraphics(ROUND_WIDTH, ROUND_HEIGHT, image);
        Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        HashMap<String, Integer> stars = new HashMap<>();
        HashMap<String, Integer> destruction = new HashMap<>();

        for (ClanWarModel clan : warLeague.getClans()) {
            stars.put(clan.getName(), 0);
            destruction.put(clan.getName(), 0);
        }
        for (int i = 0; i < 7; i++) {
            List<WarInfo> wars = getWars(warLeague, i);
            for (WarInfo war : wars) {
                ClanWarModel clan1 = war.getClan();
                ClanWarModel clan2 = war.getEnemy();

                System.out.println(clan1.getName() + " " + clan1.getStars() + " vs. " + clan2.getName() + " " + clan2.getStars());

                stars.put(clan1.getName(), stars.get(clan1.getName()) + clan1.getStars() + getWinStars(clan1, clan2, war));
                stars.put(clan2.getName(), stars.get(clan2.getName()) + clan2.getStars() + getWinStars(clan2, clan1, war));
                destruction.put(clan1.getName(), destruction.get(clan1.getName()) + clan1.getDestructionPercentage().intValue());
                destruction.put(clan2.getName(), destruction.get(clan2.getName()) + clan2.getDestructionPercentage().intValue());
            }
            System.out.println();
        }

        for (ClanWarModel clan : warLeague.getClans()) {
            System.out.println(clan.getName() + ": " + stars.get(clan.getName()) + " étoiles (" + destruction.get(clan.getName()) + "%)");
        }

        FileUtils.sendImage(event, image);
        g2d.dispose();
    }

    public static void executeClan(MessageReceivedEvent event, String... args) {

    }
}
