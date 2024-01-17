package com.lycoon.clashbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ErrorUtils
{
    private static final String OFFICIAL_TWITTER = "https://twitter.com/ClashofClans";

    public static void sendExceptionError(SlashCommandInteractionEvent event, ResourceBundle i18n, Exception e, String... args)
    {
        switch (e.getMessage()) {
            case "400" -> sendError(event, i18n.getString("exception.other"));
            case "403" -> sendError(event, i18n.getString("exception.403"));
            case "404" -> sendError(event,
                    MessageFormat.format(i18n.getString("exception.404." + args[1]), args[0]),
                    i18n.getString("exception.format"));
            case "429" -> sendError(event, i18n.getString("exception.429"));
            case "503" -> sendError(event,
                    i18n.getString("exception.503"),
                    MessageFormat.format(i18n.getString("exception.status"), OFFICIAL_TWITTER));
            default -> sendError(event,
                    i18n.getString("exception.other"),
                    i18n.getString("exception.contact"));
        }
    }

    public static int checkIndex(SlashCommandInteractionEvent event, ResourceBundle i18n, String arg, int max)
    {
        int index;
        try {
            index = Integer.parseInt(arg);
            if (index < 1 || index > max) {
                ErrorUtils.sendError(event,
                        i18n.getString("wrong.usage"),
                        MessageFormat.format(i18n.getString("exception.index"), 1, max));
                return -1;
            }
        } catch (NumberFormatException e) {
            ErrorUtils.sendError(
                    event,
                    i18n.getString("wrong.usage"),
                    MessageFormat.format(i18n.getString("exception.index"), 1, max));
            return -1;
        }
        return index;
    }

	/*
	public static void throwCommandError(MessageChannel channel, ResourceBundle i18n, Command cmd)
	{
		String prefix = DBUtils.getServerPrefix(event.getGuild().getIdLong());
		ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
				MessageFormat.format(i18n.getString("tip.usage"), cmd.formatFullCommand(prefix)));
	}
	*/

    public static void sendError(SlashCommandInteractionEvent event, String title, String... args) {
        EmbedBuilder error = new EmbedBuilder();
        error.setColor(CoreUtils.invalidColor);
        error.setTitle(title);

        if (args.length >= 1)
            error.setDescription(args[0]);
        if (args.length >= 2)
            error.setFooter(args[1]);

        event.getHook().sendMessageEmbeds(error.build()).queue();
        error.clear();
    }
}
