package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.commands.settings.AdminCommand;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.ErrorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ResourceBundle;

public class ServersCommand
{
    private static final int PAGE_SIZE = 10;

    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        if (AdminCommand.isAdmin(event.getAuthor().getIdLong()))
            execute(event, args);
        else
        {
            ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
            ErrorUtils.sendError(event.getChannel(), i18n.getString("exception.permission"));
        }
    }

    public static void execute(MessageReceivedEvent event, String[] args)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);

        builder.setTitle("Clashbot stat information");
        int size = ClashBotMain.jda.getGuilds().size();

        String general = "";
        general += "• Running: `" + size + "` servers\n";
        general += "• Rest ping: " + ClashBotMain.jda.getRestPing().complete() + "\n";
        general += "• Gateway ping: " + ClashBotMain.jda.getGatewayPing() + "\n";
        general += "• Status: " + ClashBotMain.jda.getStatus().name() + "\n";
        builder.addField("General", general, false);

        int page = 0;
        if (args.length > 1)
        {
            try
            {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored)
            {
            }
        }

        int begin = page * PAGE_SIZE;

        StringBuilder servers = new StringBuilder();
        for (int i = begin; i < size && i < begin + PAGE_SIZE; i++)
        {
            Guild guild = ClashBotMain.jda.getGuilds().get(i);
            User owner = guild.retrieveOwner().complete().getUser();
            servers.append("• ").append(guild.getName()).
                    append(" (").append(guild.getMemberCount()).append(" members) ").
                    append("`").append(owner.getName()).append("#").append(owner.getDiscriminator()).
                    append("`\n");
        }
        if (begin <= size)
            builder.addField("Servers", servers.toString(), false);

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
