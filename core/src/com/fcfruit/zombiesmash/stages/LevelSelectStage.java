package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.level.NightLevel;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-12-16.
 */

public class LevelSelectStage extends Stage {

    Table table;

    public LevelSelectStage(Viewport v){
        super(v);

        table = new Table();
        table.setWidth(getWidth());
        table.align(Align.left | Align.top);
        table.setPosition(0, getHeight());

        Skin skin = new Skin(Gdx.files.internal("gui/defaultSkin/uiskin.json"));

        TextButton button;
        for(int i = 0; i < 2; i++){
            button = new TextButton(""+(i+1), skin);
            button.setUserObject(i + 1);
            button.addListener(new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    onLevelSelect((Integer) ((TextButton)event.getListenerActor()).getUserObject());
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            table.add(button).width(getWidth()/2).height(getHeight());
        }

        addActor(table);

    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
        Environment.gameCamera = new OrthographicCamera(Environment.game.screenWidth, Environment.game.screenHeight);
        Environment.gameCamera.position.set(Environment.gameCamera.viewportWidth/2, Environment.gameCamera.viewportHeight/2, 0);
        Environment.gameCamera.update();

        Environment.physicsCamera = new OrthographicCamera(Environment.game.screenWidth/ Physics.PIXELS_PER_METER, Environment.game.screenHeight/Physics.PIXELS_PER_METER);
        // Camera position/origin is in the middle
        // Not bottom left
        // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
        // Also cam.project(worldpos) is x and y from bottom left corner
        // But cam.unproject(screenpos) is x and y from top left corner
        Environment.physicsCamera.position.set(Environment.physicsCamera.viewportWidth/2, Environment.physicsCamera.viewportHeight/2, 0);
        Environment.physicsCamera.update();

        Environment.physics = new Physics();

        Environment.level = new NightLevel(level);

        Environment.gameScreen = new GameScreen();
        Environment.level.updateCamera();
        Environment.game.setScreen(Environment.gameScreen);
    }

}
