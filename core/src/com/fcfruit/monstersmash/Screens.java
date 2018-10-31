package com.fcfruit.monstersmash;

import com.fcfruit.monstersmash.screens.GameScreen;
import com.fcfruit.monstersmash.screens.LevelMenu;
import com.fcfruit.monstersmash.screens.LoadingScreen;
import com.fcfruit.monstersmash.screens.MainMenu;
import com.fcfruit.monstersmash.screens.SplashScreen;

/**
 * Created by Lucas on 2018-05-01.
 */

public class Screens
{
    public SplashScreen splashscreen;
    public MainMenu mainmenu;
    public GameScreen gamescreen;
    public LevelMenu levelmenu;
    public LoadingScreen loadingscreen;

    public Screens()
    {
        this.splashscreen = new SplashScreen();
        this.mainmenu = new MainMenu();
        this.gamescreen = new GameScreen();
        this.levelmenu = new LevelMenu();
        this.loadingscreen = new LoadingScreen();
    }
}
