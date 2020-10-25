package com.lycoon.clashbot.utils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedString;
import java.util.Objects;

public class DrawUtils
{
	private final static Color DEFAULT_COLOR = Color.WHITE;
	
	public static Graphics2D initGraphics(int width, int height, BufferedImage image)
	{
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, width, height);
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		return g2d;
	}
	
	public static Font getFont(String file)
	{
		Font font = null;
		
		try 
		{
			InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/" + file);
			font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(stream));
		}
		catch (FontFormatException | IOException e) {e.printStackTrace();}

		return font;
	}
	
	public static void drawShadowedStringLeft(Graphics2D g2d, String text, int x, int y, float size)
	{
		drawShadowedStringLeft(g2d, text, x, y, size, 2, DEFAULT_COLOR);
	}
	
	public static void drawShadowedStringLeft(Graphics2D g2d, String text, int x, int y, float size, int borderSize)
	{
		drawShadowedStringLeft(g2d, text, x, y, size, borderSize, DEFAULT_COLOR);
	}
	
	public static void drawShadowedStringLeft(Graphics2D g2d, String text, int x, int y, float size, int borderSize, Color color)
	{
		Font tmpFont = g2d.getFont();
		
		g2d.setFont(g2d.getFont().deriveFont(size));
		FontRenderContext ctx = g2d.getFontRenderContext();
		double textWith = g2d.getFont().getStringBounds(text, ctx).getWidth();
		
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, x-(int)textWith, y + borderSize);
		g2d.setColor(color);
		g2d.drawString(text, x-(int)textWith, y);
		
		g2d.setFont(tmpFont);
	}
	
	public static void drawShadowedString(Graphics2D g2d, String text, int x, int y, float size)
	{
		drawShadowedString(g2d, text, x, y, size, 2, DEFAULT_COLOR);
	}
	
	public static void drawShadowedString(Graphics2D g2d, String text, int x, int y, float size, int borderSize)
	{
		drawShadowedString(g2d, text, x, y, size, borderSize, DEFAULT_COLOR);
	}
	
	public static void drawShadowedString(Graphics2D g2d, String text, int x, int y, float size, int borderSize, Color color)
	{
		Font tmpFont = g2d.getFont();
		g2d.setFont(g2d.getFont().deriveFont(size));
		
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, x, y + borderSize);
		g2d.setColor(color);
		g2d.drawString(text, x, y);
		
		g2d.setFont(tmpFont);
	}

	public static void drawShadowedStringLeft(Graphics2D g2d, String text, int x, int y, float size, Color color)
	{
		Font tmpFont = g2d.getFont();

		g2d.setFont(g2d.getFont().deriveFont(size));
		FontRenderContext ctx = g2d.getFontRenderContext();
		double textWith = g2d.getFont().getStringBounds(text, ctx).getWidth();

		g2d.setColor(Color.BLACK);
		g2d.drawString(text, x-(int)textWith, y + 2);
		g2d.setColor(color);
		g2d.drawString(text, x-(int)textWith, y);

		g2d.setFont(tmpFont);
	}
	
	public static void drawSimpleStringLeft(Graphics2D g2d, String text, int x, int y, float size)
	{
		drawSimpleStringLeft(g2d, text, x, y, size, DEFAULT_COLOR);
	}
	
	public static void drawSimpleStringLeft(Graphics2D g2d, String text, int x, int y, float size, Color color)
	{
		Font tmpFont = g2d.getFont();
		
		g2d.setFont(g2d.getFont().deriveFont(size));
		FontRenderContext ctx = g2d.getFontRenderContext();
		double textWith = g2d.getFont().getStringBounds(text, ctx).getWidth();
		
		g2d.setColor(color);
		g2d.drawString(text, x-(int)textWith, y);
		
		g2d.setFont(tmpFont);
	}
	
	public static void drawSimpleString(Graphics2D g2d, String text, int x, int y, float size, Color color)
	{
		Font tmpFont = g2d.getFont();
		g2d.setFont(g2d.getFont().deriveFont(size));
		
		g2d.setColor(color);
		g2d.drawString(text, x, y);
		
		g2d.setFont(tmpFont);
	}
	
	public static void drawSimpleCenteredString(Graphics2D g2d, String text, Rectangle rect, float size, Color color)
	{
		Font tmpFont = g2d.getFont();
		g2d.setFont(g2d.getFont().deriveFont(size));
		
	    FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    drawSimpleString(g2d, text, x, y, g2d.getFont().getSize(), color);
	    
	    g2d.setFont(tmpFont);
	}
	
	public static void drawCenteredString(Graphics2D g2d, Rectangle rect, Font font, String text)
	{
		drawCenteredString(g2d, rect, font, text, DEFAULT_COLOR);
	}

	public static void drawCenteredString(Graphics2D g2d, Rectangle rect, Font font, String text, Color color)
	{
		FontMetrics metrics = g2d.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		drawShadowedString(g2d, text, x, y, font.getSize(), 2, color);
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
