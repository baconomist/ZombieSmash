package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.level.Level;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.stages.GameStage;

/**
 * Created by Lucas on 2017-07-21.
 */

public class GameScreen implements Screen{

    private Stage game_stage;
    private Stage power_ups_stage;

    private StretchViewport physics_view;
    private StretchViewport game_view;
    private StretchViewport power_ups_view;

    private InputMultiplexer inputMultiplexer;

    private Box2DDebugRenderer debugRenderer;

    public GameScreen(){

        physics_view = new StretchViewport(Environment.physicsCamera.viewportWidth, Environment.physicsCamera.viewportHeight, Environment.physicsCamera);
        physics_view.apply();

        game_view = new StretchViewport(Environment.gameCamera.viewportWidth, Environment.gameCamera.viewportHeight, Environment.gameCamera);
        game_view.apply();

        game_stage = new GameStage(game_view);

        power_ups_view = new StretchViewport(480, 270);
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
        // You will not have control of ui on this screen
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {


        physics_view.apply();

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


        //Environment.gameCamera.position.x += 10;
        //Environment.physicsCamera.position.x += 10/192f;

        Environment.physicsCamera.update();
        Environment.gameCamera.update();
        //Environment.physics.constructPhysicsBoundries();

        //Environment.physicsCamera.position.x=10f;
        //Environment.gameCamera.position.x=10*192;





        //debugRenderer.render(Environment.physics.getWorld(), Environment.physicsCamera.combined);

    }

    @Override
    public void resize(int width, int height) {
        // Resize viewport to screen size.
        game_view.update(width, height);
        physics_view.update(width, height);
        power_ups_view.update(width, height);

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
