package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.fcfruit.zombiesmash.GifDecoder;
import com.fcfruit.zombiesmash.levels.Level;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-07-21.
 */

public class GameScreen implements Screen{

    private Zombie regZombie;

    private Stage stage;

    private SpriteBatch spriteBatch;

    public GameScreen(Level level, Game g){

        stage = new Stage(new ExtendViewport(1920, 1080));

        regZombie = new RegularZombie(new Texture(Gdx.files.internal("zombies/DefaultZombie.png")), GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("zombies/DefaultZombie.gif").read()), g);
        regZombie.setPosition(100, 100);
        regZombie.setTouchedAnimation(GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("zombies/DefaultZombie.gif").read()));

        spriteBatch = new SpriteBatch();

        Gdx.app.log("gamescreen", "initialized gamescreen");

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.app.log("gamescreen", "rendering");

        stage.act(delta);
        stage.draw();

        spriteBatch.begin();
        regZombie.draw(spriteBatch);
        spriteBatch.end();

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
