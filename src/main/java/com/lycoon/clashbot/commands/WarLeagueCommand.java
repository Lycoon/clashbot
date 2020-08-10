package com.lycoon.clashbot.commands;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.lycoon.clashapi.cocmodels.clanwar.ClanWarModel;
import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashapi.cocmodels.clanwar.league.Round;
import com.lycoon.clashapi.cocmodels.clanwar.league.WarLeagueGroup;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.RoundWarInfo;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.DrawUtils;
import com.lycoon.clashbot.utils.FileUtils;
import com.lycoon.clashbot.utils.GameUtils;

import net.dv8tion.jda.api.entities.MessageChannel;

public class WarLeagueCommand
{
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
				catch (IOException e){e.printStackTrace();}
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
				default:
					// warEnded
					if (clan2.getStars() > clan1.getStars() || 
							(clan2.getStars() == clan1.getStars() && clan2.getDestructionPercentage() > clan1.getDestructionPercentage()))
					{
						ClanWarModel tmp = clan1;
						clan1 = clan2;
						clan2 = tmp;
					}
					g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/end-panel.png"), x, y + i*60, 895, 55, null);
			}
			
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
		
		// Round label
		Rectangle roundLabel = new Rectangle(x, y-26, 893, 20);
		DrawUtils.drawCenteredImage(g2d, FileUtils.getImageFromFile("icons/cwl/round-label.png"), roundLabel, 124, 26);
		DrawUtils.drawCenteredString(g2d, roundLabel, font.deriveFont(18f), "Round " + (roundIndex+1));
	}
	
	public static void execute(MessageChannel channel, String tag)
	{
		ResourceBundle lang = LangUtils.bundle;
		WarLeagueGroup leagueGroup = null;
		try 
		{
			leagueGroup = ClashBotMain.clashAPI.getCWLGroup(tag);
		} 
		catch (IOException e){e.printStackTrace();}
		
		// Initializing image
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = DrawUtils.initGraphics(WIDTH, HEIGHT, image);
		Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
		g2d.setFont(font);
		
		// Background
		g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/cwl-full.png"), 0, 0, null);
		
		// Season
		DrawUtils.drawShadowedString(g2d, font.deriveFont(35f), lang.getString("season")+ " " +GameUtils.getCurrentSeason(), 35, 60);
		
		List<RoundWarInfo> wars = getWars(leagueGroup);
		drawRounds(g2d, wars);
		
		FileUtils.sendImage(channel, image, "test");
		g2d.dispose();
	}
}
