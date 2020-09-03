package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashapi.cocmodels.clanwar.Attack;
import com.lycoon.clashapi.cocmodels.clanwar.ClanWarMember;
import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.CacheComponents;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.ErrorEmbed;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.DBUtils;
import com.lycoon.clashbot.utils.DrawUtils;
import com.lycoon.clashbot.utils.FileUtils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

class SortMemberByOrder implements Comparator<ClanWarMember>
{
	@Override
	public int compare(ClanWarMember a, ClanWarMember b)
	{
		return a.getMapPosition() - b.getMapPosition();
	}
}

class SortAttackByOrder implements Comparator<Attack>
{
	@Override
	public int compare(Attack a, Attack b)
	{
		return a.getOrder() - b.getOrder();
	}
}

public class WarCommand
{
	private final static int PADDING = 4;
	private final static int MEMBER_HEIGHT = 74;
	private final static int WIDTH = 1920;
	private final static float FONT_SIZE = 16f;
	private static long starsExec = 0;
	private static long highestStarsExec = 0;
	
	private static ResourceBundle i18n;
	private static Color notUsedAttackColor = new Color(0xfbc546);
	private static Color attackColor = new Color(0x4c493a);
	
	public static int getPositive(float value)
	{
		return (int)(value * (value < 0 ? -1 : 1));
	}
	
	public static int[] getTimeLeft(String toParse)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS'Z'");
		LocalDateTime endDate = LocalDateTime.parse(toParse, formatter);
		ZonedDateTime zonedTime = endDate.atZone(ZoneId.of("UTC"));
		Duration diff = Duration.between(ZonedDateTime.now(), zonedTime);
		long s = diff.getSeconds();
		
		long hours = (long)s/3600;
		s -= hours*3600;
		long minutes = (long)s/60;
		s -= minutes*60;
		
