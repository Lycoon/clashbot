package com.lycoon.clashbot.event;

import com.lycoon.clashbot.commands.*;
import com.lycoon.clashbot.utils.ErrorUtils;
import com.lycoon.clashbot.lang.LangUtils;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class EventListener extends ListenerAdapter
{
	static boolean isCommand(String arg, Command cmd)
	{
		return arg.equalsIgnoreCase(cmd.formatCommand());
	}
	
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
    	String message = event.getMessage().getContentDisplay();
    	
    	// If a command is triggered
    	if (message.startsWith(Command.PREFIX))
    	{
        	String[] args = message.split(" ");
        	ResourceBundle i18n = LangUtils.getTranslations(event.getAuthor().getIdLong());
        	
        	if (args.length > 0)
        	{
        		MessageChannel channel = event.getChannel();
        		if (isCommand(args[0], Command.SETLANG)) // !set command
        		{
        			if (args.length > 2)
        			{
        				switch (args[1].toLowerCase())
        				{
							case "player":
								SetCommand.executePlayer(event, args[2]);
								break;
							case "clan":
								SetCommand.executeClan(event, args[2]);
								break;
    	    				case "lang":
    	    					SetCommand.executeLang(event, args[2]);
    	    					break;
        					default:
        						ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
        								MessageFormat.format(i18n.getString("tip.usage.two"),
												Command.SETLANG.formatFullCommand(),
												Command.SETTAG.formatFullCommand()));
        				}
        			}
        			else
						ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
								MessageFormat.format(i18n.getString("tip.usage.two"),
										Command.SETLANG.formatFullCommand(),
										Command.SETTAG.formatFullCommand()));
        		}
        		else if (isCommand(args[0], Command.LANG)) // !lang command
        		{
        			LangCommand.execute(event);
        		}
        		else if (isCommand(args[0], Command.PLAYER)) // !player command
        		{
        			if (args.length > 1)
        				PlayerCommand.execute(event, args[1]);
        			else
        				PlayerCommand.execute(event);
        		}
				else if (isCommand(args[0], Command.CLAN)) // !player command
				{
					if (args.length > 1)
						ClanCommand.execute(event, args[1]);
					else
						ClanCommand.execute(event);
				}
        		else if (isCommand(args[0], Command.WAR)) // !war command
        		{
					if (args.length > 2)
						WarCommand.execute(event, args[1], args[2]);
					else if (args.length == 2)
						WarCommand.execute(event, args[1]);
					else
						ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
								MessageFormat.format(i18n.getString("tip.usage"), Command.WAR.formatFullCommand()));
        		}
        		else if (isCommand(args[0], Command.WARLOG)) // !warlog command
        		{
        			if (args.length > 2)
        				WarlogCommand.execute(event, args[1], args[2]);
        			else if (args.length == 2)
        				WarlogCommand.execute(event, args[1]);
        			else
						ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
								MessageFormat.format(i18n.getString("tip.usage"), Command.WARLOG.formatFullCommand()));
        		}
        		else if (isCommand(args[0], Command.WARLEAGUE_ROUND)) // !warleague command
        		{
        			if (args.length > 1)
					{
						switch(args[1])
						{
							case "round":
								if (args.length > 3)
									WarLeagueCommand.executeRound(event, args[2], args[3]);
								else if (args.length == 3)
									WarLeagueCommand.executeRound(event, args[2]);
								else
									ErrorUtils.sendError(channel, i18n.getString("wrong.usage"),
											MessageFormat.format(i18n.getString("tip.usage"), Command.WARLEAGUE_ROUND.formatFullCommand()));
								return;
							case "all":
								if (args.length > 2)
									WarLeagueCommand.executeAll(event, args[2]);
								else
									WarLeagueCommand.executeAll(event);
								return;
							default:
								WarLeagueCommand.executeClan(event, args[1]);
								return;
						}
					}
        			WarLeagueCommand.executeClan(event);
        		}
        		else if (isCommand(args[0], Command.INFO)) // !info command
        		{
        			InfoCommand.execute(event);
        		}
        		else if (isCommand(args[0], Command.HELP)) // !help command
        		{
        			HelpCommand.execute(event);
        		}
        		else if (isCommand(args[0], Command.CLEAR)) // !clear command
        		{
        			ClearCommand.execute(event);
        		}
        		else if (isCommand(args[0], Command.INVITE)) // !clear command
        		{
        			InviteCommand.execute(event);
        		}
        	}
    	}
    }
}
