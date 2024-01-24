package com.lycoon.clashbot.commands.clan;

import static com.lycoon.clashbot.utils.DrawUtils.*;
import static com.lycoon.clashbot.utils.FileUtils.*;
import static com.lycoon.clashbot.utils.ErrorUtils.*;
import static com.lycoon.clashbot.utils.database.DatabaseUtils.*;
import static com.lycoon.clashbot.utils.CoreUtils.*;
import static com.lycoon.clashbot.utils.GameUtils.*;

import com.lycoon.clashapi.core.exceptions.ClashAPIException;
import com.lycoon.clashapi.models.war.WarAttack;
import com.lycoon.clashapi.models.war.WarMember;
import com.lycoon.clashapi.models.war.War;
import com.lycoon.clashapi.models.war.enums.WarState;
import com.lycoon.clashbot.commands.CommandData;
import com.lycoon.clashbot.core.CacheComponents;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarCommand {
    private static String tag;
    private static ResourceBundle i18n;
    private static Locale lang;

    private final static int PADDING = 145;
    private final static int SIZE = 5;
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 894;
    private final static float FONT_SIZE = 16f;

    private static List<WarMember> members, enemyMembers;
    private static List<WarAttack> sortedAttacks, sortedEnemyAttacks;
    private final static Color backgroundColor = new Color(0xe7e7e1);
    private final static Color clanNameColor = new Color(0xfeffaf);
    private final static Color notUsedAttackColor = new Color(0xfbbf70);
    private final static Color attackColor = new Color(0x4c493a);

    static class SortMemberByOrder implements Comparator<WarMember>
    {
        @Override
        public int compare(WarMember a, WarMember b) {
            return a.getMapPosition() - b.getMapPosition();
        }
    }

    static class SortAttackByOrder implements Comparator<WarAttack>
    {
        @Override
        public int compare(WarAttack a, WarAttack b) {
            return a.getOrder() - b.getOrder();
        }
    }

    public static void call(SlashCommandInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getOptions().isEmpty()) {
                i18n = LangUtils.getTranslations(event.getMember().getIdLong());
                sendError(event, i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("tip.usage"), "prefix"));
                return;
            }

            if (event.getOptions().size() == 1)
                execute(event, Objects.requireNonNull(event.getOption("page")).getAsString());
            else
                execute(event, Objects.requireNonNull(event.getOption("page")).getAsString(), Objects.requireNonNull(event.getOption("clan_tag")).getAsString());
        });
    }

    public static WarMember getClanWarMemberByTag(List<WarMember> members, String tag) {
        for (WarMember member : members) {
            if (member.getTag().equals(tag))
                return member;
        }
        return null;
    }

    public static List<WarAttack> getAttacksByOrder(List<WarMember> members) {
        List<WarAttack> sortedAttacks = new ArrayList<>();
        for (WarMember member : members) {
            List<WarAttack> attacks = member.getAttacks();
            sortedAttacks.addAll(attacks);
        }
        sortedAttacks.sort(new SortAttackByOrder());
        return sortedAttacks;
    }

    public static int getHighestStars(List<WarAttack> attacks, WarAttack atk) {
        int max = 0;
        for (int i = 0; i < attacks.size() && attacks.get(i).getOrder() < atk.getOrder(); i++) {
            WarAttack curr = attacks.get(i);
            if (curr.getDefenderTag().equals(atk.getDefenderTag())) {
                if (curr.getStars() > max)
                    max = curr.getStars();
            }
        }
        return max;
    }

    public static int getNewStars(List<WarAttack> attacks, WarAttack attack) {
        int highestStars = getHighestStars(attacks, attack);
        if (attack.getStars() > highestStars)
            return attack.getStars() - highestStars;
        return 0;
    }

    public static int drawMemberResults(Graphics2D g2d, WarMember member, List<WarMember> opponentMembers, List<WarAttack> opponentAttacks, boolean rightSide, int y) {
        int stars = 0;
        List<WarAttack> attacks = member.getAttacks();
        for (int j = 0; j < 2; j++) {
            if (!rightSide)
                drawSimpleString(g2d, MessageFormat.format(i18n.getString("attack.index"), j + 1), 105, y + 55 + j * 47, 8f, attackColor);
            else
                drawSimpleStringLeft(g2d, MessageFormat.format(i18n.getString("attack.index"), j + 1), 1100, y + 55 + j * 47, 8f, attackColor);

            if (attacks.isEmpty()) {
                if (!rightSide)
                    drawShadowedString(g2d, i18n.getString("not.used"), 105, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                else
                    drawShadowedStringLeft(g2d, i18n.getString("not.used"), 1100, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                continue;
            }

            if (j >= attacks.size()){
                if (!rightSide)
                    drawShadowedString(g2d, i18n.getString("not.used"), 105, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                else
                    drawShadowedStringLeft(g2d, i18n.getString("not.used"), 1100, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                continue;
            }

            // If the player made at least one attack
            WarAttack attack = attacks.get(j);
            WarMember defender = getClanWarMemberByTag(opponentMembers, attack.getDefenderTag());

            if (!rightSide) {
                drawShadowedString(g2d, defender != null ? defender.getMapPosition() + ". " + defender.getName() : "Unknown", 105, y + 78 + j * 46, 16f);
                drawSimpleStringLeft(g2d, attack.getDestructionPercentage() + "%", 370, y + 70 + j * 47, 16f, Color.BLACK);
            } else {
                drawShadowedStringLeft(g2d, defender != null ? defender.getMapPosition() + ". " + defender.getName() : "Unknown", 1100, y + 78 + j * 46, 16f);
                drawSimpleString(g2d, attack.getDestructionPercentage() + "%", 840, y + 70 + j * 47, 16f, Color.BLACK);
            }

            int newStars = getNewStars(opponentAttacks, attack);

            // Stars
            for (int starIndex = 0; starIndex < 3; starIndex++) {
                if (starIndex + 1 > attack.getStars()) {
                    g2d.drawImage(CacheComponents.noStar, (rightSide ? 750 : 380) + starIndex * 26, y + 48 + j * 45, 28, 28, null);
                    continue;
                }

                if (starIndex + 1 <= attack.getStars() - newStars)
                    g2d.drawImage(CacheComponents.alreadyStar, (rightSide ? 750 : 380) + starIndex * 26, y + 48 + j * 45, 28, 28, null);
                else {
                    stars++;
                    g2d.drawImage(CacheComponents.newStar, (rightSide ? 750 : 380) + starIndex * 26, y + 48 + j * 45, 28, 28, null);
                }
            }
        }
        return stars;
    }

    public static void drawMapPosition(Graphics2D g2d, WarMember member, WarMember enemy, int y) {
        g2d.setColor(Color.WHITE);

        // Username
        Rectangle usernameRect1 = new Rectangle(65, y + 7, 100, 20);
        Rectangle usernameRect2 = new Rectangle(1035, y + 7, 100, 20);
        drawCenteredString(g2d, usernameRect1, g2d.getFont().deriveFont(18f), member.getName());
        drawCenteredString(g2d, usernameRect2, g2d.getFont().deriveFont(18f), enemy.getName());

        // Townhall
        g2d.drawImage(CacheComponents.getTownHallImage(member.getTownhallLevel()), 15, y + 47, 75, 75, null);
        g2d.drawImage(CacheComponents.getTownHallImage(enemy.getTownhallLevel()), 1110, y + 47, 75, 75, null);

        // Map position
        Rectangle posRect = new Rectangle(0, y + 73, WIDTH, 20);
        drawCenteredString(g2d, posRect, g2d.getFont().deriveFont(32f), member.getMapPosition() + ".");

        // Stars
        Rectangle starRect1 = new Rectangle(475, y + 70, 30, 30);
        Rectangle starRect2 = new Rectangle(655, y + 70, 30, 30);
        drawCenteredString(g2d, starRect1, g2d.getFont().deriveFont(26f), String.valueOf(drawMemberResults(g2d, member, enemyMembers, sortedEnemyAttacks, false, y)));
        drawCenteredString(g2d, starRect2, g2d.getFont().deriveFont(26f), String.valueOf(drawMemberResults(g2d, enemy, members, sortedAttacks, true, y)));
    }

    public static War getWar(SlashCommandInteractionEvent event, Locale lang, String[] args) {
        // If rate limitation has exceeded
        if (!checkThrottle(event, lang))
            return null;

        War war = null;
        tag = args.length > 1 ? args[1] : getClanTag(event.getMember().getIdLong());

        if (tag == null) {
            sendError(event, i18n.getString("set.clan.error"),
                    MessageFormat.format(i18n.getString("cmd.general.tip"), CommandData.SET_CLAN.formatCommand()));
            return null;
        }

        try { war = ClashBotMain.clashAPI.getCurrentWar(tag); }
        catch (ClashAPIException | IOException e)
        {
            sendExceptionError(event, i18n, e, tag, "war");
            return null;
        }

        return war;
    }

    public static void execute(SlashCommandInteractionEvent event, String... args) {
        lang = LangUtils.getLanguage(event.getMember().getIdLong());
        i18n = LangUtils.getTranslations(lang);

        War war = getWar(event, lang, args);
        if (Objects.requireNonNull(war).getState() == WarState.NOT_IN_WAR)
        {
            sendError(event, MessageFormat.format(i18n.getString("exception.404.war"), tag));
            return;
        }

        // Checking index validity
        int index = checkIndex(event, i18n, args[0], war.getTeamSize() / 5);
        if (index == -1)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = initGraphics(WIDTH, HEIGHT, image);
        Font font = getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        members = war.getClan().getMembers();
        enemyMembers = war.getOpponent().getMembers();

        members.sort(new SortMemberByOrder());
        enemyMembers.sort(new SortMemberByOrder());
        sortedAttacks = getAttacksByOrder(members);
        sortedEnemyAttacks = getAttacksByOrder(enemyMembers);

        // Color background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Image background
        g2d.drawImage(getImageFromFile("backgrounds/clanwar/war-background.png"), 0, 0, null);

        // Clan badges
        g2d.drawImage(getImageFromUrl(war.getClan().getBadgeUrls().getSmall()), 20, 20, 135, 135, null);
        g2d.drawImage(getImageFromUrl(war.getOpponent().getBadgeUrls().getSmall()), 1050, 20, 135, 135, null);

        // Clan names
        drawShadowedString(g2d, war.getClan().getName(), 165, 70, 32f, 2, clanNameColor);
        drawShadowedStringLeft(g2d, war.getOpponent().getName(), 1030, 70, 32f, 2, clanNameColor);

        // Status
        Rectangle statusRect = new Rectangle(0, 55, WIDTH, 20);
        Rectangle timeRect = new Rectangle(0, 95, WIDTH, 20);
        int[] timeLeft;
        switch (war.getState())
        {
            case IN_WAR -> {
                timeLeft = getTimeLeft(war.getEndTime());
                drawSimpleCenteredString(g2d, i18n.getString("war.inwar"), statusRect, 30f, Color.BLACK);
                drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.timeleft"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 22f, Color.BLACK);
            }
            case PREPARATION -> {
                timeLeft = getTimeLeft(war.getStartTime());
                drawSimpleCenteredString(g2d, i18n.getString("war.preparation"), statusRect, 30f, Color.BLACK);
                drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.timeleft"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 22f, Color.BLACK);
            }
            default -> {
                //warEnded
                timeLeft = getTimeLeft(war.getEndTime());
                drawSimpleCenteredString(g2d, i18n.getString("war.ended"), statusRect, 30f, Color.BLACK);
                drawSimpleCenteredString(g2d,
                        MessageFormat.format(i18n.getString("war.since"),
                                MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                        timeRect, 22f, Color.BLACK);
            }
        }

        for (int i = 0; i < SIZE; i++)
            drawMapPosition(g2d, members.get(i + (index - 1) * SIZE), enemyMembers.get(i + (index - 1) * SIZE), 168 + i * PADDING);

        // Total clan stars
        Rectangle clanStarRect1 = new Rectangle(110, 100, 200, 20);
        Rectangle clanStarRect2 = new Rectangle(890, 100, 200, 20);

        drawCenteredString(g2d, clanStarRect1, font.deriveFont(28f), String.valueOf(war.getClan().getStars()));
        drawCenteredString(g2d, clanStarRect2, font.deriveFont(28f), String.valueOf(war.getOpponent().getStars()));

        // Total destruction percentage
        Rectangle destructionRect1 = new Rectangle(290, 102, 150, 20);
        Rectangle destructionRect2 = new Rectangle(758, 102, 150, 20);

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(lang);
        DecimalFormat df = new DecimalFormat("#.#", dfs);
        drawSimpleCenteredString(g2d, df.format(war.getClan().getDestructionPercentage()) + "%", destructionRect1, 26f, Color.BLACK);
        drawSimpleCenteredString(g2d, df.format(war.getOpponent().getDestructionPercentage()) + "%", destructionRect2, 26f, Color.BLACK);

        sendImage(event, image);
        g2d.dispose();
    }
}
