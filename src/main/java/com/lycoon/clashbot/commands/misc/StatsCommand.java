package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.CoreUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class StatsCommand {
    public static void call(SlashCommandEvent event) {
        execute(event);
    }

    public static void execute(SlashCommandEvent event) {
        Locale lang = LangUtils.getLanguage(event.getMember().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        NumberFormat nf = NumberFormat.getInstance(lang);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GRAY);
        builder.setTitle("Stats panel");

        int members = 0;
        List<Guild> guilds = ClashBotMain.jda.getGuilds();
        for (Guild guild : guilds) members += guild.getMemberCount();

        String general = "";
        general += "▫ 🏓 Ping of `" + nf.format(ClashBotMain.jda.getRestPing().complete()) + "ms`\n\n";
        general += "▫ 💻 Running on `" + nf.format(guilds.size()) + "` servers\n";
        general += "▫ 👥 Used by `" + nf.format(members) + "` members\n";
        builder.setDescription(general);

        CoreUtils.sendMessage(event, i18n, builder);
    }
}
