package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.DBUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetCommand
{
	public static void executePlayer(MessageReceivedEvent event, String tag)
	{
		DBUtils.setPlayerTag(event.getAuthor().getIdLong(), tag);
		ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.GREEN);
		builder.setTitle(MessageFormat.format(i18n.getString("set.player.success"), tag));
		builder.setFooter(i18n.getString("set.player.tip"));
		
		event.getChannel().sendMessage(builder.build()).queue();
		builder.clear();
	}
	
	public static void executeClan(MessageReceivedEvent event, String tag)
	{
		DBUtils.setClanTag(event.getAuthor().getIdLong(), tag);
		ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.GREEN);
		builder.setTitle(MessageFormat.format(i18n.getString("set.clan.success"), tag));
		builder.setFooter(i18n.getString("set.clan.tip"));
		
		event.getChannel().sendMessage(builder.build()).queue();
		builder.clear();
	}
	
	public static void executeLang(MessageReceivedEvent event, String language)
	{
		long id = event.getAuthor().getIdLong();
		if (LangUtils.isSupportedLanguage(language))
		{
			DBUtils.setUserLang(id, language);
			Locale lang = new Locale(language);
			ResourceBundle i18n = LangUtils.getTranslations(lang);
			
			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setTitle(i18n.getString("lang.flag")+
					" " +MessageFormat.format(i18n.getString("lang.success"), lang.getDisplayLanguage(lang)));
			builder.setDescription(i18n.getString("lang.info.other"));
			
			event.getChannel().sendMessage(builder.build()).queue();
			builder.clear();
		}
		else
		{
			Locale lang = LangUtils.getLanguage(id);
			ResourceBundle i18n = LangUtils.getTranslations(lang);

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle(MessageFormat.format(i18n.getString("lang.error"), language));
			builder.appendDescription(i18n.getString("lang.info.supported"));

			int length = LangUtils.LANGUAGES.length;
			double perColumn = Math.ceil(length / 3D);
			StringBuilder str = new StringBuilder();
			for (int i=0; i < length; i++)
			{
				if (i != 0 && i % perColumn == 0)
				{
					builder.addField(str.toString(), "", true);
					str = new StringBuilder();
				}

				String curr = LangUtils.LANGUAGES[i];
				Locale localeLang = new Locale(curr);
				str.append("▫ ").append(localeLang.getDisplayLanguage(lang)).append(" (`").append(curr).append("`)\n");

				if (i == length-1)
					builder.addField(str.toString(), "", true);
			}
			builder.setFooter(i18n.getString("lang.suggest.contact"));
			event.getChannel().sendMessage(builder.build()).queue();
		}
	}
}
