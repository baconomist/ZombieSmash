package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.stages.LevelSelectStage;


/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelSelect implements Screen{

    LevelSelectStage stage;

    Viewport viewport;

    public LevelSelect(){
        viewport = new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        stage = new LevelSelectStage(viewport);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta){
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // use true here to center the camera
        // that's what you probably want in case of Screens
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
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
