package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

import com.lycoon.clashapi.cocmodels.player.Player;
import com.lycoon.clashapi.cocmodels.player.Troop;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.DrawUtils;
import com.lycoon.clashbot.utils.FileUtils;
import com.lycoon.clashbot.utils.GameUtils;

import net.dv8tion.jda.api.entities.MessageChannel;

public class PlayerCommand
{
	private final static int WIDTH = 932;
	private final static int HEIGHT = 322;
	private final static float FONT_SIZE = 12f;
	private final static int ARMY_BASE_LINE = 232;
	
	private final static String[] TROOPS = {"Barbarian", "Archer", "Giant", "Goblin", "Wall Breaker", "Balloon", "Wizard", "Healer", "Dragon", "P.E.K.K.A", "Baby Dragon", "Miner", "Electro Dragon", "Yeti"};
	private final static String[] DARK_TROOPS = {"Minion", "Hog Rider", "Valkyrie", "Golem", "Witch", "Lava Hound", "Bowler", "Ice Golem", "Headhunter"};
	private final static String[] HEROES = {"Barbarian King", "Archer Queen", "Grand Warden", "Royal Champion"};
	private final static String[] SPELLS = {"Lightning Spell", "Healing Spell", "Rage Spell", "Jump Spell", "Freeze Spell", "Clone Spell", "Poison Spell", "Earthquake Spell", "Haste Spell", "Skeleton Spell", "Bat Spell"};
	private final static String[] MACHINES = {"Wall Wrecker", "Battle Blimp", "Stone Slammer", "Siege Barracks"};
	
	public static void drawTroop(Graphics2D g2d, Font font, List<Troop> troops, String troopName, int x, int y)
	{
		Troop troop = GameUtils.getTroopByName(troops, troopName);
		
		// If the player has not unlocked the troop yet
		if (troop == null)
			g2d.drawImage(FileUtils.getImageFromFile("troops/locked/" + troopName + ".png"), x, y, 35, 35, null);
		else
		{
			g2d.drawImage(FileUtils.getImageFromFile("troops/" + troop.getName() + ".png"), x, y, 35, 35, null);
			if (troop.getLevel() == troop.getMaxLevel())
			{
				g2d.drawImage(FileUtils.getImageFromFile("icons/level-label-max.png"), x+2, y+18, 15, 15, null);
			}
			else
			{
				if (troop.getLevel() != 1)
					g2d.drawImage(FileUtils.getImageFromFile("icons/level-label.png"), x+2, y+18, 15, 15, null);
			}
			if (troop.getLevel() != 1)
			{
				Rectangle levelRect = new Rectangle(x+2, y+18, 15, 15);
				DrawUtils.drawCenteredString(g2d, levelRect, font.deriveFont(font.getSize()-4f), troop.getLevel().toString());
			}
		}
	}
	
	public static void drawTroops(Graphics2D g2d, Font font, List<Troop> troops, int y)
	{
		for (int i=0; i < TROOPS.length; i++)
			drawTroop(g2d, font, troops, TROOPS[i], i*38 + 20, y);
	}
	
	public static void drawDarkTroops(Graphics2D g2d, Font font, List<Troop> troops, int y)
	{
		for (int i=0; i < DARK_TROOPS.length; i++)
			drawTroop(g2d, font, troops, DARK_TROOPS[i], i*38 + 20, y + 37);
	}
	
	public static void drawHeroes(Graphics2D g2d, Font font, List<Troop> heroes, int y)
	{
		for (int i=0; i < HEROES.length; i++)
			drawTroop(g2d, font, heroes, HEROES[i], i*38 + 400, y + 37);
	}
	
	public static void drawSpells(Graphics2D g2d, Font font, List<Troop> spells, int y)
	{
		for (int i=0; i < 5; i++)
			drawTroop(g2d, font, spells, SPELLS[i], i*38 + 580, y);
		for (int i=5; i < SPELLS.length; i++)
			drawTroop(g2d, font, spells, SPELLS[i], (i-5)*38 + 580, y + 37);
	}
	
	public static void drawMachines(Graphics2D g2d, Font font, List<Troop> machines, int y)
	{
		drawTroop(g2d, font, machines, MACHINES[0], 837, y);
		drawTroop(g2d, font, machines, MACHINES[1], 875, y);
		drawTroop(g2d, font, machines, MACHINES[2], 837, y + 37);
		drawTroop(g2d, font, machines, MACHINES[3], 875, y + 37);
	}
	
	public static void execute(MessageChannel channel, String tag)
	{
		ResourceBundle lang = LangUtils.bundle;
		NumberFormat nf = NumberFormat.getInstance(LangUtils.currentLang);
		
		Player player = null;
		try 
		{
			player = ClashBotMain.clashAPI.getPlayer(tag);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initializing image
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = DrawUtils.initGraphics(WIDTH, HEIGHT, image);
		
		Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
		g2d.setFont(font);
		
		g2d.drawImage(FileUtils.getImageFromFile("backgrounds/profile.png"), 0, 0, null);
		
		// Experience level
		g2d.drawImage(FileUtils.getImageFromFile("icons/exp-star.png"), 20, 18, 45, 45, null);
		Rectangle level = new Rectangle(23, 30, 40, 20);
		DrawUtils.drawCenteredString(g2d, level, font.deriveFont(FONT_SIZE+5f), player.getExpLevel().toString());
		
		// Nickname
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE+6f), player.getName(), 75, 39);
		
