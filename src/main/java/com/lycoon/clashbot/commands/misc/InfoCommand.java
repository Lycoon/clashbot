package com.lycoon.clashbot.commands.misc;

import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class InfoCommand
{
    private static final String TWITTER = "@LycoonMC";
    private static final String DISCORD = "Lycoon#7542";
    private static final String WEBSITE = "https://clashbot.app/";
    private static final String DISCORD_INVITE = "https://discord.gg/Cy86PDA";
    private static final String PATREON = "https://www.patreon.com/clashbot";

    static final String DISCORD_EMOJI = "<:discord:909058121236881468>";
    static final String PATREON_EMOJI = "<:patreon:909058096503062528>";
    static final String CLASHBOT_EMOJI = "<:clashbot:909058781583917107>";

    public static void call(SlashCommandInteractionEvent event) {
        execute(event);
    }

    public static void execute(SlashCommandInteractionEvent event)
    {
        Locale lang = LangUtils.getLanguage(event.getMember().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        NumberFormat nf = NumberFormat.getInstance(lang);
        EmbedBuilder builder = new EmbedBuilder();

        List<Guild> guilds = ClashBotMain.jda.getGuilds();

        builder.setColor(Color.GRAY);
        builder.setTitle(i18n.getString("cmd.info.panel"));
        builder.setDescription(i18n.getString("info.description") + "\n\n");

        builder.addField("Version", ClashBotMain.VERSION, true);
        builder.addField("Library", "Discord JDA", true);
        builder.addField("Author", "Lycoon#7542", true);
        builder.addField("Members", nf.format(guilds.stream().mapToInt(Guild::getMemberCount).sum()), true);
        builder.addField("Servers", nf.format(guilds.size()), true);
        builder.addField("Ping", nf.format(ClashBotMain.jda.getRestPing().complete()) + "ms", true);

        event.getHook().sendMessageEmbeds(builder.build()).addActionRow(
                Button.link(WEBSITE, "Official Website").withEmoji(Emoji.fromMarkdown(CLASHBOT_EMOJI)),
                Button.link(DISCORD_INVITE, "Official Discord").withEmoji(Emoji.fromMarkdown(DISCORD_EMOJI)),
                Button.link(PATREON, "Contribute").withEmoji(Emoji.fromMarkdown(PATREON_EMOJI))
        ).queue();
    }
}
