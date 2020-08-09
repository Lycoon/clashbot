package com.lycoon.clashbot.draw;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class FileUtils
{
	public static void sendImage(MessageChannel channel, BufferedImage image, String file)
	{
		// Generating the picture
		String filename = file+ ".png";
		File outputfile = new File("outputs/" + filename);
		try {ImageIO.write(image, "png", outputfile);}
		catch (IOException e) {e.printStackTrace();}
		
		// Sending the picture and then deleting it
		Consumer<Message> deleteFile = (res) ->
		{
			try {Files.delete(Paths.get(outputfile.getPath()));}
			catch (IOException e) {e.printStackTrace();}
		};
		channel.sendFile(outputfile).queue(deleteFile);
		System.out.println("Message sent.");
	}
	
	public static Image getImageFromFile(String file)
	{
		Image image = null;
		try
		{
		    File pathToFile = new File("src/main/resources/" + file);
		    image = ImageIO.read(pathToFile);
		}
		catch (IOException ex) {ex.printStackTrace();}
		return image;
	}
	
	public static Image getImageFromUrl(String link)
	{
		Image image = null;
		try
		{
			URL url = new URL(link);
		    image = ImageIO.read(url);
		} 
		catch (IOException ex) {ex.printStackTrace();}
		return image;
	}
}
