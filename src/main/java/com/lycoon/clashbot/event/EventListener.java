package com.lycoon.clashbot.event;

import com.lycoon.clashbot.commands.*;
import com.lycoon.clashbot.core.ClashBotMain;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter
{
    static boolean isCommand(String arg, Command cmd)
    {
        return arg.equalsIgnoreCase(cmd.formatCommand());
    }

    static boolean isAdminCommand(String arg)
    {
        return arg.equalsIgnoreCase(AdminCommand.ADMIN.formatCommand());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String message = event.getMessage().getContentDisplay();
        if (!message.startsWith(Command.PREFIX))
            return;

        ClashBotMain.LOGGER.info(event.getAuthor().getAsTag() + " issued: " + message);
        String[] args = message.split(" ");

        if (isCommand(args[0], Command.SETLANG)) // !set
            SetCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.LANG)) // !lang
            LangCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.PLAYER)) // !player
            PlayerCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.CLAN)) // !player
            ClanCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.WAR)) // !war
            WarCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.WARLOG)) // !warlog
            WarlogCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.WARLEAGUE_ROUND)) // !warleague
            WarLeagueCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.INFO)) // !info
            InfoCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.HELP)) // !help
            HelpCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.CLEAR)) // !clear
            ClearCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.INVITE)) // !invite
            InviteCommand.dispatch(event, args);
        else if (isCommand(args[0], Command.STATS)) // !stats
            StatsCommand.dispatch(event, args);
        else if (isAdminCommand(args[0])) // !admin
            ServersCommand.dispatch(event, args);
    }
}
