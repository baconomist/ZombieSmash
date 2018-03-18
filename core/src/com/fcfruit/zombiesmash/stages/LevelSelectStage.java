package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.level.NightLevel;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.ui.CustomImageButton;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-12-16.
 */

public class LevelSelectStage extends Stage {

    ArrayList<CustomImageButton> buttons =new ArrayList<CustomImageButton>();

    SpriteBatch spriteBatch = new SpriteBatch();

    public LevelSelectStage(Viewport v){
        super(v);

        CustomImageButton customImageButton;
        int row = 0;
        int column = 0;
        for(int i = 0; i < Gdx.files.internal("maps/night_map/levels").list().length; i++){
            if(column > 9) {
                row += 1;
                column = 0;
            }
            customImageButton = new CustomImageButton(new Texture(Gdx.files.internal("gui/level_select/level_box.png")), ""+(i+1));
            customImageButton.setUserData(i+1);
            customImageButton.setPosition(column * customImageButton.getWidth(), (getHeight() - customImageButton.getHeight()) - (customImageButton.getHeight() * row));
            column += 1;
            buttons.add(customImageButton);
        }



    }

    @Override
    public void draw() {
        spriteBatch.setProjectionMatrix(getCamera().combined);
        spriteBatch.begin();
        for(CustomImageButton c : buttons){
            c.draw(spriteBatch);
        }
        spriteBatch.end();
        super.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 pos = getCamera().unproject(new Vector3(screenX, screenY, 0));
        for(CustomImageButton c : buttons){
            if(c.getBoundingRectangle().contains(pos.x, pos.y)){
                c.touchDown();
                onLevelSelect((Integer)c.getUserData());
                break;
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }


    void onLevelSelect(int level){
        Environment.gameCamera = new OrthographicCamera(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        Environment.gameCamera.position.set(Environment.gameCamera.viewportWidth/2, Environment.gameCamera.viewportHeight/2, 0);
        Environment.gameCamera.update();

        Environment.physicsCamera = new OrthographicCamera(Physics.WIDTH, Physics.HEIGHT);
        // Camera position/origin is in the middle
        // Not bottom left
        // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
        // Also cam.project(worldpos) is x and y from bottom left corner
        // But cam.unproject(screenpos) is x and y from top left corner
        Environment.physicsCamera.position.set(Environment.physicsCamera.viewportWidth/2, Environment.physicsCamera.viewportHeight/2, 0);
        Environment.physicsCamera.update();

        Environment.physics = new Physics();

        Environment.level = new NightLevel(level);
        Environment.level.create();

        Environment.gameScreen = new GameScreen();

        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
        Environment.gameCamera.update();
        Environment.physics.constructPhysicsBoundaries();

        Environment.game.setScreen(Environment.gameScreen);
    }

}
