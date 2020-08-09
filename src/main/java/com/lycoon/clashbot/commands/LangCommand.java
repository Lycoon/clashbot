package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.lycoon.clashbot.core.ErrorEmbed;
import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LangCommand
{
	public static void execute(MessageChannel channel, String lang)
	{
		if (!LangUtils.updateLanguage(lang))
		{
			ResourceBundle language = LangUtils.bundle;
			ErrorEmbed.sendError(channel, 
					MessageFormat.format(language.getString("lang.error"), lang), 
					language.getString("lang.info.supported")+ "\n" +LangUtils.getSupportedLanguages(),
					language.getString("lang.suggest.contact"));
		}
		else
		{
			ResourceBundle language = LangUtils.bundle;
			EmbedBuilder builder = new EmbedBuilder();
			
			builder.setColor(Color.GREEN);
			builder.setTitle(language.getString("lang.flag")+
					" " +MessageFormat.format(language.getString("lang.success"), LangUtils.currentLang.getDisplayLanguage(LangUtils.currentLang)));
			builder.setFooter(language.getString("lang.info.other"));
			
			channel.sendMessage(builder.build()).queue();
			builder.clear();
		}
	}
	
	public static void executeInfo(MessageChannel channel)
	{
		ResourceBundle language = LangUtils.bundle;
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setColor(Color.GRAY);
		builder.setTitle(language.getString("lang.flag")+ 
				" " +MessageFormat.format(language.getString("lang.current"), LangUtils.currentLang.getDisplayLanguage(LangUtils.currentLang)));
		builder.setFooter(language.getString("lang.info.other"));
		
		channel.sendMessage(builder.build()).queue();
		builder.clear();
	}
}
