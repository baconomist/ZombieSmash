package com.fcfruit.zombiesmash.dev.tests.ui_test;

import com.fcfruit.zombiesmash.release.GameData;
import com.fcfruit.zombiesmash.release.Settings;

/**
 * Created by Lucas on 2017-09-18.
 */

public class Environment extends com.fcfruit.zombiesmash.release.Environment
{
    public static ZombieSmash game = new ZombieSmash();

    public static Screens screens;

    public static void create()
    {
        Environment.gameData = new GameData();
        Environment.settings = new Settings();

        Environment.screens = new Screens();
    }

    public static void load()
    {
        // Load all assets
        Environment.assets.finishLoading();
    }

}

