package com.lycoon.clashbot.core;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ErrorEmbed
{
	public static void sendError(MessageChannel channel, String title, String message, String footer)
	{
		EmbedBuilder error = new EmbedBuilder();
		error.setColor(Color.RED);
		error.setTitle(title);
		error.setDescription(message);
		error.setFooter(footer);
		
		channel.sendMessage(error.build()).queue();
		error.clear();
	}
	
	public static void sendError(MessageChannel channel, String title, String message)
	{
		sendError(channel, title, message, "");
	}
}
