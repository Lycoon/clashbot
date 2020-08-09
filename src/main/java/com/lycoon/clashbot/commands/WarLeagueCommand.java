package com.lycoon.clashbot.commands;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashapi.cocmodels.clanwar.league.Round;
import com.lycoon.clashapi.cocmodels.clanwar.league.WarLeagueGroup;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.RoundWarInfo;
import com.lycoon.clashbot.draw.DrawUtils;
import com.lycoon.clashbot.draw.FileUtils;

import net.dv8tion.jda.api.entities.MessageChannel;

public class WarLeagueCommand
{
	private final static int WIDTH = 1900;
	private final static int HEIGHT = 1213;
	private final static float FONT_SIZE = 12f;
	
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
	
	public static void drawRounds(Graphics2D g2d, List<RoundWarInfo> wars)
	{
		drawRound(g2d, wars.get(0), 950, 50);
		drawRound(g2d, wars.get(1), 25, 50);
		drawRound(g2d, wars.get(2), 25, 50);
		drawRound(g2d, wars.get(3), 25, 50);
		drawRound(g2d, wars.get(4), 25, 50);
		drawRound(g2d, wars.get(5), 25, 50);
		drawRound(g2d, wars.get(6), 25, 50);
	}
	
	public static void drawRound(Graphics2D g2d, RoundWarInfo round, int x, int y)
	{
		List<WarInfo> wars = round.getWars();
		
		// Round label
		Rectangle roundLabel = new Rectangle(x, y, 932, 60);
		g2d.draw(roundLabel);
		DrawUtils.drawCenteredImage(g2d, FileUtils.getImageFromFile("icons/cwl/round-label.png"), roundLabel, 124, 26);
		
		// Wars
		for (int i=0; i < wars.size(); i++)
		{
			g2d.drawImage(FileUtils.getImageFromFile("backgrounds/cwl/end-panel.png"), x, y + i*60, null);
		}
	}
	
	public static void execute(MessageChannel channel, String tag)
	{
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
		
		List<RoundWarInfo> wars = getWars(leagueGroup);
		drawRound(g2d, wars.get(0), 20, 30);
		drawRounds(g2d, wars);
		
		FileUtils.sendImage(channel, image, "test");
		g2d.dispose();
	}
}
