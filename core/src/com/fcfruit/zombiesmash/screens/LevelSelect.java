package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.fcfruit.zombiesmash.levels.Level;

/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelSelect implements Screen{

    private Game game;

    public LevelSelect(MainMenu mainMenu){
        game = mainMenu.game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.setScreen(new GameScreen(new Level(null), game));
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
