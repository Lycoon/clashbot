package com.lycoon.clashbot.commands.clan;

import static com.lycoon.clashbot.utils.DrawUtils.*;
import static com.lycoon.clashbot.utils.FileUtils.*;
import static com.lycoon.clashbot.utils.ErrorUtils.*;
import static com.lycoon.clashbot.utils.DatabaseUtils.*;
import static com.lycoon.clashbot.utils.CoreUtils.*;

import com.lycoon.clashapi.models.clan.ClanMember;
import com.lycoon.clashapi.models.clan.Clan;
import com.lycoon.clashapi.models.common.Label;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ClanCommand {
    private final static int WIDTH = 932;
    private final static int HEIGHT = 322;
    private final static float FONT_SIZE = 12f;

    private static final Color backgroundColor = new Color(0xe7e7e1);
    private static final Color clanNameColor = new Color(0xfeffaf);
    private static final Color clanTypeOpenColor = new Color(0xaede91);
    private static final Color clanTypeClosedColor = new Color(0xd87878);
    private static final Color clanTypeInviteOnlyColor = new Color(0xfbbf70);
    private static final Color valueColor = new Color(0x444545);

    private final static Map<String, String> regions = new HashMap<>() {{
        put("Europe", "europe");
        put("North America", "north.america");
        put("South America", "south.america");
        put("Asia", "asia");
        put("Australia", "australia");
        put("Africa", "africa");
        put("International", "international");
    }};

    private final static Map<String, String> labels = new HashMap<>() {{
        put("Clan Wars", "label.clanwars");
        put("Clan War League", "label.clanwarleague");
        put("Trophy Pushing", "label.trophy");
        put("Friendly Wars", "label.friendlywars");
        put("Clan Games", "label.clangames");
        put("Builder Base", "label.builderbase");
        put("Base Designing", "label.basedesigning");
        put("International", "label.international");
        put("Farming", "label.farming");
        put("Donations", "label.donations");
        put("Friendly", "label.friendly");
        put("Talkative", "label.talkative");
        put("Underdog", "label.underdog");
        put("Relaxed", "label.relaxed");
        put("Competitive", "label.competitive");
        put("Newbie Friendly", "label.newbie");
        put("Clan Capital", "label.capital");
    }};

    public static void call(SlashCommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getOptions().isEmpty())
                execute(event);
            else
                execute(event, Objects.requireNonNull(event.getOption("clan_tag")).getAsString());
        });
    }

    public static String getClanChief(List<ClanMember> members) {
        for (ClanMember member : members)
            if (member.getRole().equals("leader"))
                return member.getName();

        return "";
    }

    public static Clan getClan(SlashCommandEvent event, Locale lang, String[] args) {
        // Checking rate limitation
        if (!checkThrottle(event, lang))
            return null;

        Clan clan = null;
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        String tag = args.length > 0 ? args[0] : getClanTag(event.getMember().getIdLong());

        if (tag == null) {
            sendError(event, i18n.getString("set.clan.error"),
                    MessageFormat.format(i18n.getString("cmd.general.tip"), Command.SET_CLAN.formatCommand()));
            return null;
        }

        try {
            clan = ClashBotMain.clashAPI.getClan(tag);
        } catch (IOException ignored) {
        } catch (ClashAPIException e) {
            sendExceptionError(event, i18n, e, tag, "clan");
            return null;
        }
        return clan;
    }

    public static void execute(SlashCommandEvent event, String... args) {
        Locale lang = LangUtils.getLanguage(event.getMember().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        NumberFormat nf = NumberFormat.getInstance(lang);

        Clan clan = getClan(event, lang, args);
        if (clan == null)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = initGraphics(WIDTH, HEIGHT, image);

        Font font = getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        // Color background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Top background
        g2d.drawImage(getImageFromFile("backgrounds/clan-profile.png"), 0, 0, null);

        // Clan badge
        g2d.drawImage(getImageFromUrl(clan.getBadgeUrls().getLarge()), 20, 20, 95, 95, null);

        // Clan name
        drawShadowedString(g2d, clan.getName(), 125, 60, 26, 2, clanNameColor);

        // Tag
        drawShadowedString(g2d, clan.getTag(), 125, 85, 16);

        // Clan leader
        drawShadowedStringLeft(g2d, MessageFormat.format(i18n.getString("clan.leader"), getClanChief(clan.getMemberList())), 694, 55, 14);

        // Members size
        drawShadowedStringLeft(g2d, MessageFormat.format(i18n.getString("clan.members"), clan.getMembers()), 694, 85, 20);

        // Clan location
        if (clan.getLocation() != null) {
            if (clan.getLocation().isCountry()) {
                Locale clanLocale = new Locale("", clan.getLocation().getCountryCode().toLowerCase());
                drawShadowedStringLeft(g2d, clanLocale.getDisplayCountry(lang), 905, 33, 14f, 2);
            } else
                drawShadowedStringLeft(g2d, i18n.getString(regions.get(clan.getLocation().getName())), 905, 33, 14f, 2);
        } else
            drawShadowedStringLeft(g2d, i18n.getString("undefined"), 905, 33, 14f, 2);

        // Invitation type
        switch (clan.getType()) {
            case "inviteOnly" -> drawShadowedStringLeft(g2d, i18n.getString("clan.type.inviteonly"), 905, 67, 12f, 2, clanTypeInviteOnlyColor);
            case "open" -> drawShadowedStringLeft(g2d, i18n.getString("clan.type.open"), 905, 67, 12f, 2, clanTypeOpenColor);
            default -> drawShadowedStringLeft(g2d, i18n.getString("clan.type.closed"), 905, 67, 12f, 2, clanTypeClosedColor);
        }

        // War frequency
        drawShadowedStringLeft(g2d, i18n.getString("clan.frequency." + clan.getWarFrequency()), 905, 101, 12f, 2);

        // Clan labels
        Rectangle labelsTitleRect = new Rectangle(8, 155, 269, 5);
        drawCenteredString(g2d, labelsTitleRect, font.deriveFont(16f), i18n.getString("clan.labels.title"));

        if (clan.getLabels().size() <= 0) {
            // If there is no label set
            Rectangle noLabelTitleRect = new Rectangle(8, 230, 269, 5);
            drawCenteredString(g2d, noLabelTitleRect, font.deriveFont(16f), i18n.getString("clan.labels.notset"));
        } else {
            // Printing each label
            for (int i = 0; i < clan.getLabels().size(); i++) {
                Label label = clan.getLabels().get(i);
                if (!labels.containsKey(label.getName()))
                    continue;

                g2d.drawImage(getImageFromFile("backgrounds/field-placeholder.png"), 70, 190 + i * 40, 185, 24, null);
                g2d.drawImage(getImageFromUrl(label.getIconUrls().getMedium()), 25, 185 + i * 40, 35, 35, null);
                drawShadowedString(g2d, i18n.getString(labels.get(label.getName())), 80, 207 + i * 40, 12f);
            }
        }

        // Trophies section
        Rectangle trophiesTitleRect = new Rectangle(279, 155, 318, 5);
        drawCenteredString(g2d, trophiesTitleRect, font.deriveFont(16f), i18n.getString("clan.trophies.title"));
        drawShadowedString(g2d, i18n.getString("clan.trophies.required"), 294, 197, 13f);
        drawShadowedString(g2d, i18n.getString("clan.trophies.average"), 294, 227, 13f);
        drawShadowedString(g2d, i18n.getString("clan.trophies.total"), 294, 262, 13f);
        drawShadowedString(g2d, i18n.getString("clan.trophies.total.builder"), 294, 292, 13f);

        drawSimpleString(g2d, nf.format(clan.getRequiredTrophies()), 503, 198, 12f, valueColor);

        var members = clan.getMemberList();
        int averageTrophies = (int) members.stream().mapToInt(ClanMember::getTrophies).average().orElse(0);
        int totalTrophies = members.stream().mapToInt(ClanMember::getTrophies).sum();
        int totalVersusTrophies = members.stream().mapToInt(ClanMember::getVersusTrophies).sum();

        drawSimpleString(g2d, nf.format(averageTrophies), 503, 228, 12f, valueColor);
        drawSimpleString(g2d, nf.format(totalTrophies), 503, 263, 12f, valueColor);
        drawSimpleString(g2d, nf.format(totalVersusTrophies), 503, 293, 12f, valueColor);

        // Clan wars section
        Rectangle clanwarTitleRect = new Rectangle(600, 155, 318, 5);
        drawCenteredString(g2d, clanwarTitleRect, font.deriveFont(16f), i18n.getString("clan.clanwar.title"));
        drawShadowedString(g2d, i18n.getString("wins"), 621, 197, 13f);
        drawShadowedString(g2d, i18n.getString("losses"), 621, 227, 13f);
        drawShadowedString(g2d, i18n.getString("draws"), 621, 262, 13f);
        drawShadowedString(g2d, i18n.getString("clan.clanwar.streak"), 621, 292, 13f);

        drawSimpleString(g2d, nf.format(clan.getWarWins()), 826, 198, 12f, valueColor);
        drawSimpleString(g2d, nf.format(clan.getWarWinStreak()), 826, 293, 12f, valueColor);

        drawSimpleString(g2d, clan.isWarLogPublic() ? nf.format(clan.getWarLosses()) : i18n.getString("warlog.private"), 826, 228, 12f, valueColor);
        drawSimpleString(g2d, clan.isWarLogPublic() ? nf.format(clan.getWarTies()) : i18n.getString("warlog.private"), 826, 263, 12f, valueColor);

        sendImage(event, image);

        g2d.dispose();
    }
}
