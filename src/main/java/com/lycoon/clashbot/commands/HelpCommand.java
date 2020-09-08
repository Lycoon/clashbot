package com.lycoon.clashbot.commands;

import java.awt.Color;
import java.util.ResourceBundle;

import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand
{
	private static ResourceBundle i18n;
	private static EmbedBuilder builder;
	
	public static void drawCategory(CommandCategory category, Command[] commands)
	{
		StringBuilder categoryField = new StringBuilder();
		for (int i=0; i < commands.length; i++)
		{
			Command cmd = commands[i];
			if (cmd.getCategory().equals(category))
			{
				categoryField.append("▫ `").append(cmd.formatFullCommand()).append("` ");
				categoryField.append(i18n.getString(cmd.getDescription())).append("\n");
			}
		}
		builder.addField(i18n.getString(category.toString()), categoryField.toString(), false);
	}
	
	public static void execute(MessageReceivedEvent event)
	{
		i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
		builder = new EmbedBuilder();
		
		builder.setColor(Color.GRAY);
		builder.setTitle(i18n.getString("cmd.help.panel"));
		builder.appendDescription(i18n.getString("tip.help"));
		
		CommandCategory[] categories = CommandCategory.values();
		for (CommandCategory category : categories)
		{
			drawCategory(category, Command.values());
		}
		
		event.getChannel().sendMessage(builder.build()).queue();
	}
}
