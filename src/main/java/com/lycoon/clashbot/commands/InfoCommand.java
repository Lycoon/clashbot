package com.lycoon.clashbot.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class InfoCommand
{
	public static void execute(MessageChannel channel)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.GRAY);
		builder.setTitle("Info - ClashBot");
		
		channel.sendMessage(builder.build()).queue();
	}
}
