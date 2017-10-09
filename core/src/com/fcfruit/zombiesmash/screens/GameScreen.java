package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.Physics;
import com.fcfruit.zombiesmash.levels.Level;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;
import com.fcfruit.zombiesmash.stages.GameStage;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-21.
 */

public class GameScreen implements Screen{

    private Level level;

    public OrthographicCamera camera;

    private Stage game_stage;
    private Stage power_ups_stage;

    private ExtendViewport game_view;
    private ExtendViewport power_ups_view;

    private InputMultiplexer inputMultiplexer;

    private Box2DDebugRenderer debugRenderer;

    public GameScreen(){

        camera = Environment.gameCamera;

        game_view = new ExtendViewport(1920, 1080);
        game_view.apply();

        Vector3 pos = camera.project(new Vector3(0, 0, 0));
        Gdx.app.log("posx", ""+pos.x);
        Gdx.app.log("posy", ""+pos.y);

        pos = camera.unproject(new Vector3(192, 192, 0));
        Gdx.app.log("sposx", ""+pos.x);
        Gdx.app.log("sposy", ""+pos.y);

        game_stage = new GameStage(game_view);

        power_ups_view = new ExtendViewport(480, 270);
        power_ups_view.setScreenPosition(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight() - 270);

        power_ups_stage = new Stage(power_ups_view);

        inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(game_stage);
        inputMultiplexer.addProcessor(power_ups_stage);

        debugRenderer = new Box2DDebugRenderer();

    }

    @Override
    public void show() {
        // Have to reset input processor bc
        // Game stage created @ begining of game,
        // So game menu sets input processor after
        // GameScreen if you don't put this here
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game_stage.act(delta);
        // viewport.apply() needs to be called if you have multiple viewports before drawing, see docs.
        // Sets gl viewport
        game_view.apply();
        game_stage.draw();

        power_ups_stage.act(delta);
        // viewport.apply() needs to be called if you have multiple viewports before drawing, see docs.
        // Sets gl viewport
        power_ups_view.apply();
        power_ups_stage.draw();

        camera.update();

        Environment.physics.update(delta);

        debugRenderer.render(Environment.physics.getWorld(), camera.combined);

    }

    @Override
    public void resize(int width, int height) {
        power_ups_stage.getViewport().update(width, height);

        // Resize viewport to screen size.
        game_view.update(width, height);

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

    public void setLevel(Level lvl){
        level = lvl;
    }

}
