package com.lycoon.clashbot.core;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ErrorEmbed
{
	public static void sendExceptionError(MessageReceivedEvent event, ResourceBundle i18n, Exception e, String... args)
	{
		MessageChannel channel = event.getChannel();
		switch (e.getMessage())
		{
			case "400":
				sendError(channel, i18n.getString("exception.other"));
				break;
			case "403":
				sendError(channel, i18n.getString("exception.403"));
				break;
			case "404":
				sendError(channel, 
						MessageFormat.format(i18n.getString("exception.404." +args[1]), args[0]),
						i18n.getString("exception.format"));
				break;
			case "429":
				sendError(channel, i18n.getString("exception.429"));
				break;
			case "503":
				sendError(channel, 
						i18n.getString("exception.503"),
						i18n.getString("exception.status"));
				break;
			default:
				sendError(channel, 
						i18n.getString("exception.other"),
						i18n.getString("exception.contact"));
		}
	}

	public static void throwCommandError(MessageChannel channel, ResourceBundle i18n, Command cmd)
	{
		ErrorEmbed.sendError(channel, i18n.getString("wrong.usage"),
				MessageFormat.format(i18n.getString("tip.usage"), cmd.formatFullCommand()));
	}
	
	public static void sendError(MessageChannel channel, String title, String... args)
	{
		EmbedBuilder error = new EmbedBuilder();
		error.setColor(Color.RED);
		error.setTitle(title);
		if (args.length >= 1)
			error.setDescription(args[0]);
		if (args.length >= 2)
			error.setFooter(args[1]);
		
		channel.sendMessage(error.build()).queue();
		error.clear();
	}
}