		// Player tag
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-1f), player.getTag(), 75, 55);
		
		// Townhall
		g2d.drawImage(FileUtils.getImageFromFile("buildings/townhalls/home/th" +player.getTownHallLevel()+ ".png"), 80, 80, 100, 100, null);
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-2f), lang.getString("townhall"), 25, 125);
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE+8f), lang.getString("level") + " " +player.getTownHallLevel(), 25, 150);
		
		// Builder hall
		if (player.getBuilderHallLevel() != null)
		{
			g2d.drawImage(FileUtils.getImageFromFile("buildings/townhalls/builder/bh" +player.getBuilderHallLevel()+ ".png"), 265, 85, 95, 95, null);
			DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE+8f), lang.getString("level") + " " +player.getBuilderHallLevel(), 200, 150);
		}
		else
		{
			// In case the player has not built the builder hall yet
			DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE+8f), lang.getString("no.builderhall"), 200, 150);
			
		}
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-2f), lang.getString("builderhall"), 200, 125);
		
		// League
		if (player.getLeague() != null)
			g2d.drawImage(FileUtils.getImageFromUrl(player.getLeague().getIconUrls().getMedium()), 383, 30, 90, 90, null);
		else
		{
			g2d.drawImage(FileUtils.getImageFromFile("icons/noleague.png"), 383, 30, 90, 90, null);
			Rectangle noLeagueRect = new Rectangle(375, 60, 105, 20);
			DrawUtils.drawCenteredString(g2d, noLeagueRect, font.deriveFont(FONT_SIZE-4f), lang.getString("no.league"));
		}
		
		// Clan
		if (player.getClan() != null)
		{
			g2d.drawImage(FileUtils.getImageFromUrl(player.getClan().getBadgeUrls().getLarge()), 800, 30, 105, 105, null);
			
			Rectangle clanNameRect = new Rectangle(775, 130, 148, 30);
			Rectangle clanRoleRect = new Rectangle(775, 151, 148, 30);
			DrawUtils.drawCenteredString(g2d, clanNameRect, font.deriveFont(FONT_SIZE+2f), player.getClan().getName());
			DrawUtils.drawCenteredString(g2d, clanRoleRect, font.deriveFont(FONT_SIZE-2f), lang.getString(player.getRole()));
		}
		else
		{
			Rectangle noClanRect = new Rectangle(775, 130, 148, 30);
			DrawUtils.drawCenteredString(g2d, noClanRect, font.deriveFont(FONT_SIZE+2f), lang.getString("no.clan"));
			g2d.drawImage(FileUtils.getImageFromFile("icons/noclan.png"), 812, 40, 75, 75, null);
		}
		
		// Trophies
		Rectangle trophiesRect = new Rectangle(375, 148, 75, 24);
		DrawUtils.drawCenteredString(g2d, trophiesRect, font.deriveFont(FONT_SIZE+4f), nf.format(player.getTrophies()));
		
		// Statistics
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE+3f), lang.getString("season")+ " " +GameUtils.getCurrentSeason(), 486, 45);
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-1.5f), lang.getString("attacks.won"), 486, 77);
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-1.5f), lang.getString("defenses.won"), 486, 107);
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-1.5f), lang.getString("donations"), 486, 143);
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE-1.5f), lang.getString("donations.received"), 486, 173);
		
		DrawUtils.drawSimpleString(g2d, font.deriveFont(FONT_SIZE), new Color(0x444545), nf.format(player.getAttackWins()), 693, 79);
		DrawUtils.drawSimpleString(g2d, font.deriveFont(FONT_SIZE), new Color(0x444545), nf.format(player.getDefenseWins()), 693, 109);
		DrawUtils.drawSimpleString(g2d, font.deriveFont(FONT_SIZE), new Color(0x444545), nf.format(player.getDonations()), 693, 144);
		DrawUtils.drawSimpleString(g2d, font.deriveFont(FONT_SIZE), new Color(0x444545), nf.format(player.getDonationsReceived()), 693, 174);
		
		// Army
		DrawUtils.drawShadowedString(g2d, font.deriveFont(FONT_SIZE+2f), lang.getString("army"), 21, 222);
		
		// Troops
		List<Troop> troops = player.getTroops();
		List<Troop> heroes = player.getHeroes();
		List<Troop> spells = player.getSpells();
		
		drawTroops(g2d, font, troops, ARMY_BASE_LINE);
		drawDarkTroops(g2d, font, troops, ARMY_BASE_LINE);
		drawHeroes(g2d, font, heroes, ARMY_BASE_LINE);
		drawSpells(g2d, font, spells, ARMY_BASE_LINE);
		drawMachines(g2d, font, troops, ARMY_BASE_LINE);
		
		FileUtils.sendImage(channel, image, player.getTag());
		
		g2d.dispose();
	}
}
