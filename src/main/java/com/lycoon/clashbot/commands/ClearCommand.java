package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.util.ResourceBundle;

import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.DBUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearCommand
{
	public static void execute(MessageReceivedEvent event)
	{
		long id = event.getAuthor().getIdLong();
		ResourceBundle i18n = LangUtils.getTranslations(id);
		DBUtils.deleteUser(id);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.GREEN);
		builder.setTitle(i18n.getString("clear.success"));
		
		event.getChannel().sendMessage(builder.build()).queue();
		builder.clear();
	}
}
