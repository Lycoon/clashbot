package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.commands.CommandCategory;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ResourceBundle;

public class HelpCommand
{
    private static ResourceBundle i18n;
    private static EmbedBuilder builder;

    public static void call(SlashCommandInteractionEvent event)
    {
        execute(event);
    }

    public static void drawCategory(CommandCategory category, Command[] commands)
    {
        StringBuilder categoryField = new StringBuilder();
        for (Command cmd : commands)
            if (cmd.getCategory().equals(category)) {
                categoryField.append("▫ `").append(cmd.formatCommand()).append("` ");
                categoryField.append(i18n.getString(cmd.getDescription())).append("\n");
            }
        builder.addField(i18n.getString(category.toString()), categoryField.toString(), false);
    }

    public static void execute(SlashCommandInteractionEvent event)
    {
        i18n = LangUtils.getTranslations(event.getMember().getIdLong());
        builder = new EmbedBuilder();

        builder.setColor(Color.GRAY);
        builder.setTitle(i18n.getString("cmd.help.panel"));
        builder.appendDescription(i18n.getString("tip.help"));

        CommandCategory[] categories = CommandCategory.values();
        for (CommandCategory category : categories)
            drawCategory(category, Command.values());

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
