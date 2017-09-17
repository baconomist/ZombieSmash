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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
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

    static float screenWidth = 1920;
    static float screenHeight = 1080;

    private Level level;

    public Physics physics;

    public OrthographicCamera camera;

    private Stage game_stage;
    private Stage power_ups_stage;

    private ExtendViewport game_view;
    private ExtendViewport power_ups_view;

    private InputMultiplexer inputMultiplexer;

    private Box2DDebugRenderer debugRenderer;

    public GameScreen(Level lvl, Game g){

        level = lvl;

        physics = new Physics();

        camera = new OrthographicCamera(screenWidth/Physics.PPM, screenHeight/Physics.PPM);

        game_view = new ExtendViewport(1920, 1080);
        game_view.apply();

        game_stage = new GameStage(game_view, this);

        power_ups_view = new ExtendViewport(480, 270);
        power_ups_view.setScreenPosition(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight() - 270);

        power_ups_stage = new Stage(power_ups_view);

        inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(game_stage);
        inputMultiplexer.addProcessor(power_ups_stage);

        Gdx.input.setInputProcessor(inputMultiplexer);

        debugRenderer = new Box2DDebugRenderer();

    }

    @Override
    public void show() {

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

        physics.update(delta);

        debugRenderer.render(physics.getWorld(), camera.combined);


    }

    @Override
    public void resize(int width, int height) {
        power_ups_stage.getViewport().update(width, height);

        // Resize viewport to screen size.
        game_view.update(width, height);

        // Resize camera to world size.
        // I don't know if this makes sense
        //camera.viewportWidth = width/Physics.PPM;
        //camera.viewportHeight = height/Physics.PPM;

        // Center camera
        camera.position.x = camera.viewportWidth/2;
        camera.position.y = camera.viewportHeight/2;

        camera.update();

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
