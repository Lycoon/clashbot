package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.util.ResourceBundle;

import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommand
{
	private static final String TWITTER = "@LycoonMC";
	private static final String DISCORD = "Lycoon#7542";
	private static final String BOT_CODE = "https://github.com/Lycoon/clash-bot";
	private static final String API_CODE = "https://github.com/Lycoon/clash-api";
	private static final String INVITE = "https://discord.com/api/oauth2/authorize?client_id=734481969630543883&permissions=51200&scope=bot";
	private static final String PAYPAL = "https://paypal.me/lycoon";
	
	public static void execute(MessageReceivedEvent event)
	{
		ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setColor(Color.GRAY);
		builder.setTitle(i18n.getString("cmd.info.panel"));
		builder.setDescription("*Clashbot is a Java running Discord bot whose purpose is to provide Clash of Clans ingame info inside Discord. It was first published in July 2020.*" + "\n\n"
				+ "Bot author: **" +TWITTER+ "** on Twitter or **" +DISCORD+ "** on Discord\n"
				+ "Source code: " +BOT_CODE+ "\n"
				+ "Custom game API: " +API_CODE+ "\n"
				+ "Invite bot: " +INVITE+ "\n"
				+ "Support: " +PAYPAL+ "\n\n"
				+ "Need help with how it works? `" + Command.HELP.formatCommand() + "`");
		
		event.getChannel().sendMessage(builder.build()).queue();
	}
}
