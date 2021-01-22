package com.lycoon.clashbot.commands;

import com.lycoon.clashapi.cocmodels.player.Player;
import com.lycoon.clashapi.cocmodels.player.Troop;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.CacheComponents;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.utils.ErrorUtils;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlayerCommand
{
    private final static int WIDTH = 932;
    private final static int HEIGHT = 322;
    private final static float FONT_SIZE = 12f;
    private final static int ARMY_BASE_LINE = 232;

    private final static String[] TROOPS = {"Barbarian", "Archer", "Giant", "Goblin", "Wall Breaker", "Balloon",
            "Wizard", "Healer", "Dragon", "P.E.K.K.A", "Baby Dragon", "Miner", "Electro Dragon", "Yeti"};
    private final static String[] DARK_TROOPS = {"Minion", "Hog Rider", "Valkyrie", "Golem", "Witch", "Lava Hound",
            "Bowler", "Ice Golem", "Headhunter"};
    private final static String[] HEROES = {"Barbarian King", "Archer Queen", "Grand Warden", "Royal Champion"};
    private final static String[] SPELLS = {"Lightning Spell", "Healing Spell", "Rage Spell", "Jump Spell",
            "Freeze Spell", "Invisibility Spell", "Clone Spell", "Poison Spell", "Earthquake Spell", "Haste Spell",
            "Skeleton Spell", "Bat Spell"};
    private final static String[] MACHINES = {"Wall Wrecker", "Battle Blimp", "Stone Slammer", "Siege Barracks"};

    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        if (args.length > 1)
            PlayerCommand.execute(event, args[1]);
        else
            PlayerCommand.execute(event);
    }

    public static void drawTroop(Graphics2D g2d, Font font, List<Troop> troops, String troopName, int x, int y)
    {
        Troop troop = GameUtils.getTroopByName(troops, troopName);

        // If the player has not unlocked the troop yet
        if (troop == null)
            g2d.drawImage(FileUtils.getImageFromFile("troops/locked/" + troopName + ".png"), x, y, 35, 35, null);
        else
        {
            g2d.drawImage(FileUtils.getImageFromFile("troops/" + troop.getName() + ".png"), x, y, 35, 35, null);
            if (troop.getLevel().intValue() == troop.getMaxLevel().intValue())
                g2d.drawImage(FileUtils.getImageFromFile("icons/level-label-max.png"), x + 2, y + 18, 15, 15, null);
            else
            {
                if (troop.getLevel() != 1)
                    g2d.drawImage(FileUtils.getImageFromFile("icons/level-label.png"), x + 2, y + 18, 15, 15, null);
            }
            if (troop.getLevel() != 1)
            {
                Rectangle levelRect = new Rectangle(x + 2, y + 18, 15, 15);
                DrawUtils.drawCenteredString(g2d, levelRect, font.deriveFont(font.getSize() - 4f), troop.getLevel().toString());
            }
        }
    }

    public static void drawTroops(Graphics2D g2d, Font font, List<Troop> troops, int y)
    {
        for (int i = 0; i < TROOPS.length; i++)
            drawTroop(g2d, font, troops, TROOPS[i], i * 38 + 20, y);
    }

    public static void drawDarkTroops(Graphics2D g2d, Font font, List<Troop> troops, int y)
    {
        for (int i = 0; i < DARK_TROOPS.length; i++)
            drawTroop(g2d, font, troops, DARK_TROOPS[i], i * 38 + 20, y + 37);
    }

    public static void drawHeroes(Graphics2D g2d, Font font, List<Troop> heroes, int y)
    {
        for (int i = 0; i < HEROES.length; i++)
            drawTroop(g2d, font, heroes, HEROES[i], i * 38 + 400, y + 37);
    }

    public static void drawSpells(Graphics2D g2d, Font font, List<Troop> spells, int y)
    {
        for (int i = 0; i < 6; i++)
            drawTroop(g2d, font, spells, SPELLS[i], i * 38 + 580, y);
        for (int i = 6; i < SPELLS.length; i++)
            drawTroop(g2d, font, spells, SPELLS[i], (i - 6) * 38 + 580, y + 37);
    }

    public static void drawMachines(Graphics2D g2d, Font font, List<Troop> machines, int y)
    {
        drawTroop(g2d, font, machines, MACHINES[0], 837, y);
        drawTroop(g2d, font, machines, MACHINES[1], 875, y);
        drawTroop(g2d, font, machines, MACHINES[2], 837, y + 37);
        drawTroop(g2d, font, machines, MACHINES[3], 875, y + 37);
    }

    public static void execute(MessageReceivedEvent event, String... args)
    {
        MessageChannel channel = event.getChannel();

        Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        NumberFormat nf = NumberFormat.getInstance(lang);

        // Checking rate limitation
        if (!CoreUtils.checkThrottle(event, lang))
            return;

        Player player = null;
        String tag = args.length > 0 ? args[0] : DBUtils.getPlayerTag(event.getAuthor().getIdLong());

        if (tag == null)
        {
            ErrorUtils.sendError(channel, i18n.getString("set.player.error"), i18n.getString("set.player.help"));
            return;
        }

        try
        {
            player = ClashBotMain.clashAPI.getPlayer(tag);
        } catch (IOException ignored)
        {
        } catch (ClashAPIException e)
        {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "player");
            return;
        }

        // Initializing image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = DrawUtils.initGraphics(WIDTH, HEIGHT, image);

        Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        g2d.drawImage(FileUtils.getImageFromFile("backgrounds/player-profile.png"), 0, 0, null);

        // Experience level
        g2d.drawImage(FileUtils.getImageFromFile("icons/exp-star.png"), 20, 18, 45, 45, null);
        Rectangle level = new Rectangle(23, 30, 40, 20);
        DrawUtils.drawCenteredString(g2d, level, font.deriveFont(FONT_SIZE + 5f), Objects.requireNonNull(player).getExpLevel().toString());

        // Nickname
        DrawUtils.drawShadowedString(g2d, player.getName(), 75, 36, FONT_SIZE + 8f);

        // Player tag
        DrawUtils.drawShadowedString(g2d, player.getTag(), 75, 55, FONT_SIZE - 1f);

        // Townhall
        g2d.drawImage(CacheComponents.getTownHallImage(player.getTownHallLevel()), 80, 80, 100, 100, null);
        DrawUtils.drawShadowedString(g2d, i18n.getString("townhall"), 25, 125, FONT_SIZE - 2f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("level") + " " + player.getTownHallLevel(), 25, 150, FONT_SIZE + 8f);

        // Builder hall
        if (player.getBuilderHallLevel() != null)
        {
            g2d.drawImage(CacheComponents.getBuilderHallImage(player.getBuilderHallLevel()), 265, 85, 95, 95, null);
            DrawUtils.drawShadowedString(g2d, i18n.getString("level") + " " + player.getBuilderHallLevel(), 200, 150, FONT_SIZE + 8f);
        }
        else
        {
            // In case the player has not built the builder hall yet
            DrawUtils.drawShadowedString(g2d, i18n.getString("no.builderhall"), 200, 150, FONT_SIZE + 8f);
        }
        DrawUtils.drawShadowedString(g2d, i18n.getString("builderhall"), 200, 125, FONT_SIZE - 2f);

        // League
        if (player.getLeague() != null)
            g2d.drawImage(FileUtils.getImageFromUrl(player.getLeague().getIconUrls().getMedium()), 383, 30, 90, 90, null);
        else
        {
            g2d.drawImage(FileUtils.getImageFromFile("icons/noleague.png"), 383, 30, 90, 90, null);
            Rectangle noLeagueRect = new Rectangle(375, 60, 105, 20);
            DrawUtils.drawCenteredString(g2d, noLeagueRect, font.deriveFont(FONT_SIZE - 4f), i18n.getString("no.league"));
        }

        // Clan
        if (player.getClan() != null)
        {
            g2d.drawImage(FileUtils.getImageFromUrl(player.getClan().getBadgeUrls().getLarge()), 800, 30, 105, 105, null);

            Rectangle clanNameRect = new Rectangle(775, 130, 148, 30);
            Rectangle clanRoleRect = new Rectangle(775, 151, 148, 30);
            DrawUtils.drawCenteredString(g2d, clanNameRect, font.deriveFont(FONT_SIZE + 2f), player.getClan().getName());
            DrawUtils.drawCenteredString(g2d, clanRoleRect, font.deriveFont(FONT_SIZE - 2f), i18n.getString(player.getRole()));
        }
        else
        {
            Rectangle noClanRect = new Rectangle(775, 130, 148, 30);
            DrawUtils.drawCenteredString(g2d, noClanRect, font.deriveFont(FONT_SIZE + 2f), i18n.getString("no.clan"));
            g2d.drawImage(FileUtils.getImageFromFile("icons/noclan.png"), 812, 40, 75, 75, null);
        }

        // Trophies
        Rectangle trophiesRect = new Rectangle(375, 148, 75, 24);
        DrawUtils.drawCenteredString(g2d, trophiesRect, font.deriveFont(FONT_SIZE + 4f), nf.format(player.getTrophies()));

        // Statistics
        DrawUtils.drawShadowedString(g2d, i18n.getString("season") + " " + GameUtils.getCurrentSeason(lang), 486, 45, FONT_SIZE + 3f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("attacks.won"), 486, 77, FONT_SIZE - 1.5f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("defenses.won"), 486, 107, FONT_SIZE - 1.5f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("donations"), 486, 143, FONT_SIZE - 1.5f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("donations.received"), 486, 173, FONT_SIZE - 1.5f);

        DrawUtils.drawSimpleString(g2d, nf.format(player.getAttackWins()), 693, 79, FONT_SIZE, new Color(0x444545));
        DrawUtils.drawSimpleString(g2d, nf.format(player.getDefenseWins()), 693, 109, FONT_SIZE, new Color(0x444545));
        DrawUtils.drawSimpleString(g2d, nf.format(player.getDonations()), 693, 144, FONT_SIZE, new Color(0x444545));
        DrawUtils.drawSimpleString(g2d, nf.format(player.getDonationsReceived()), 693, 174, FONT_SIZE, new Color(0x444545));

        // Army
        DrawUtils.drawShadowedString(g2d, i18n.getString("army"), 21, 222, FONT_SIZE + 2f);

        // Troops
        List<Troop> troops = player.getTroops();
        List<Troop> heroes = player.getHeroes();
        List<Troop> spells = player.getSpells();

        drawTroops(g2d, font, troops, ARMY_BASE_LINE);
        drawDarkTroops(g2d, font, troops, ARMY_BASE_LINE);
        drawHeroes(g2d, font, heroes, ARMY_BASE_LINE);
        drawSpells(g2d, font, spells, ARMY_BASE_LINE);
        drawMachines(g2d, font, troops, ARMY_BASE_LINE);

        FileUtils.sendImage(event, image, player.getTag(), "png");

        g2d.dispose();
    }
}
