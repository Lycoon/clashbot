package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import com.lycoon.clashbot.utils.DatabaseUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ResourceBundle;

public class ClearCommand
{
    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        execute(event);
    }

    public static void execute(MessageReceivedEvent event)
    {
        long id = event.getAuthor().getIdLong();
        ResourceBundle i18n = LangUtils.getTranslations(id);
        DatabaseUtils.deleteUser(id);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(CoreUtils.validColor);
        builder.setTitle(i18n.getString("clear.success"));

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
