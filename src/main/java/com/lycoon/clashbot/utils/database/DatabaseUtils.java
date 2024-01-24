package com.lycoon.clashbot.utils.database;

import com.lycoon.clashbot.utils.database.entities.User;
import java.util.Optional;

public class DatabaseUtils extends DatabaseHandler
{
	public static String getPlayerTag(long id)
	{
		Optional<User> user = getUser(id);
		return user.map(User::getPlayerTag).orElse(null);
	}

	public static String getClanTag(long id)
	{
		Optional<User> user = getUser(id);
		return user.map(User::getClanTag).orElse(null);
	}

	public static String getUserLang(long id)
	{
		Optional<User> user = getUser(id);
		return user.map(User::getLang).orElse(null);
	}

	public static void setUserLang(long id, String lang)
	{
		User user = new User(id).withLang(lang);
		saveUser(user);
	}

	public static void setPlayerTag(long id, String playerTag)
	{
		User user = new User(id).withPlayerTag(playerTag);
		saveUser(user);
	}

	public static void setClanTag(long id, String clanTag)
	{
		User user = new User(id).withClanTag(clanTag);
		saveUser(user);
	}

	public static void deleteUser(long id)
	{
		removeUser(id);
	}
}
