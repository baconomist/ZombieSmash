package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.stages.SettingsStage;

/**
 * Created by Lucas on 2017-07-21.
 */

//compile "com.underwaterapps.overlap2druntime:overlap2d-runtime-libgdx:0.1.0"

public class MainMenu implements Screen{

    com.fcfruit.zombiesmash.stages.MainMenuStage stage;

    Viewport viewport;

    Music music;

    public boolean show_settings_stage = false;

    Stage settings;


    public MainMenu(){
        viewport = new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        stage = new com.fcfruit.zombiesmash.stages.MainMenuStage(viewport, this);

        music = Gdx.audio.newMusic(Gdx.files.internal("audio/theme_song.wav"));

        settings = new SettingsStage(new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT), this);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(!stage.mute) {
            //music.play();
        }
        else{
            music.stop();
        }

        Gdx.input.setInputProcessor(stage);
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        if(show_settings_stage){
            Gdx.input.setInputProcessor(settings);
            settings.getViewport().apply();
            settings.act();
            settings.draw();
        }


    }

    @Override
    public void resize(int width, int height) {
        // use true here to center the camera
        // that's what you probably want in case of Screens
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        settings.getViewport().update(width, height, true);
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
