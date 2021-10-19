package com.lycoon.clashbot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.lycoon.clashbot.core.ClashBotMain.LOGGER;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

public class FileUtils {
    public static void sendImage(MessageReceivedEvent event, BufferedImage image) {
        // Generating the picture
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Callback function
        long id = event.getAuthor().getIdLong();
        if (!CoreUtils.isOwner(id))
            CoreUtils.addUserToGenerating(id);

        Consumer<Message> sendingCallback = (res) ->
        {
            // Bot rate limitation
            try {
                Thread.sleep(CoreUtils.threshold);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CoreUtils.removeUserFromGenerating(id);
        };
        event.getChannel().sendFile(bos.toByteArray(), "clashbot_generated.jpg").queue(sendingCallback);
        LOGGER.info("Picture sent to " + event.getAuthor().getAsTag() + " on " + event.getGuild().getIdLong());
    }

    public static Image getImageFromFile(String file) {
        Image image = null;
        try {
            URL imageUrl = Objects.requireNonNull(FileUtils.class.getResource("/" + file));
            image = ImageIO.read(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static Image getImageFromUrl(String link) {
        Image image = null;
        try {
            URL url = new URL(link);
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
