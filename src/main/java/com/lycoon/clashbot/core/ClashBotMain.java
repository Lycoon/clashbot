package com.lycoon.clashbot.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.security.auth.login.LoginException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lycoon.clashapi.core.ClashAPI;
import com.lycoon.clashbot.event.EventListener;
import com.lycoon.clashbot.lang.LangUtils;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class ClashBotMain 
{
	private static final String TOKEN_FILE = "keys.json";
	public static ClashAPI clashAPI;
	
	public static void main(String[] args) throws JsonSyntaxException, IOException, LoginException 
	{
    	JDABuilder builder = JDABuilder.createDefault(getTokenModel().getDiscordToken());
    	builder.addEventListeners(new EventListener());
    	
    	builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Clash of Clans"));
        builder.build();

        LangUtils.updateLanguage("en");
        clashAPI = new ClashAPI(getTokenModel().getGameToken());
	}
	
    private static TokenModel getTokenModel() throws JsonSyntaxException, IOException
    {
    	Gson gson = new Gson();
    	return gson.fromJson(getFileContent(TOKEN_FILE), TokenModel.class);
    }
    
    private static String getFileContent(String file) throws IOException
    {
    	return new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
    }
}