		int[] res = {(int)hours, (int)minutes, (int)s};
		return res;
	}
	
	public static ClanWarMember getClanWarMemberByTag(List<ClanWarMember> members, String tag)
	{
		for (int i=0; i < members.size(); i++)
		{
			ClanWarMember member = members.get(i);
			if (member.getTag().equals(tag))
				return member;
		}
		return null;
	}
	
	public static List<Attack> getAttacksByOrder(List<ClanWarMember> members)
	{
		List<Attack> sortedAttacks = new ArrayList<Attack>();
		for (int i=0; i < members.size(); i++)
		{
			ClanWarMember member = members.get(i);
			List<Attack> attacks = member.getAttacks();
			if (attacks != null)
			{
				for (int j=0; j < attacks.size(); j++)
				{
					Attack atk = attacks.get(j);
					sortedAttacks.add(atk);
				}
			}
		}
		sortedAttacks.sort(new SortAttackByOrder());
		return sortedAttacks;
	}
	
	public static int getHighestStars(List<Attack> attacks, Attack atk)
	{
		long startHighest = System.currentTimeMillis();
		int max = 0;
		for (int i=0; i < attacks.size() && attacks.get(i).getOrder() < atk.getOrder(); i++)
		{
			Attack curr = attacks.get(i);
			if (curr.getDefenderTag().equals(atk.getDefenderTag()))
			{
				if (curr.getStars() > max)
					max = curr.getStars();
			}
		}
		highestStarsExec += System.currentTimeMillis()-startHighest;
		return max;
	}
	
	public static int getNewStars(List<Attack> attacks, Attack attack)
	{
		int highestStars = getHighestStars(attacks, attack);
		if (attack.getStars() > highestStars)
			return attack.getStars()-highestStars;
		return 0;
	}
	
	public static void drawMember(Graphics2D g2d, ClanWarMember member, 
			List<ClanWarMember> members, List<ClanWarMember> enemyMembers, List<Attack> sortedAttacks, int i, int x, int y)
	{
		g2d.setColor(Color.WHITE);
		
		// Member background
		g2d.drawImage(i%2 == 0 ? CacheComponents.memberLight : CacheComponents.memberDark, 
				x + PADDING, y, 
				null);
		
		// Username
		DrawUtils.drawShadowedString(g2d, member.getName(), x+130, y + 45, 20f);
		
		// Townhall
		g2d.drawImage(CacheComponents.getTownHallImage(member.getTownhallLevel()), x + 65, y + 10, 52, 52, null);
		
		// Map position
		Rectangle posRect = new Rectangle(x+4, y+5, 60, 60);
		DrawUtils.drawCenteredString(g2d, posRect, g2d.getFont().deriveFont(25f), member.getMapPosition()+ ".");
		
		int stars = 0;
		List<Attack> attacks = member.getAttacks();
		for (int j=0; j < 2; j++)
		{
			DrawUtils.drawSimpleString(g2d, MessageFormat.format(i18n.getString("attack"), j+1), x+390, y+24 + j*35, 10f, attackColor);
			if (attacks != null)
			{
				Attack attack = null;
				if (j < attacks.size())
				{
					// If the player made at least one attack
					attack = attacks.get(j);
					ClanWarMember defender = getClanWarMemberByTag(enemyMembers, attack.getDefenderTag());
					
					DrawUtils.drawShadowedStringLeft(g2d, defender.getMapPosition()+ ". " +defender.getName(), x+680, y+24 + j*35, 12f);
					DrawUtils.drawSimpleStringLeft(g2d, attack.getDestructionPercentage().longValue()+ "%", x+765, y+24 + j*35, 12f, Color.BLACK);
					
					int newStars = getNewStars(sortedAttacks, attack);
					
					// Stars
					long startStarsExec = System.currentTimeMillis();
					for (int k=0; k < 3; k++)
					{
						if (k+1 <= attack.getStars())
						{
							if (k+1 <= attack.getStars()-newStars)
								g2d.drawImage(CacheComponents.alreadyStar, x+775 + k*24, y+6 + j*35, 22, 22, null);
							else
							{
								stars++;
								g2d.drawImage(CacheComponents.newStar, x+775 + k*24, y+6 + j*35, 22, 22, null);
							}
						}
						else
							g2d.drawImage(CacheComponents.noStar, x+775 + k*24, y+6 + j*35, 22, 22, null);
					}
					starsExec += System.currentTimeMillis()-startStarsExec;
				}
				else
					DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("not.used"), x+680, y+24 + j*35, 10f, 2, notUsedAttackColor);
			}
			else
				DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("not.used"), x+680, y+24 + j*35, 10f, 2, notUsedAttackColor);
		}
		
		// Stars
		Rectangle starRect = new Rectangle(x+864, y+19, 30, 30);
		DrawUtils.drawCenteredString(g2d, starRect, g2d.getFont().deriveFont(22f), String.valueOf(stars));
	}
	
	public static void execute(MessageReceivedEvent event, String... args)
	{
		MessageChannel channel = event.getChannel();
		
		Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
		i18n = LangUtils.getTranslations(lang);
		
		WarInfo war = null;
		String tag = args.length > 0 ? args[0] : DBUtils.getClanTag(event.getAuthor().getIdLong());
		
		if (tag == null)
		{
			ErrorEmbed.sendError(channel, i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
			return;
		}
		
		try 
		{
			war = ClashBotMain.clashAPI.getCurrentWar(tag);
		}
		catch (IOException e) {}
		catch (ClashAPIException e) 
		{
			ErrorEmbed.sendExceptionError(event, e, tag, "war");
			return;
		}
		
		if (!war.getState().equals("notInWar"))
		{
			// Initializing image
			int image_height = (MEMBER_HEIGHT + PADDING) * war.getTeamSize() + 277;
			BufferedImage image = new BufferedImage(WIDTH, image_height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = DrawUtils.initGraphics(WIDTH, image_height, image);
			Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
			g2d.setFont(font);
			
			List<ClanWarMember> members = war.getClan().getWarMembers();
			List<ClanWarMember> enemyMembers = war.getEnemy().getWarMembers();
			
			long startSort = System.currentTimeMillis();
			members.sort(new SortMemberByOrder());
			enemyMembers.sort(new SortMemberByOrder());
			System.out.println("SORT MEMBERS AND ATTACKS: " +(System.currentTimeMillis()-startSort)/1000.0+ "s");
			
			List<Attack> attacks = getAttacksByOrder(members);
			List<Attack> enemyAttacks = getAttacksByOrder(enemyMembers);
			
			// Main background
			long start = System.currentTimeMillis();
			g2d.drawImage(FileUtils.getImageFromFile("backgrounds/clanwar/background-panel.png"), 
					0, 277, 
					WIDTH, image_height-355, 
					null);
			System.out.println("DRAW MAIN BACKGROUND: " +(System.currentTimeMillis()-start)/1000.0+ "s");
			
			// Bottom background
			long startbg = System.currentTimeMillis();
			g2d.drawImage(FileUtils.getImageFromFile("backgrounds/clanwar/background-panel-end.png"), 
					0, image_height - 78, 
					WIDTH, 78, 
					null);
			System.out.println("DRAW BOTTOM BACKGROUND: " +(System.currentTimeMillis()-startbg)/1000.0+ "s");
			
			// Top background
			long startbg2 = System.currentTimeMillis();
			g2d.drawImage(FileUtils.getImageFromFile("backgrounds/clanwar/background-panel-top.png"), 
					0, 0,
					WIDTH, 277,
					null);
			System.out.println("DRAW TOP BACKGROUND: " +(System.currentTimeMillis()-startbg2)/1000.0+ "s");
			
			// Clan badges
			long startNamesAndBadges = System.currentTimeMillis();
			g2d.drawImage(FileUtils.getImageFromUrl(war.getClan().getBadgeUrls().getSmall()), 30, 20, 160, 160, null);
			g2d.drawImage(FileUtils.getImageFromUrl(war.getEnemy().getBadgeUrls().getSmall()), 1740, 20, 160, 160, null);
			
			// Clan names
			DrawUtils.drawSimpleString(g2d, war.getClan().getName(), 30, 230, 44f, Color.BLACK);
			DrawUtils.drawSimpleStringLeft(g2d, war.getEnemy().getName(), 1890, 230, 44f, Color.BLACK);
			System.out.println("DRAW CLAN NAMES AND BADGES: " +(System.currentTimeMillis()-startNamesAndBadges)/1000.0+ "s");
			
			// Status
			Rectangle timeRect = new Rectangle(0, 120, WIDTH, 80);
			Rectangle statusRect = new Rectangle(0, 50, WIDTH, 80);
			int[] timeLeft;
			switch (war.getState())
			{
				case "inWar":
					timeLeft = getTimeLeft(war.getEndTime());
					DrawUtils.drawSimpleCenteredString(g2d, i18n.getString("war.inwar"), statusRect, 40f, Color.BLACK);
					DrawUtils.drawSimpleCenteredString(g2d, 
							MessageFormat.format(i18n.getString("war.timeleft"), 
							MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])), 
							timeRect, 50f, Color.BLACK);
					break;
				case "preparation":
					timeLeft = getTimeLeft(war.getStartTime());
					DrawUtils.drawSimpleCenteredString(g2d, i18n.getString("war.preparation"), statusRect, 40f, Color.BLACK);
					DrawUtils.drawSimpleCenteredString(g2d, 
							MessageFormat.format(i18n.getString("war.timeleft"), 
							MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])), 
							timeRect, 50f, Color.BLACK);
					break;
				default: 
					//warEnded
					timeLeft = getTimeLeft(war.getEndTime());
					
					DrawUtils.drawSimpleCenteredString(g2d, i18n.getString("war.ended"), statusRect, 40f, Color.BLACK);
					DrawUtils.drawSimpleCenteredString(g2d, 
							MessageFormat.format(i18n.getString("war.since"), 
							MessageFormat.format(i18n.getString("war.date"), timeLeft[0], timeLeft[1], timeLeft[2])), 
							timeRect, 50f, Color.BLACK);
			}
			
			long startMember = System.currentTimeMillis();
			for (int i=0; i < war.getTeamSize(); i++)
			{
				ClanWarMember member = members.get(i);
				ClanWarMember enemy = enemyMembers.get(i);
				drawMember(g2d, member, members, enemyMembers, attacks, i, 18, i*(MEMBER_HEIGHT+PADDING) + 250);
				drawMember(g2d, enemy, enemyMembers, members, enemyAttacks, i, 957, i*(MEMBER_HEIGHT+PADDING) + 250);
			}
			System.out.println("DRAW MEMBERS: " +(System.currentTimeMillis()-startMember)/1000.0+ "s");
			System.out.println("DRAW ALL STARS: " +starsExec/1000.0+ "s");
			System.out.println("GET ALL HIGHEST STARS: " +highestStarsExec/1000.0+ "s");
			
			// Total clan stars
			Rectangle clanStarRect1 = new Rectangle(210, 25, 200, 80);
			Rectangle clanStarRect2 = new Rectangle(1483, 25, 200, 80);
			g2d.drawImage(FileUtils.getImageFromFile("icons/cwl/star-label.png"), 240, 28, 187, 72, null);
			g2d.drawImage(FileUtils.getImageFromFile("icons/cwl/star-label.png"), 1510, 28, 187, 72, null);
			
			DrawUtils.drawCenteredString(g2d, clanStarRect1, font.deriveFont(45f), String.valueOf(war.getClan().getStars()));
			DrawUtils.drawCenteredString(g2d, clanStarRect2, font.deriveFont(45f), String.valueOf(war.getEnemy().getStars()));
			
			// Total destruction percentage
			DecimalFormatSymbols dfs = new DecimalFormatSymbols(lang);
			DecimalFormat df = new DecimalFormat("#.#", dfs);
			DrawUtils.drawSimpleString(g2d, df.format(war.getClan().getDestructionPercentage())+ "%", 240, 150, 32f, Color.BLACK);
			DrawUtils.drawSimpleStringLeft(g2d, df.format(war.getEnemy().getDestructionPercentage())+ "%", 1700, 150, 32f, Color.BLACK);
			
			FileUtils.sendImage(channel, image, tag + "war", "jpg");
			g2d.dispose();
		}
		else
			ErrorEmbed.sendError(channel, MessageFormat.format(i18n.getString("exception.404.war"), tag));
	}
}
