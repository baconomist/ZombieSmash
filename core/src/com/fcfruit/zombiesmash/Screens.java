package com.fcfruit.zombiesmash;

import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.screens.LevelSelect;
import com.fcfruit.zombiesmash.screens.MainMenu;

/**
 * Created by Lucas on 2018-05-01.
 */

public class Screens
{
    public MainMenu mainmenu;
    public GameScreen gamescreen;
    public LevelSelect levelselect;

    public Screens()
    {
        this.mainmenu = new MainMenu();
        this.gamescreen = new GameScreen();
        this.levelselect = new LevelSelect();
    }
}
