package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.zombiesmash.Physics;
import com.fcfruit.zombiesmash.levels.Level;
import com.fcfruit.zombiesmash.stages.GameStage;

/**
 * Created by Lucas on 2017-07-21.
 */

public class GameScreen implements Screen{

    private Level level;

    private Physics physics;

    public OrthographicCamera camera;

    private Stage game_stage;
    private Stage power_ups_stage;

    private ExtendViewport game_view;
    private ExtendViewport power_ups_view;

    private InputMultiplexer inputMultiplexer;

    public GameScreen(Level lvl, Game g){

        level = lvl;

        physics = new Physics(this);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        game_view = new ExtendViewport(1920, 1080, camera);

        game_stage = new GameStage(game_view, this);

        power_ups_view = new ExtendViewport(480, 270);
        power_ups_view.setScreenPosition(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight() - 270);

        power_ups_stage = new Stage(power_ups_view);

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
        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game_stage.act(delta);
        game_view.apply();// Needs to be called if you have multiple viewports, see docs. Before drawing.
        game_stage.draw();

        power_ups_stage.act(delta);
        power_ups_view.apply();// Needs to be called if you have multiple viewports, see docs. Before drawing.
        power_ups_stage.draw();

        camera.update();

    }

    @Override
    public void resize(int width, int height) {
        power_ups_stage.getViewport().update(width, height);
        game_stage.getViewport().update(width, height);

        camera.viewportWidth = width;
        camera.viewportHeight = height;

        camera.position.x = Gdx.graphics.getWidth()/2;
        camera.position.y = Gdx.graphics.getHeight()/2;

        Gdx.app.log("viewport", "x"+game_view.getWorldWidth());

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
