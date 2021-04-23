package com.lycoon.clashbot.core;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.lycoon.clashbot.utils.FileUtils;

public class CacheComponents {
    public static Image alreadyStar = FileUtils.getImageFromFile("icons/clanwar/alreadyclan-star.png");
    public static Image newStar = FileUtils.getImageFromFile("icons/clanwar/clan-star.png");
    public static Image noStar = FileUtils.getImageFromFile("icons/clanwar/noclan-star.png");
    public static Image memberLight = FileUtils.getImageFromFile("backgrounds/clanwar/member-light.png");
    public static Image memberDark = FileUtils.getImageFromFile("backgrounds/clanwar/member-dark.png");
    public static Image warlogCWL = FileUtils.getImageFromFile("backgrounds/warlog/cwl-panel-full.png");
    public static Image warlogWon = FileUtils.getImageFromFile("backgrounds/warlog/win-panel-full.png");
    public static Image warlogLost = FileUtils.getImageFromFile("backgrounds/warlog/lose-panel-full.png");
    public static Image warlogTie = FileUtils.getImageFromFile("backgrounds/warlog/tie-panel-full.png");

    public static List<Image> townhalls = new ArrayList<>();
    public static List<Image> builderhalls = new ArrayList<>();

    private static final CacheComponents singleton = new CacheComponents();

    private CacheComponents() {
        for (int i = 0; i < 14; i++)
            townhalls.add(FileUtils.getImageFromFile("buildings/townhalls/home/th" + (i + 1) + ".png"));
        for (int i = 0; i < 9; i++)
            builderhalls.add(FileUtils.getImageFromFile("buildings/townhalls/builder/bh" + (i + 1) + ".png"));
    }

    public static CacheComponents getInstance() {
        return singleton;
    }

    public static Image getTownHallImage(int level) {
        return townhalls.get(level - 1);
    }

    public static Image getBuilderHallImage(int level) {
        return builderhalls.get(level - 1);
    }
}
