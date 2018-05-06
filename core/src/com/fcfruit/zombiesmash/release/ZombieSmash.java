package com.fcfruit.zombiesmash.release;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.fcfruit.zombiesmash.Flags;


public class ZombieSmash extends Game
{

    public static float WIDTH = 1920;
    public static float HEIGHT = 1080;

    FPSLogger logger = new FPSLogger();

    public ZombieSmash()
    {
        //don't put anything here, may not work because game is not yet initialized
        super();

    }

    @Override
    public void create()
    {

        if(Flags.DEBUG)
        {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        else
        {
            Gdx.app.setLogLevel(Application.LOG_ERROR);
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android)
        {
            Gdx.input.setCatchBackKey(true);
        }

        Environment.create();

        this.setScreen(Environment.screens.mainmenu);
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
        Environment.onResize();
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
        // Update assetManager
        Environment.assets.update();

        // Don't put anything in here, use screens
        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        logger.log();

        super.render();
    }
}
