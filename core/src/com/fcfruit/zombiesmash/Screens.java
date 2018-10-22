package com.fcfruit.zombiesmash;

import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.screens.LevelMenu;
import com.fcfruit.zombiesmash.screens.MainMenu;

/**
 * Created by Lucas on 2018-05-01.
 */

public class Screens
{
    public MainMenu mainmenu;
    public GameScreen gamescreen;
    public LevelMenu levelmenu;

    public Screens()
    {
        this.mainmenu = new MainMenu();
        this.gamescreen = new GameScreen();
        this.levelmenu = new LevelMenu();
    }
}
