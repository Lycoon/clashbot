package com.lycoon.clashbot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static com.lycoon.clashbot.core.ClashBotMain.LOGGER;
import static com.lycoon.clashbot.utils.CoreUtils.addUserToGenerating;
import static com.lycoon.clashbot.utils.CoreUtils.removeUserFromGenerating;
import static com.lycoon.clashbot.utils.CoreUtils.isOwner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

public class FileUtils
{
    public static void sendImage(SlashCommandInteractionEvent event, BufferedImage image)
    {
        // Generating the picture
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try { ImageIO.write(image, "png", bos); }
        catch (IOException e) { LOGGER.error("Error while generating picture: " + e.getMessage()); }

        // Callback function
        long id = event.getMember().getIdLong();
        if (!isOwner(id))
            addUserToGenerating(id);

        Consumer<Message> sendingCallback = (res) ->
        {
            // Bot rate limitation
            try { Thread.sleep(CoreUtils.threshold); }
            catch (InterruptedException e) { LOGGER.error("Error while sending picture: " + e.getMessage()); }

            removeUserFromGenerating(id);
        };
        event.getHook().sendFile(bos.toByteArray(), "clashbot.png").queue(sendingCallback);
        LOGGER.info("Picture sent to " + event.getMember().getUser().getAsTag() + " on " + event.getGuild().getIdLong());
    }

    public static Image getImageFromFile(String file)
    {
        try {
            URL imageUrl = Objects.requireNonNull(FileUtils.class.getResource("/" + file));
            return ImageIO.read(imageUrl);
        }
        catch (IOException e) {
            LOGGER.error("Error while reading file: " + e.getMessage());
        }

        return null;
    }

    public static Image getImageFromUrl(String link)
    {
        try {
            URL url = new URL(link);
            return ImageIO.read(url);
        }
        catch (IOException e) {
            LOGGER.error("Error while reading URL: " + e.getMessage());
        }

        return null;
    }
}
