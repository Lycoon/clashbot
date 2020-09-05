package com.lycoon.clashbot.commands;

//import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashapi.cocmodels.clanwar.ClanWarModel;
import com.lycoon.clashapi.cocmodels.clanwar.WarlogItem;
import com.lycoon.clashapi.cocmodels.clanwar.WarlogModel;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.CacheComponents;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.ErrorEmbed;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DBUtils;
import com.lycoon.clashbot.utils.DrawUtils;
import com.lycoon.clashbot.utils.FileUtils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WarlogCommand
{
	public static ResourceBundle i18n;
	
	private final static int MEMBER_HEIGHT = 111;
	private final static int WIDTH = 1400;
	private final static float FONT_SIZE = 16f;
	
	/*
	private static Color winsColor = new Color(0xd5edba);
	private static Color lossesColor = new Color(0xf2c8c7);
	private static Color tiesColor = new Color(0xcccccc);
	private static Color totalColor = new Color(0xfefed1);
	private static Color versusColor = new Color(0xffffc0);
	private static Color percentageColor = new Color(0x5e5d60);
	*/
	
	public static void drawWar(Graphics2D g2d, WarlogItem war, int y)
	{
		System.out.println(war.getOpponent().getName());
		
		// Member background
		Image result = war.getResult().equals("win") ? CacheComponents.warWon : CacheComponents.warLost;
		g2d.drawImage(result, 0, y, null);
		
		// Clan badges
		ClanWarModel clan = war.getClan();
		ClanWarModel enemy = war.getOpponent();
		g2d.drawImage(FileUtils.getImageFromUrl(clan.getBadgeUrls().getSmall()), 400, y+40, null);
		g2d.drawImage(FileUtils.getImageFromUrl(enemy.getBadgeUrls().getSmall()), 550, y+40, null);
		
		// Clan names
		DrawUtils.drawShadowedStringLeft(g2d, clan.getName(), 380, y+40, 20f);
		DrawUtils.drawShadowedStringLeft(g2d, enemy.getName(), 600, y+40, 20f);
	}
	
	public static void execute(MessageReceivedEvent event, String... args)
	{
		MessageChannel channel = event.getChannel();
		
		Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
		i18n = LangUtils.getTranslations(lang);
		
		// If rate limitation has exceeded
		if (!CoreUtils.checkThrottle(event, lang))
			return;
		
		WarlogModel warlog = null;
		String tag = args.length > 0 ? args[0] : DBUtils.getClanTag(event.getAuthor().getIdLong());
		
		if (tag == null)
		{
			ErrorEmbed.sendError(channel, i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
			return;
		}
		
		try
		{
			warlog = ClashBotMain.clashAPI.getWarlog(tag);
		}
		catch (IOException e) {}
		catch (ClashAPIException e) 
		{
			ErrorEmbed.sendExceptionError(event, e, tag, "warlog");
			return;
		}
		
		List<WarlogItem> wars = warlog.getWars();
		for (int i=0; i < wars.size(); i++)
		{
			if (wars.get(i).getOpponent().getName() == null)
				wars.remove(i);
		}
		
		// Initializing image
		int image_height = MEMBER_HEIGHT * wars.size() + 172;
		BufferedImage image = new BufferedImage(WIDTH, image_height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = DrawUtils.initGraphics(WIDTH, image_height, image);
		Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
		g2d.setFont(font);
		
		// Top background
		g2d.drawImage(FileUtils.getImageFromFile("backgrounds/warlog/stats-panel-top.png"), 0, 0, null);
		
		for (int i=0; i < wars.size(); i++)
		{
			drawWar(g2d, wars.get(i), i*MEMBER_HEIGHT + 172);
		}
		
		FileUtils.sendImage(event, image, tag + "warlog", "jpg");
		g2d.dispose();
	}
}
