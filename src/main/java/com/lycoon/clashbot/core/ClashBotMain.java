package com.lycoon.clashbot.core;

import com.lycoon.clashapi.core.ClashAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClashBotMain
{
    private static final String CONFIG = "tokens.properties";
    private static Properties tokens;

    public static long[] owners = {138282927502000128L, 198485955701768192L};
    public static final String VERSION = "2.0.0";
    public static final String INVITE = "https://discord.com/api/oauth2/authorize?client_id=734481969630543883&permissions=2147534848&scope=bot%20applications.commands";
    public static Logger LOGGER = LoggerFactory.getLogger(ClashBotMain.class.getName());

    // Following attributes are initialized on launch
    public static ClashAPI clashAPI;
    public static CacheComponents cached;
    public static JDA jda;

    /*
     * ############################## Clashbot ##############################
     * 		                    Author: Hugo BOIS
     * 			             All rights reserved 2024
     * ######################################################################
     */
    public static void main(String[] args) throws InterruptedException {
        loadTokensFromConfig();
        buildDiscordInstance();

        clashAPI = new ClashAPI(tokens.getProperty("clash-of-clans"));
        cached = CacheComponents.getInstance();
    }

    /*
     * Load secret tokens from config file for Clash of Clans and Discord APIs
     */
    private static void loadTokensFromConfig()
    {
        try
        {
            tokens.load(new FileInputStream(CONFIG));
            LOGGER.info("Secret tokens loaded");
        }
        catch (IOException e) { LOGGER.error(e.getMessage()); }
    }

    /*
     * Build Discord instance from previous loaded token
     */
    private static void buildDiscordInstance() throws InterruptedException
    {
        JDABuilder builder = JDABuilder.createDefault(tokens.getProperty("discord"));
        builder.addEventListeners(new EventListener());
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Clash of Clans"));
        jda = builder.build().awaitReady();
    }
}
