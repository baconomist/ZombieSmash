package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.fcfruit.zombiesmash.levels.Level;

/**
 * Created by Lucas on 2017-07-21.
 */

public class MainMenu implements Screen{

    private MainMenu self = this;

    private TextButton playButton;

    private TextButton shopButton;

    private TextButton settingsButton;

    public Game game;

    private Stage stage;

    public MainMenu(Game g){
        game = g;
        stage = new Stage(new ExtendViewport(1920, 1080));

        Gdx.input.setInputProcessor(stage);

        Skin defaultSkin = new Skin(Gdx.files.internal("gui/defaultSkin/uiskin.json"));

        playButton = new TextButton("Play", defaultSkin);

        playButton.setSize(200, 200);
        playButton.setPosition(960 - playButton.getWidth()/2, 720);
        playButton.getLabel().setFontScale(10, 10); //change text size


        shopButton = new TextButton("Shop", defaultSkin);

        shopButton.setSize(200, 200);
        shopButton.setPosition(960 - shopButton.getWidth()/2, playButton.getY() - shopButton.getHeight()*1.5f);
        shopButton.getLabel().setFontScale(10, 10); //change text size


        settingsButton = new TextButton("Settings", defaultSkin);

        settingsButton.setSize(200, 200);
        settingsButton.setPosition(960 - settingsButton.getWidth()/2, shopButton.getY() - settingsButton.getHeight()*1.5f);
        settingsButton.getLabel().setFontScale(10, 10); //change text size


        stage.addActor(playButton);
        stage.addActor(shopButton);
        stage.addActor(settingsButton);


        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.clear();// clears stage/screen
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                game.setScreen(new LevelSelect(self));
            }
        });

        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.clear();// clears stage/screen
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                game.setScreen(new Shop(self));
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.clear();// clears stage/screen
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                game.setScreen(new Settings(self));
            }
        });



    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // use true here to center the camera
        // that's what you probably want in case of UI
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
