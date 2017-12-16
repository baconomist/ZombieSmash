package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.level.NightLevel;
import com.fcfruit.zombiesmash.physics.Physics;


/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelSelect implements Screen{

    public LevelSelect(){

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta){

        Environment.gameCamera = new OrthographicCamera(Environment.game.screenWidth, Environment.game.screenHeight);
        Environment.gameCamera.position.set(Environment.gameCamera.viewportWidth/2, Environment.gameCamera.viewportHeight/2, 0);
        Environment.gameCamera.update();

        Environment.physicsCamera = new OrthographicCamera(Environment.game.screenWidth/Physics.PIXELS_PER_METER, Environment.game.screenHeight/Physics.PIXELS_PER_METER);
        // Camera position/origin is in the middle
        // Not bottom left
        // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
        // Also cam.project(worldpos) is x and y from bottom left corner
        // But cam.unproject(screenpos) is x and y from top left corner
        Environment.physicsCamera.position.set(Environment.physicsCamera.viewportWidth/2, Environment.physicsCamera.viewportHeight/2, 0);
        Environment.physicsCamera.update();

        Environment.physics = new Physics();

        Environment.level = new NightLevel(1);
        
        Environment.gameScreen = new GameScreen();
        Environment.game.setScreen(Environment.gameScreen);
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
