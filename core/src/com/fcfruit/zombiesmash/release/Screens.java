package com.fcfruit.zombiesmash.release;

import com.fcfruit.zombiesmash.release.screens.GameScreen;
import com.fcfruit.zombiesmash.release.screens.MainMenu;

/**
 * Created by Lucas on 2018-05-01.
 */

public class Screens
{
    public MainMenu mainmenu;
    public GameScreen gamescreen;

    public Screens()
    {
        this.mainmenu = new MainMenu();
        this.gamescreen = new GameScreen();
    }
}
