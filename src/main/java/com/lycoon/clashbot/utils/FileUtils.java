package com.lycoon.clashbot.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FileUtils
{
	public static void sendImage(MessageReceivedEvent event, BufferedImage image, String file, String extension)
	{
		ZonedDateTime date = ZonedDateTime.now(ZoneId.of("UTC"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		String time = formatter.format(date);
		
		// Generating the picture
		String filename = time + file + "." + extension;
		File outputfile = new File("outputs/" + filename);
		try {ImageIO.write(image, extension, outputfile);}
		catch (IOException e) {e.printStackTrace();}
		
		// Callback function
		Consumer<Message> sendingCallback = (res) ->
		{
			System.out.println("SENDING CALLBACK...");
			
			// Bot rate limitation
			long id = event.getAuthor().getIdLong();
			CoreUtils.addUserToGenerating(id);
			System.out.println("ADDED TO GENERATING...");
			
			try {Thread.sleep(CoreUtils.threshold);}
			catch (InterruptedException e1) {e1.printStackTrace();}
			
			CoreUtils.removeUserFromGenerating(id);
			System.out.println("REMOVED FROM GENERATING...");
			
			// Deleting picture from disk
			try {Files.delete(Paths.get(outputfile.getPath()));}
			catch (IOException e) {e.printStackTrace();}
		};
		event.getChannel().sendFile(outputfile).queue(sendingCallback);
		
		System.out.println(filename+ " sent.");
	}
	
	public static Image getImageFromFile(String file)
	{
		Image image = null;
		try
		{
		    image = ImageIO.read(FileUtils.class.getResource("/" + file));
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
