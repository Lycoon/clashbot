package com.lycoon.clashbot.commands;

import java.io.IOException;

import com.lycoon.clashapi.cocmodels.clanwar.WarInfo;
import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.core.ErrorEmbed;

import net.dv8tion.jda.api.entities.MessageChannel;

public class WarCommand
{
	public static void execute(MessageChannel channel, String tag)
	{
		WarInfo war = null;
		try 
		{
			war = ClashBotMain.clashAPI.getCurrentWar(tag);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if (!war.getState().equals("notInWar"))
		{
			System.out.println("WAR STATE: " +war.getState());
		}
		else
		{
			ErrorEmbed.sendError(channel, 
					"The war for clan's tag ''{0}'' could not have been found.", 
					"Make sure:\n"
					+ "▫ The clan tag is in the form of #AAAA00\n"
					+ "▫ The clan's war log is public\n"
					+ "▫ The clan is at war (not in league war)");
		}
	}
}
