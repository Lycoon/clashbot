package com.lycoon.clashbot.utils;

import com.lycoon.clashbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ErrorUtils
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

	public static int checkIndex(MessageReceivedEvent event, ResourceBundle i18n, String arg, int max)
	{
		int index;
		try
		{
			index = Integer.parseInt(arg);
			if (index < 1 || index > max)
			{
				ErrorUtils.sendError(event.getChannel(),
						i18n.getString("wrong.usage"), MessageFormat.format(i18n.getString("exception.index"), 1, max));
				return -1;
			}
		}
		catch(NumberFormatException e)
		{
			ErrorUtils.sendError(event.getChannel(),
					i18n.getString("wrong.usage"), MessageFormat.format(i18n.getString("exception.index"), 1, max));
			return -1;
		}
		return index;
	}

	public static void throwCommandError(MessageChannel channel, ResourceBundle i18n, Command cmd)
	{
		ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
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
