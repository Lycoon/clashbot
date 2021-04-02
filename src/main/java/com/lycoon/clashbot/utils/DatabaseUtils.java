package com.lycoon.clashbot.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils
{
	private static final HikariDataSource ds;
    
    static
    {
		HikariConfig cfg = new HikariConfig("database.properties");
        ds = new HikariDataSource(cfg);
    }
    
    private DatabaseUtils() {}
    
    public static Connection getConnection() throws SQLException 
    {
    	return ds.getConnection();
    }
	
	public static String getUserLang(long id)
	{
		String req = "SELECT lang FROM user WHERE id=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			ResultSet res = statement.executeQuery();
			
			statement.close();
			conn.close();
			
			if (res.next())
				return res.getString("lang");
		}
		catch (SQLException ignored) {}
		return "en";
	}
	
	public static String getPlayerTag(long id)
	{
		String req = "SELECT player FROM user WHERE id=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			ResultSet res = statement.executeQuery();
			
			statement.close();
			conn.close();
			
			if (res.next())
				return res.getString("player");
		}
		catch (SQLException ignored) {}
		return null;
	}
	
	public static String getClanTag(long id)
	{
		String req = "SELECT clan FROM user WHERE id=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			ResultSet res = statement.executeQuery();
			
			statement.close();
			conn.close();
			
			if (res.next())
				return res.getString("clan");
		}
		catch (SQLException ignored) {}
		return null;
	}

	public static String getServerPrefix(long id)
	{
		String req = "SELECT prefix FROM server WHERE id=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			ResultSet res = statement.executeQuery();

			statement.close();
			conn.close();

			if (res.next())
				return res.getString("prefix");
		}
		catch (SQLException ignored) {}
		return "!";
	}
	
	public static void setUserLang(long id, String lang)
	{
		String req = "INSERT INTO user(id, lang) VALUES(?, ?) ON DUPLICATE KEY UPDATE lang=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			statement.setString(2, lang);
			statement.setString(3, lang);
			statement.executeUpdate();
			
			statement.close();
			conn.close();
		}
		catch (SQLException ignored) {}
	}
	
	public static void setPlayerTag(long id, String playerTag)
	{
		String req = "INSERT INTO user(id, player) VALUES(?, ?) ON DUPLICATE KEY UPDATE player=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			statement.setString(2, playerTag);
			statement.setString(3, playerTag);
			statement.executeUpdate();
			
			statement.close();
			conn.close();
		}
		catch (SQLException ignored) {}
	}
	
	public static void setClanTag(long id, String clanTag)
	{
		String req = "INSERT INTO user(id, clan) VALUES(?, ?) ON DUPLICATE KEY UPDATE clan=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			statement.setString(2, clanTag);
			statement.setString(3, clanTag);
			statement.executeUpdate();
			
			statement.close();
			conn.close();
		}
		catch (SQLException ignored) {}
	}

	public static void setServerPrefix(long id, String prefix)
	{
		String req = "INSERT INTO server(id, prefix) VALUES(?, ?) ON DUPLICATE KEY UPDATE prefix=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			statement.setString(2, prefix);
			statement.setString(3, prefix);
			statement.executeUpdate();

			statement.close();
			conn.close();
		}
		catch (SQLException ignored) {}
	}
	
	public static void deleteUser(long id)
	{
		String req = "DELETE FROM user WHERE id=?;";
		try (Connection conn = DatabaseUtils.getConnection();
			 PreparedStatement statement = conn.prepareStatement(req))
		{
			statement.setLong(1, id);
			statement.executeUpdate();
			
			statement.close();
			conn.close();
		}
		catch (SQLException ignored) {}
	}
}
