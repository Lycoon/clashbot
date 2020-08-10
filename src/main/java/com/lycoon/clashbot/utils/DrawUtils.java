package com.lycoon.clashbot.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DrawUtils
{
	public static Graphics2D initGraphics(int width, int height, BufferedImage image)
	{
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, width, height);
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		return g2d;
	}
	
	public static Font getFont(String file)
	{
		Font font = null;
		
		try 
		{
			InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/" + file);
			font = Font.createFont(Font.TRUETYPE_FONT, stream);
		}
		catch (FontFormatException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		
		return font;
	}
	
	public static void drawShadowedString(Graphics2D g2d, Font font, String text, int x, int y)
	{
		g2d.setFont(font);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, x, y + 2);
		g2d.setColor(Color.WHITE);
		g2d.drawString(text, x, y);
	}
	
	public static void drawSimpleString(Graphics2D g2d, Font font, Color color, String text, int x, int y)
	{
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.drawString(text, x, y);
	}
	
	public static void drawCenteredString(Graphics2D g2d, Rectangle rect, Font font, String text)
	{
	    FontMetrics metrics = g2d.getFontMetrics(font);
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    drawShadowedString(g2d, font, text, x, y);
	}
	
	public static void drawCenteredImage(Graphics2D g2d, Image img, Rectangle rect, int sizeX, int sizeY)
	{
		g2d.drawImage(img,
				(int)(rect.getX() + rect.getWidth()/2) - img.getWidth(null)/2,
				(int)(rect.getY() + rect.getHeight()/2) - img.getHeight(null)/2,
				sizeX,
				sizeY,
				null);
	}
}
