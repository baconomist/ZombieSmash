package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.GifDecoder;
import com.fcfruit.zombiesmash.levels.Level;
import com.fcfruit.zombiesmash.zombies.Body;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class GameScreen implements Screen{

    private Zombie regZombie;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    private Stage game_stage;
    private Stage power_ups_stage;

    private ExtendViewport game_view;
    private ExtendViewport power_ups_view;

    private InputMultiplexer inputMultiplexer;

    public GameScreen(Level level, Game g){

        game_view = new ExtendViewport(1920, 1080);

        game_stage = new Stage(game_view);

        power_ups_view = new ExtendViewport(480, 270);
        power_ups_view.setScreenPosition(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight() - 270);

        power_ups_stage = new Stage(power_ups_view);

        regZombie = new RegularZombie(g);
        regZombie.setPosition(300, 300);

        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();

        inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(game_stage);
        inputMultiplexer.addProcessor(power_ups_stage);

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game_stage.act(delta);
        game_stage.draw();

        power_ups_stage.act(delta);
        power_ups_stage.draw();

        regZombie.update(delta);

        spriteBatch.begin();
        skeletonRenderer.draw(spriteBatch, regZombie.body.skeleton);
        spriteBatch.end();

    }

    @Override
    public void resize(int width, int height) {
        power_ups_stage.getViewport().update(width, height);
        game_stage.getViewport().update(width, height);
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
