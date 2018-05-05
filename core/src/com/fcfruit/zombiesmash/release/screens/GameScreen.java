package com.fcfruit.zombiesmash.release.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.zombiesmash.release.Environment;
import com.fcfruit.zombiesmash.release.physics.Physics;
import com.fcfruit.zombiesmash.release.stages.GameStage;
import com.fcfruit.zombiesmash.release.stages.GameUIStage;

/**
 * Created by Lucas on 2017-07-21.
 */

public class GameScreen implements Screen{

    private Stage game_stage;
    private GameUIStage ui_stage;

    private StretchViewport physics_view;
    private StretchViewport game_view;
    private StretchViewport ui_view;

    private InputMultiplexer inputMultiplexer;

    private Box2DDebugRenderer debugRenderer;



    public GameScreen(){

    }

    public void create()
    {
        physics_view = new StretchViewport(Environment.physicsCamera.viewportWidth, Environment.physicsCamera.viewportHeight, Environment.physicsCamera);
        physics_view.apply();

        game_view = new StretchViewport(Environment.gameCamera.viewportWidth, Environment.gameCamera.viewportHeight, Environment.gameCamera);
        game_view.apply();

        game_stage = new GameStage(game_view);

        ui_view = new StretchViewport(Environment.gameCamera.viewportWidth, Environment.gameCamera.viewportHeight);
        ui_view.apply();
        ui_stage = new GameUIStage(ui_view);

        inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(ui_stage);
        inputMultiplexer.addProcessor(game_stage);


        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        // Have to reset input processor bc
        // Game stage created @ begining of game,
        // So game menu sets input processor after
        // GameScreen if you don't put this here
        // You will not have control of screens on this screen
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

        ui_stage.act(delta);
        // viewport.apply() needs to be called if you have multiple viewports before drawing, see docs.
        // Sets gl viewport
        ui_view.apply();
        ui_stage.draw();


        //Environment.gameCamera.position.x += 10;
        //Environment.physicsCamera.position.x += 10/192f;

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {

            Environment.gameCamera.position.x -= Physics.PIXELS_PER_METER/10;
            Environment.physicsCamera.position.x -= 0.1f;
            Environment.gameCamera.update();
            Environment.physicsCamera.update();
            Environment.physics.constructPhysicsBoundaries();

            Environment.physicsCamera.update();
            Environment.gameCamera.update();
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {

            Environment.gameCamera.position.x += Physics.PIXELS_PER_METER/10;
            Environment.physicsCamera.position.x += 0.1f;
            Environment.gameCamera.update();
            Environment.physicsCamera.update();
            Environment.physics.constructPhysicsBoundaries();

            Environment.physicsCamera.update();
            Environment.gameCamera.update();
        }


        //Environment.physics.constructPhysicsBoundaries();

        //Environment.physicsCamera.position.x=10f;
        //Environment.gameCamera.position.x=10*192;



        //
        //debugRenderer.render(Environment.physics.getWorld(), Environment.physicsCamera.combined);

    }

    public void set_ui_stage(GameUIStage s){
        ui_stage = s;
    }

    public GameUIStage get_ui_stage(){return this.ui_stage;}

    @Override
    public void resize(int width, int height) {
        // Resize viewport to screen size.
        game_view.update(width, height);
        physics_view.update(width, height);
        ui_view.update(width, height);

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
