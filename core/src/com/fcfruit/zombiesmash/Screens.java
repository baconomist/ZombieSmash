package com.fcfruit.zombiesmash;

import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.screens.LevelMenu;
import com.fcfruit.zombiesmash.screens.MainMenu;
import com.fcfruit.zombiesmash.screens.SplashScreen;

/**
 * Created by Lucas on 2018-05-01.
 */

public class Screens
{
    public SplashScreen splashscreen;
    public MainMenu mainmenu;
    public GameScreen gamescreen;
    public LevelMenu levelmenu;

    public Screens()
    {
        this.splashscreen = new SplashScreen();
        this.mainmenu = new MainMenu();
        this.gamescreen = new GameScreen();
        this.levelmenu = new LevelMenu();
    }
}
