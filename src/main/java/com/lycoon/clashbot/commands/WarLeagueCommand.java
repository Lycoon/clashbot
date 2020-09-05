package com.lycoon.clashbot.commands;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashapi.cocmodels.clanwar.ClanWarModel;
import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashapi.cocmodels.clanwar.league.Round;
import com.lycoon.clashapi.cocmodels.clanwar.league.WarLeagueGroup;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.ClanWarStats;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.ErrorEmbed;
import com.lycoon.clashbot.core.RoundWarInfo;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DBUtils;
import com.lycoon.clashbot.utils.DrawUtils;
import com.lycoon.clashbot.utils.FileUtils;
import com.lycoon.clashbot.utils.GameUtils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WarLeagueCommand
{
	private static ResourceBundle i18n;
	private static Locale lang;
	
	private final static int WIDTH = 1900;
	private final static int HEIGHT = 1213;
	private final static float FONT_SIZE = 16f;
	
	public static List<RoundWarInfo> getWars(WarLeagueGroup leagueGroup)
	{
		List<RoundWarInfo> wars = new ArrayList<RoundWarInfo>();
		for (int i=0; i < leagueGroup.getRounds().size(); i++)
		{
			Round round = leagueGroup.getRounds().get(i);
			RoundWarInfo roundWarInfo = new RoundWarInfo();
			List<String> warTags = round.getWarTags();
			
			for (int j=0; j < warTags.size(); j++)
			{
				try {roundWarInfo.addWarInfo(ClashBotMain.clashAPI.getCWLWar(warTags.get(j)));}
				catch (IOException e) {}
				catch (ClashAPIException e) {}
			}
			wars.add(roundWarInfo);
		}
		return wars;
	}
	
	public static void drawRounds(Graphics2D g2d, List<RoundWarInfo> rounds)
	{
		drawRound(g2d, rounds, 0, 970, 55);
		drawRound(g2d, rounds, 1, 34, 350);
		drawRound(g2d, rounds, 2, 970, 350);
		drawRound(g2d, rounds, 3, 34, 647);
		drawRound(g2d, rounds, 4, 970, 647);
		drawRound(g2d, rounds, 5, 34, 945);
		drawRound(g2d, rounds, 6, 970, 945);
	}
	
	public static void drawRound(Graphics2D g2d, List<RoundWarInfo> rounds, int roundIndex, int x, int y)
	{
		List<WarInfo> wars = rounds.get(roundIndex).getWars();
		Font font = g2d.getFont().deriveFont(FONT_SIZE);
		
		// Wars
		for (int i=0; i < wars.size(); i++)
		{
			WarInfo war = wars.get(i);
			ClanWarModel clan1 = war.getClan();
			ClanWarModel clan2 = war.getEnemy();
			
			System.out.println(war.getState());
			
			switch (war.getState())
			{
				case "inWar":
					g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/current-panel.png"), x, y + i*60, 895, 55, null);
					break;
				case "preparation":
					g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/preparation-panel.png"), x, y + i*60, 895, 55, null);
					break;
				case "warEnded":
					if (clan2.getStars() > clan1.getStars() || 
							(clan2.getStars() == clan1.getStars() && clan2.getDestructionPercentage() > clan1.getDestructionPercentage()))
					{
						ClanWarModel tmp = clan1;
						clan1 = clan2;
						clan2 = tmp;
					}
					g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/end-panel.png"), x, y + i*60, 895, 55, null);
					break;
				default:
					// notInWar
					g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/preparation-panel.png"), x, y + i*60, 895, 55, null);
			}
			
			if (clan1 != null && clan2 != null)
			{
				Rectangle rectClan1 = new Rectangle(x+10, y + 17 + i*60, 300, 20);
				Rectangle rectStarClan1 = new Rectangle(x+365, y + 15 + i*60, 50, 20);
				Rectangle rectClan2 = new Rectangle(x+585, y + 17 + i*60, 300, 20);
				Rectangle rectStarClan2 = new Rectangle(x+480, y + 15 + i*60, 50, 20);
				
				DrawUtils.drawCenteredString(g2d, rectClan1, font, clan1.getName());
				DrawUtils.drawCenteredString(g2d, rectStarClan1, font, clan1.getStars().toString());
				DrawUtils.drawCenteredString(g2d, rectClan2, font, clan2.getName());
				DrawUtils.drawCenteredString(g2d, rectStarClan2, font, clan2.getStars().toString());
				
				g2d.drawImage(FileUtils.getImageFromUrl(clan1.getBadgeUrls().getLarge()), x+310, y+8 + i*60, 40, 40, null);
				g2d.drawImage(FileUtils.getImageFromUrl(clan2.getBadgeUrls().getLarge()), x+547, y+8 + i*60, 40, 40, null);
			}
		}
		
		// Round label
		Rectangle roundLabel = new Rectangle(x, y-26, 893, 20);
		DrawUtils.drawCenteredImage(g2d, FileUtils.getImageFromFile("icons/cwl/round-label.png"), roundLabel, 124, 26);
		DrawUtils.drawCenteredString(g2d, roundLabel, font.deriveFont(18f), "Round " + (roundIndex+1));
	}
	
	public static void updateStats(ClanWarModel clan, HashMap<String, ClanWarStats> stats)
	{
		if (clan != null)
		{
			if (stats.containsKey(clan.getTag()))
			{
				ClanWarStats stats1 = stats.get(clan.getTag());
				stats1.addStars(clan.getStars());
				stats1.addDestruction(clan.getDestructionPercentage());
				stats.put(clan.getTag(), stats1);
			}
			else
				stats.put(clan.getTag(), new ClanWarStats(clan));
		}
	}
	
	public static void drawStats(Graphics2D g2d, List<RoundWarInfo> rounds)
	{
		HashMap<String, ClanWarStats> stats = new HashMap<String, ClanWarStats>();
		for (int i=0; i < rounds.size(); i++)
		{
			RoundWarInfo roundWars = rounds.get(i);
			for (int j=0; j < roundWars.getWars().size(); j++)
			{
				WarInfo warInfo = roundWars.getWars().get(j);
				updateStats(warInfo.getClan(), stats);
				updateStats(warInfo.getEnemy(), stats);
			}
		}
	}
	
	public static void execute(MessageReceivedEvent event, String... args)
	{
		MessageChannel channel = event.getChannel();
		
		lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
		i18n = LangUtils.getTranslations(lang);
		
		// If rate limitation has exceeded
		if (!CoreUtils.checkThrottle(event, lang))
			return;
		
		WarLeagueGroup leagueGroup = null;
		String tag = args.length > 0 ? args[0] : DBUtils.getClanTag(event.getAuthor().getIdLong());
		
		if (tag == null)
		{
			ErrorEmbed.sendError(channel, i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
			return;
		}
		
		try 
		{
			leagueGroup = ClashBotMain.clashAPI.getCWLGroup(tag);
		} 
		catch (IOException e) {}
		catch (ClashAPIException e)
		{
			ErrorEmbed.sendExceptionError(event, e, tag, "warleague");
			return;
		}
		
		// Initializing image
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = DrawUtils.initGraphics(WIDTH, HEIGHT, image);
		Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
		g2d.setFont(font);
		
		// Background
		g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/cwl-full.png"), 0, 0, null);
		
		// Season
		DrawUtils.drawShadowedString(g2d, i18n.getString("season")+ " " +GameUtils.getCurrentSeason(lang), 35, 60, 35f);
		
		// Rounds
		List<RoundWarInfo> roundWars = getWars(leagueGroup);
		drawRounds(g2d, roundWars);
		
		// Statistics
		drawStats(g2d, roundWars);
		
		FileUtils.sendImage(event, image, "test", "jpg");
		g2d.dispose();
	}
}
