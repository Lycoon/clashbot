package com.lycoon.clashbot.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenModel
{
	@SerializedName("coc-token")
	@Expose
	private String cocToken;
	
	@SerializedName("discord-token")
	@Expose
	private String discordToken;
	
	public String getGameToken()
	{
		return cocToken;
	}
	
	public String getDiscordToken()
	{
		return discordToken;
	}
}
