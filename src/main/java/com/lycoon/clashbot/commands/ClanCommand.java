package com.lycoon.clashbot.commands;

import com.lycoon.clashapi.cocmodels.clan.ClanMember;
import com.lycoon.clashapi.cocmodels.clan.ClanModel;
import com.lycoon.clashapi.cocmodels.clan.Label;
import com.lycoon.clashapi.core.exception.ClashAPIException;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.lang.LangUtils;
import com.lycoon.clashbot.utils.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public class ClanCommand
{
    private final static int WIDTH = 932;
    private final static int HEIGHT = 322;
    private final static float FONT_SIZE = 12f;

    private static Color backgroundColor = new Color(0xe7e7e1);
    private static Color clanNameColor = new Color(0xfeffaf);
    private static Color clanTypeOpenColor = new Color(0xaede91);
    private static Color clanTypeClosedColor = new Color(0xd87878);
    private static Color clanTypeInviteOnlyColor = new Color(0xfbbf70);
    private static Color valueColor = new Color(0x444545);

    private final static Map<String, String> regions = new HashMap<String, String>()
    {{
        put("Europe", "europe");
        put("North America", "north.america");
        put("South America", "south.america");
        put("Asia", "asia");
        put("Australia", "australia");
        put("Africa", "africa");
        put("International", "international");
    }};

    private final static Map<String, String> labels = new HashMap<String, String>()
    {{
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
    }};

    public static void dispatch(MessageReceivedEvent event, String... args)
    {
        if (args.length > 1)
            ClanCommand.execute(event, args[1]);
        else
            ClanCommand.execute(event);
    }

    public static int getAverageTrophies(List<ClanMember> members)
    {
        int average = 0;
        for (ClanMember member : members)
            average += member.getTrophies();
        return average / members.size();
    }

    public static String getClanChief(List<ClanMember> members)
    {
        for (ClanMember member : members)
        {
            if (member.getRole().equals("leader"))
                return member.getName();
        }
        return "";
    }

    public static ClanModel getClan(MessageReceivedEvent event, Locale lang, String[] args)
    {
        // Checking rate limitation
        if (!CoreUtils.checkThrottle(event, lang))
            return null;

        ClanModel clan = null;
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        String tag = args.length > 0 ? args[0] : DatabaseUtils.getClanTag(event.getAuthor().getIdLong());

        if (tag == null)
        {
            ErrorUtils.sendError(event.getChannel(), i18n.getString("set.clan.error"), i18n.getString("set.clan.help"));
            return null;
        }

        try
        {
            clan = ClashBotMain.clashAPI.getClan(tag);
        } catch (IOException ignored)
        {
        } catch (ClashAPIException e)
        {
            ErrorUtils.sendExceptionError(event, i18n, e, tag, "clan");
            return null;
        }
        return clan;
    }

    public static void execute(MessageReceivedEvent event, String... args)
    {
        MessageChannel channel = event.getChannel();

        Locale lang = LangUtils.getLanguage(event.getAuthor().getIdLong());
        ResourceBundle i18n = LangUtils.getTranslations(lang);
        NumberFormat nf = NumberFormat.getInstance(lang);

        ClanModel clan = getClan(event, lang, args);
        if (clan == null)
            return;

        // Initializing image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = DrawUtils.initGraphics(WIDTH, HEIGHT, image);

        Font font = DrawUtils.getFont("Supercell.ttf").deriveFont(FONT_SIZE);
        g2d.setFont(font);

        // Color background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Top background
        g2d.drawImage(FileUtils.getImageFromFile("backgrounds/clan-profile.png"), 0, 0, null);

        // Clan badge
        g2d.drawImage(FileUtils.getImageFromUrl(clan.getBadgeUrls().getLarge()), 20, 20, 95, 95, null);

        // Clan name
        DrawUtils.drawShadowedString(g2d, clan.getName(), 125, 60, 26, 2, clanNameColor);

        // Tag
        DrawUtils.drawShadowedString(g2d, clan.getTag(), 125, 85, 16);

        // Clan leader
        DrawUtils.drawShadowedStringLeft(g2d, MessageFormat.format(i18n.getString("clan.leader"), getClanChief(clan.getMemberList())), 694, 55, 14);

        // Members size
        DrawUtils.drawShadowedStringLeft(g2d, MessageFormat.format(i18n.getString("clan.members"), clan.getMembers()), 694, 85, 20);

        // Clan location
        if (clan.getLocation() != null)
        {
            if (clan.getLocation().getIsCountry())
            {
                Locale clanLocale = new Locale("", clan.getLocation().getCountryCode().toLowerCase());
                DrawUtils.drawShadowedStringLeft(g2d, clanLocale.getDisplayCountry(lang), 905, 33, 14f, 2);
            }
            else
                DrawUtils.drawShadowedStringLeft(g2d, i18n.getString(regions.get(clan.getLocation().getName())), 905, 33, 14f, 2);
        }
        else
            DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("undefined"), 905, 33, 14f, 2);

        // Invitation type
        switch (clan.getType())
        {
            case "inviteOnly":
                DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("clan.type.inviteonly"), 905, 67, 12f, 2, clanTypeInviteOnlyColor);
                break;
            case "open":
                DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("clan.type.open"), 905, 67, 12f, 2, clanTypeOpenColor);
                break;
            default:
                DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("clan.type.closed"), 905, 67, 12f, 2, clanTypeClosedColor);
        }

        // War frequency
        DrawUtils.drawShadowedStringLeft(g2d, i18n.getString("clan.frequency." + clan.getWarFrequency()), 905, 101, 12f, 2);

        // Clan labels
        Rectangle labelsTitleRect = new Rectangle(8, 155, 269, 5);
        DrawUtils.drawCenteredString(g2d, labelsTitleRect, font.deriveFont(16f), i18n.getString("clan.labels.title"));

        if (clan.getLabels().size() <= 0)
        {
            // If there is no label set
            Rectangle noLabelTitleRect = new Rectangle(8, 230, 269, 5);
            DrawUtils.drawCenteredString(g2d, noLabelTitleRect, font.deriveFont(16f), i18n.getString("clan.labels.notset"));
        }
        else
        {
            // Printing each label
            for (int i = 0; i < clan.getLabels().size(); i++)
            {
                Label label = clan.getLabels().get(i);
                g2d.drawImage(FileUtils.getImageFromFile("backgrounds/field-placeholder.png"), 70, 190 + i * 40, 185, 24, null);
                g2d.drawImage(FileUtils.getImageFromUrl(label.getIconUrls().getMedium()), 25, 185 + i * 40, 35, 35, null);
                DrawUtils.drawShadowedString(g2d, i18n.getString(labels.get(label.getName())), 80, 207 + i * 40, 12f);
            }
        }

        // Trophies section
        Rectangle trophiesTitleRect = new Rectangle(279, 155, 318, 5);
        DrawUtils.drawCenteredString(g2d, trophiesTitleRect, font.deriveFont(16f), i18n.getString("clan.trophies.title"));
        DrawUtils.drawShadowedString(g2d, i18n.getString("clan.trophies.required"), 294, 197, 13f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("clan.trophies.average"), 294, 227, 13f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("clan.trophies.total"), 294, 262, 13f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("clan.trophies.total.builder"), 294, 292, 13f);

        DrawUtils.drawSimpleString(g2d, nf.format(clan.getRequiredTrophies()), 503, 198, 12f, valueColor);
        DrawUtils.drawSimpleString(g2d, nf.format(getAverageTrophies(clan.getMemberList())), 503, 228, 12f, valueColor);
        DrawUtils.drawSimpleString(g2d, nf.format(clan.getTotalTrophies()), 503, 263, 12f, valueColor);
        DrawUtils.drawSimpleString(g2d, nf.format(clan.getTotalVersusTrophies()), 503, 293, 12f, valueColor);

        // Clan wars section
        Rectangle clanwarTitleRect = new Rectangle(600, 155, 318, 5);
        DrawUtils.drawCenteredString(g2d, clanwarTitleRect, font.deriveFont(16f), i18n.getString("clan.clanwar.title"));
        DrawUtils.drawShadowedString(g2d, i18n.getString("wins"), 621, 197, 13f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("losses"), 621, 227, 13f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("draws"), 621, 262, 13f);
        DrawUtils.drawShadowedString(g2d, i18n.getString("clan.clanwar.streak"), 621, 292, 13f);

        DrawUtils.drawSimpleString(g2d, nf.format(clan.getWarWins()), 826, 198, 12f, valueColor);
        DrawUtils.drawSimpleString(g2d, nf.format(clan.getWarWinStreak()), 826, 293, 12f, valueColor);

        DrawUtils.drawSimpleString(g2d, clan.isWarLogPublic() ? nf.format(clan.getWarLosses()) : i18n.getString("warlog.private"), 826, 228, 12f, valueColor);
        DrawUtils.drawSimpleString(g2d, clan.isWarLogPublic() ? nf.format(clan.getWarTies()) : i18n.getString("warlog.private"), 826, 263, 12f, valueColor);

        FileUtils.sendImage(event, image, clan.getTag(), "png");

        g2d.dispose();
    }
}
