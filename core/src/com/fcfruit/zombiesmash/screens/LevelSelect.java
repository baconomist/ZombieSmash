package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.levels.Level;

/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelSelect implements Screen{

    public LevelSelect(MainMenu mainMenu){

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Environment.game.setScreen(Environment.gameScreen);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
