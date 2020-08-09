package com.lycoon.clashbot.event;

import com.lycoon.clashbot.commands.Command;
import com.lycoon.clashbot.commands.InfoCommand;
import com.lycoon.clashbot.commands.LangCommand;
import com.lycoon.clashbot.commands.PlayerCommand;
import com.lycoon.clashbot.commands.WarCommand;
import com.lycoon.clashbot.commands.WarLeagueCommand;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter
{
	private static final String PREFIX = "!";
	
	static boolean isCommand(String arg, Command cmd)
	{
		return arg.equals(PREFIX + cmd);
	}
	
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
    	String message = event.getMessage().getContentDisplay();
    	String[] args = message.split(" ");
    	
    	// If a command is triggered
    	if (args.length > 0)
    	{
    		MessageChannel channel = event.getChannel();
    		
    		if (isCommand(args[0], Command.PLAYER)) // !player command
    		{
    			if (args.length > 1)
    				PlayerCommand.execute(channel, args[1]);
    		}
    		else if (isCommand(args[0], Command.LANG)) // !lang command
    		{
    			if (args.length > 1)
    				LangCommand.execute(channel, args[1]);
    			else
    				LangCommand.executeInfo(channel);
    		}
    		else if (isCommand(args[0], Command.WAR))
    		{
    			if (args.length > 1)
    				WarCommand.execute(channel, args[1]);
    		}
    		else if (isCommand(args[0], Command.WARLEAGUE))
    		{
    			if (args.length > 1)
    				WarLeagueCommand.execute(channel, args[1]);
    		}
    		else if (isCommand(args[0], Command.INFO)) // !info command
    		{
    			InfoCommand.execute(channel);
    		}
    	}
    }
}
