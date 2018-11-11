package com.fcfruit.monstersmash;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;


public class MonsterSmash extends Game
{

    public static float WIDTH = 3840;
    public static float HEIGHT = 2160;

    FPSLogger logger = new FPSLogger();

    public MonsterSmash()
    {
        //don't put anything here, may not work because game is not yet initialized
        super();
    }

    @Override
    public void create()
    {

        if(Config.DEBUG)
        {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        else
        {
            Gdx.app.setLogLevel(Application.LOG_NONE);
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android)
        {
            Gdx.input.setCatchBackKey(true);
        }


        Environment.create();
        Environment.setupGameSandbox();
        this.setScreen(Environment.screens.gamescreen);

        //Environment.setupMainMenuGame(5);
        //Environment.game.setScreen(Environment.screens.splashscreen);

        //Environment.game.setScreen(Environment.screens.mainmenu);
        //Environment.screens.mainmenu.showInAppPurchasesStage();

    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void pause()
    {
        super.pause();
    }

    @Override
    public void resume()
    {
        super.resume();
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);
        com.fcfruit.monstersmash.Environment.onResize();
    }

    @Override
    public void setScreen(Screen screen)
    {
        super.setScreen(screen);
    }

    @Override
    public Screen getScreen()
    {
        return super.getScreen();
    }

    @Override
    public void render()
    {
        Environment.update();

        // Don't put anything in here, use screens
        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        logger.log();

        super.render();
    }
}
