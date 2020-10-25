package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LangCommand
{
	public static void execute(MessageReceivedEvent event)
	{
		Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
		ResourceBundle i18n = LangUtils.getTranslations(lang);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.GRAY);
		builder.setTitle(i18n.getString("lang.flag")+ 
				"  " +MessageFormat.format(i18n.getString("lang.current"), lang.getDisplayLanguage(lang)));
		builder.setDescription(i18n.getString("lang.info.other"));
		
		event.getChannel().sendMessage(builder.build()).queue();
		builder.clear();
	}
}
