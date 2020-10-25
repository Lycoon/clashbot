package com.lycoon.clashbot.core;

import com.lycoon.clashapi.core.ClashAPI;
import com.lycoon.clashbot.event.EventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClashBotMain 
{
	private static final String CONFIG = "tokens.properties";
	public static ClashAPI clashAPI;
	public static CacheComponents cached;
	public static JDA jda;
	
	public static void main(String[] args) throws IOException, LoginException
	{
		Properties tokens = new Properties();
		tokens.load(new FileInputStream(CONFIG));

		JDABuilder builder = JDABuilder.createDefault(tokens.getProperty("discord"));
		builder.addEventListeners(new EventListener());
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("Clash of Clans"));
		jda = builder.build();

		clashAPI = new ClashAPI(tokens.getProperty("clash-of-clans"));
		cached = CacheComponents.getInstance();
	}
}
