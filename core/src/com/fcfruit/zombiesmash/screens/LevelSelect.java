package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.level.NightLevel;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.stages.LevelSelectStage;

import static com.fcfruit.zombiesmash.Environment.level;


/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelSelect implements Screen{

    LevelSelectStage stage;

    Viewport viewport;

    public LevelSelect(){
        viewport = new StretchViewport(Environment.game.screenWidth, Environment.game.screenHeight);
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
        // that's what you probably want in case of UI
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
