package com.lycoon.clashbot.core;

import com.lycoon.clashapi.core.ClashAPI;
import com.lycoon.clashbot.event.EventListener;
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

public class ClashBotMain {
    static final String CONFIG = "tokens.properties";

    public static Logger LOGGER = LoggerFactory.getLogger(ClashBotMain.class.getName());
    public static long[] owners = {138282927502000128L, 198485955701768192L};
    public static ClashAPI clashAPI;
    public static CacheComponents cached;
    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        Properties tokens = new Properties();
        try {
            // Loading secret tokens
            tokens.load(new FileInputStream(CONFIG));
            LOGGER.info("Secret tokens loaded");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        JDABuilder builder = JDABuilder.createDefault(tokens.getProperty("discord"));
        builder.addEventListeners(new EventListener());
        builder.setActivity(Activity.playing("Clash of Clans"));
        builder.setStatus(OnlineStatus.ONLINE);
        jda = builder.build();

        clashAPI = new ClashAPI(tokens.getProperty("clash-of-clans"));
        cached = CacheComponents.getInstance();
    }
}
