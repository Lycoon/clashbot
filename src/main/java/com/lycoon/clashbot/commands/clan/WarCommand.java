package com.lycoon.clashbot.commands.clan;

import com.lycoon.clashapi.cocmodels.clanwar.Attack;
import com.lycoon.clashapi.cocmodels.clanwar.ClanWarMember;
import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.core.CacheComponents;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WarCommand {
    private static String tag;

    private final static int PADDING = 145;
    private final static int SIZE = 5;
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 894;
    private final static float FONT_SIZE = 16f;

    private static List<ClanWarMember> members, enemyMembers;
    private static List<Attack> sortedAttacks, sortedEnemyAttacks;
    private static ResourceBundle i18n;
    private final static Color backgroundColor = new Color(0xe7e7e1);
    private final static Color clanNameColor = new Color(0xfeffaf);
    private final static Color notUsedAttackColor = new Color(0xfbbf70);
    private final static Color attackColor = new Color(0x4c493a);

    static class SortMemberByOrder implements Comparator<ClanWarMember> {
        @Override
        public int compare(ClanWarMember a, ClanWarMember b) {
            return a.getMapPosition() - b.getMapPosition();
        }
    }

    static class SortAttackByOrder implements Comparator<Attack> {
        @Override
        public int compare(Attack a, Attack b) {
            return a.getOrder() - b.getOrder();
        }
    }

    public static void dispatch(MessageReceivedEvent event, String... args) {
        String prefix = DatabaseUtils.getServerPrefix(event.getGuild().getIdLong());
        Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
        i18n = LangUtils.getTranslations(lang);

        CompletableFuture.runAsync(() -> {
            if (args.length > 2)
                execute(event, lang, args[1], args[2]);
            else if (args.length == 2)
                execute(event, lang, args[1]);
            else
                ErrorUtils.sendError(event.getChannel(), i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("tip.usage"), Command.WAR.formatFullCommand(prefix)));
        });
    }

    public static ClanWarMember getClanWarMemberByTag(List<ClanWarMember> members, String tag) {
        for (ClanWarMember member : members) {
            if (member.getTag().equals(tag))
                return member;
        }
        return null;
    }

    public static List<Attack> getAttacksByOrder(List<ClanWarMember> members) {
        List<Attack> sortedAttacks = new ArrayList<>();
        for (ClanWarMember member : members) {
            List<Attack> attacks = member.getAttacks();
            if (attacks != null)
                sortedAttacks.addAll(attacks);
        }
        sortedAttacks.sort(new SortAttackByOrder());
        return sortedAttacks;
    }

    public static int getHighestStars(List<Attack> attacks, Attack atk) {
        int max = 0;
        for (int i = 0; i < attacks.size() && attacks.get(i).getOrder() < atk.getOrder(); i++) {
            Attack curr = attacks.get(i);
            if (curr.getDefenderTag().equals(atk.getDefenderTag())) {
                if (curr.getStars() > max)
                    max = curr.getStars();
            }
        }
        return max;
    }

    public static int getNewStars(List<Attack> attacks, Attack attack) {
        int highestStars = getHighestStars(attacks, attack);
        if (attack.getStars() > highestStars)
            return attack.getStars() - highestStars;
        return 0;
    }

    public static int drawMemberResults(Graphics2D g2d, ClanWarMember member, List<ClanWarMember> opponentMembers, List<Attack> opponentAttacks, boolean rightSide, int y) {
        int stars = 0;
        List<Attack> attacks = member.getAttacks();
        for (int j = 0; j < 2; j++) {
            if (!rightSide)
                DrawUtils.drawSimpleString(g2d, MessageFormat.format(i18n.getString("attack.index"), j + 1), 105, y + 55 + j * 47, 8f, attackColor);
            else
                DrawUtils.drawSimpleStringLeft(g2d, MessageFormat.format(i18n.getString("attack.index"), j + 1), 1100, y + 55 + j * 47, 8f, attackColor);

            if (attacks == null) {
                if (!rightSide)
                    DrawUtils.drawShadowedString(g2d, i18n.getString("not.used"), 105, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                else
                    DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("not.used"), 1100, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                continue;
            }

            if (j >= attacks.size()){
                if (!rightSide)
                    DrawUtils.drawShadowedString(g2d, i18n.getString("not.used"), 105, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                else
                    DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("not.used"), 1100, y + 76 + j * 46, 15f, 2, notUsedAttackColor);
                continue;
            }

            // If the player made at least one attack
            Attack attack = attacks.get(j);
            ClanWarMember defender = getClanWarMemberByTag(opponentMembers, attack.getDefenderTag());

            if (!rightSide) {
                DrawUtils.drawShadowedString(g2d, defender != null ? defender.getMapPosition() + ". " + defender.getName() : "Unknown", 105, y + 78 + j * 46, 16f);
                DrawUtils.drawSimpleStringLeft(g2d, attack.getDestructionPercentage().longValue() + "%", 370, y + 70 + j * 47, 16f, Color.BLACK);
            } else {
                DrawUtils.drawShadowedStringLeft(g2d, defender != null ? defender.getMapPosition() + ". " + defender.getName() : "Unknown", 1100, y + 78 + j * 46, 16f);
                DrawUtils.drawSimpleString(g2d, attack.getDestructionPercentage().longValue() + "%", 840, y + 70 + j * 47, 16f, Color.BLACK);
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

    public static void drawMapPosition(Graphics2D g2d, ClanWarMember member, ClanWarMember enemy, int y) {
        g2d.setColor(Color.WHITE);

        // Username
        Rectangle usernameRect1 = new Rectangle(65, y + 7, 100, 20);
        Rectangle usernameRect2 = new Rectangle(1035, y + 7, 100, 20);
        DrawUtils.drawCenteredString(g2d, usernameRect1, g2d.getFont().deriveFont(18f), member.getName());
        DrawUtils.drawCenteredString(g2d, usernameRect2, g2d.getFont().deriveFont(18f), enemy.getName());

        // Townhall
        g2d.drawImage(CacheComponents.getTownHallImage(member.getTownhallLevel()), 15, y + 47, 75, 75, null);
        g2d.drawImage(CacheComponents.getTownHallImage(enemy.getTownhallLevel()), 1110, y + 47, 75, 75, null);

        // Map position
        Rectangle posRect = new Rectangle(0, y + 73, WIDTH, 20);
        DrawUtils.drawCenteredString(g2d, posRect, g2d.getFont().deriveFont(32f), member.getMapPosition() + ".");

        // Stars
        Rectangle starRect1 = new Rectangle(475, y + 70, 30, 30);
        Rectangle starRect2 = new Rectangle(655, y + 70, 30, 30);
        DrawUtils.drawCenteredString(g2d, starRect1, g2d.getFont().deriveFont(26f), String.valueOf(drawMemberResults(g2d, member, enemyMembers, sortedEnemyAttacks, false, y)));
        DrawUtils.drawCenteredString(g2d, starRect2, g2d.getFont().deriveFont(26f), String.valueOf(drawMemberResults(g2d, enemy, members, sortedAttacks, true, y)));
    }

    public static WarInfo getWar(MessageReceivedEvent event, Locale lang, String[] args) {
        // If rate limitation has exceeded
        if (!CoreUtils.checkThrottle(event, lang))
            return null;

        WarInfo war = null;
        tag = args.length > 1 ? args[1] : DatabaseUtils.getClanTag(event.getAuthor().getIdLong());

        if (tag == null) {
            ErrorUtils.sendError(event.getChannel(), i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
            return null;
        }

        try {
            war = ClashBotMain.clashAPI.getCurrentWar(tag);
        } catch (IOException ignored) {
        } catch (ClashAPIException e) {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "war");
            return null;
        }
        return war;
    }

    public static void execute(MessageReceivedEvent event, Locale lang, String... args) {
        MessageChannel channel = event.getChannel();
        WarInfo war = getWar(event, lang, args);
        if (war == null)
            return;

        if (!war.getState().equals("notInWar")) {
            // Checking index validity
            int index = ErrorUtils.checkIndex(event, i18n, args[0], war.getTeamSize() / 5);
            if (index == -1)
                return;

            // Initializing image
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = DrawUtils.initGraphics(WIDTH, HEIGHT, image);
            Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
            g2d.setFont(font);

            members = war.getClan().getWarMembers();
            enemyMembers = war.getEnemy().getWarMembers();

            members.sort(new SortMemberByOrder());
            enemyMembers.sort(new SortMemberByOrder());
            sortedAttacks = getAttacksByOrder(members);
            sortedEnemyAttacks = getAttacksByOrder(enemyMembers);

            // Color background
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Image background
            g2d.drawImage(FileUtils.getImageFromFile("backgrounds/clanwar/war-background.png"), 0, 0, null);

            // Clan badges
            g2d.drawImage(FileUtils.getImageFromUrl(war.getClan().getBadgeUrls().getSmall()), 20, 20, 135, 135, null);
            g2d.drawImage(FileUtils.getImageFromUrl(war.getEnemy().getBadgeUrls().getSmall()), 1050, 20, 135, 135, null);

            // Clan names
            DrawUtils.drawShadowedString(g2d, war.getClan().getName(), 165, 70, 32f, 2, clanNameColor);
            DrawUtils.drawShadowedStringLeft(g2d, war.getEnemy().getName(), 1030, 70, 32f, 2, clanNameColor);

            // Status
            Rectangle statusRect = new Rectangle(0, 55, WIDTH, 20);
            Rectangle timeRect = new Rectangle(0, 95, WIDTH, 20);
            int[] timeLeft;
            switch (war.getState()) {
                case "inWar" -> {
                    timeLeft = GameUtils.getTimeLeft(war.getEndTime());
                    DrawUtils.drawSimpleCenteredString(g2d, i18n.getString("war.inwar"), statusRect, 30f, Color.BLACK);
                    DrawUtils.drawSimpleCenteredString(g2d,
                            MessageFormat.format(i18n.getString("war.timeleft"),
                                    MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                            timeRect, 22f, Color.BLACK);
                }
                case "preparation" -> {
                    timeLeft = GameUtils.getTimeLeft(war.getStartTime());
                    DrawUtils.drawSimpleCenteredString(g2d, i18n.getString("war.preparation"), statusRect, 30f, Color.BLACK);
                    DrawUtils.drawSimpleCenteredString(g2d,
                            MessageFormat.format(i18n.getString("war.timeleft"),
                                    MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])),
                            timeRect, 22f, Color.BLACK);
                }
                default -> {
                    //warEnded
                    timeLeft = GameUtils.getTimeLeft(war.getEndTime());
                    DrawUtils.drawSimpleCenteredString(g2d, i18n.getString("war.ended"), statusRect, 30f, Color.BLACK);
                    DrawUtils.drawSimpleCenteredString(g2d,
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

            DrawUtils.drawCenteredString(g2d, clanStarRect1, font.deriveFont(28f), String.valueOf(war.getClan().getStars()));
            DrawUtils.drawCenteredString(g2d, clanStarRect2, font.deriveFont(28f), String.valueOf(war.getEnemy().getStars()));

            // Total destruction percentage
            Rectangle destructionRect1 = new Rectangle(290, 102, 150, 20);
            Rectangle destructionRect2 = new Rectangle(758, 102, 150, 20);

            DecimalFormatSymbols dfs = new DecimalFormatSymbols(lang);
            DecimalFormat df = new DecimalFormat("#.#", dfs);
            DrawUtils.drawSimpleCenteredString(g2d, df.format(war.getClan().getDestructionPercentage()) + "%", destructionRect1, 26f, Color.BLACK);
            DrawUtils.drawSimpleCenteredString(g2d, df.format(war.getEnemy().getDestructionPercentage()) + "%", destructionRect2, 26f, Color.BLACK);

            FileUtils.sendImage(event, image);
            g2d.dispose();
        } else
            ErrorUtils.sendError(channel, MessageFormat.format(i18n.getString("exception.404.war"), tag));
    }
}
