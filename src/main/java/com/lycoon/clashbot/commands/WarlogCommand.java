package com.lycoon.clashbot.commands;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashapi.cocmodels.clanwar.ClanWarModel;
import com.lycoon.clashapi.cocmodels.clanwar.WarlogItem;
import com.lycoon.clashapi.cocmodels.clanwar.WarlogModel;
import com.lycoon.clashapi.cocmodels.clanwar.league.WarLeagueGroup;
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
	
	private final static int PADDING = 6;
	private final static int WAR_ITEM_HEIGHT = 74;
	private final static int WIDTH = 932;
	private final static float FONT_SIZE = 16f;

	private static Color backgroundColor = new Color(0xe7e7e1);
	private static Color winsColor = new Color(0xd5edba);
	private static Color lossesColor = new Color(0xf2c8c7);
	private static Color tiesColor = new Color(0xcccccc);
	private static Color totalColor = new Color(0xfefed1);
	private static Color versusColor = new Color(0xffffc0);
	private static Color percentageColor = new Color(0x5e5d60);
	
	public static void drawWar(Graphics2D g2d, WarlogItem war, int y)
	{
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
		DrawUtils.drawShadowedString(g2d, enemy.getName(), 600, y+40, 20f);
	}

	public static WarlogModel getWarlog(MessageReceivedEvent event, Locale lang, String[] args)
	{
		// If rate limitation has exceeded
		if (!CoreUtils.checkThrottle(event, lang))
			return null;

		WarlogModel warlog = null;
		ResourceBundle i18n = LangUtils.getTranslations(lang);
		String tag = args.length > 1 ? args[0] : DBUtils.getClanTag(event.getAuthor().getIdLong());

		if (tag == null)
		{
			ErrorEmbed.sendError(event.getChannel(), i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
			return null;
		}

		try
		{
			warlog = ClashBotMain.clashAPI.getWarlog(tag);
		}
		catch (IOException ignored) {}
		catch (ClashAPIException e)
		{
			ErrorEmbed.sendExceptionError(event, i18n, e, tag, "warlog");
			return null;
		}
		return warlog;
	}
	
	public static void execute(MessageReceivedEvent event, String... args)
	{
		MessageChannel channel = event.getChannel();
		
		Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
		i18n = LangUtils.getTranslations(lang);

		WarlogModel warlog = getWarlog(event, lang, args);
		if (warlog == null)
			return;

		// Removing clan wars with null clans
		List<WarlogItem> wars = warlog.getWars();
		for (int i=0; i < wars.size(); i++)
		{
			if (wars.get(i).getOpponent().getName() == null)
				wars.remove(i);
		}

		// Computing height
		int height = 0;
		
		// Initializing image
		BufferedImage image = new BufferedImage(WIDTH, 700, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = DrawUtils.initGraphics(WIDTH, 700, image);
		Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
		g2d.setFont(font);

		// Color background
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, WIDTH, height);
		
		// Top background
		g2d.drawImage(FileUtils.getImageFromFile("backgrounds/warlog/stats-panel-full.png"), 0, 15, null);
		
		for (int i=0; i < wars.size(); i++)
		{
			drawWar(g2d, wars.get(i), 118 + i * WAR_ITEM_HEIGHT + i * PADDING);
		}
		
		FileUtils.sendImage(event, image, "warlog", "jpg");
		g2d.dispose();
	}
}
