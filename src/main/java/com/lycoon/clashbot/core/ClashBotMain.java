package com.lycoon.clashbot.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.lycoon.clashapi.core.ClashAPI;
import com.lycoon.clashbot.event.EventListener;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class ClashBotMain 
{
	private static final String CONFIG = "tokens.properties";
	public static ClashAPI clashAPI;
	public static CacheComponents cached;
	
	public static void main(String[] args) throws IOException, LoginException
	{
		Properties tokens = new Properties();
		tokens.load(new FileInputStream(CONFIG));

		JDABuilder builder = JDABuilder.createDefault(tokens.getProperty("discord"));
		builder.addEventListeners(new EventListener());
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("Clash of Clans"));
		builder.build();

		clashAPI = new ClashAPI(tokens.getProperty("clash-of-clans"));
		cached = CacheComponents.getInstance();
	}
}
