package com.lycoon.clashbot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

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

public class FileUtils {
    public static void sendImage(SlashCommandEvent event, BufferedImage image) {
        // Generating the picture
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Callback function
        long id = event.getMember().getIdLong();
        if (!isOwner(id))
            addUserToGenerating(id);

        Consumer<Message> sendingCallback = (res) ->
        {
            // Bot rate limitation
            try {
                Thread.sleep(CoreUtils.threshold);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            removeUserFromGenerating(id);
        };
        event.getHook().sendFile(bos.toByteArray(), "clashbot.png").queue(sendingCallback);
        LOGGER.info("Picture sent to " + event.getMember().getUser().getAsTag() + " on " + event.getGuild().getIdLong());
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
