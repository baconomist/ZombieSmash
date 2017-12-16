package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.stages.MainMenuStage;


import java.util.ArrayList;

import javafx.scene.Scene;

/**
 * Created by Lucas on 2017-07-21.
 */

//compile "com.underwaterapps.overlap2druntime:overlap2d-runtime-libgdx:0.1.0"

public class MainMenu implements Screen{

    MainMenuStage stage;

    Viewport viewport;

    Music music;


    public MainMenu(){
        viewport = new StretchViewport(Environment.game.screenWidth, Environment.game.screenHeight);
        stage = new MainMenuStage(viewport);

        music = Gdx.audio.newMusic(Gdx.files.internal("audio/theme_song.wav"));


        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(!stage.mute) {
            music.play();
        }
        else{
            music.stop();
        }

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
