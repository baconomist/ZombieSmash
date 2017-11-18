package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.level.Level;
import com.fcfruit.zombiesmash.zombies.GirlZombie;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.PoliceZombie;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage {

    Level level;

    public Viewport viewport;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    public GameStage(Viewport v, Level lvl){
        super(v);

        this.level = lvl;
        viewport = v;

        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();

    }


    @Override
    public void draw() {
        super.draw();

        // Viewport.getCamera() != Environment.gameCamera
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        level.draw(spriteBatch, skeletonRenderer);
        spriteBatch.end();

        level.update();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 vector = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchDown(vector.x, vector.y, pointer);
        Gdx.app.log("touch", ""+Environment.gameCamera.unproject(new Vector3(0, Gdx.input.getY(), 0)).y);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 vector = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchDragged(vector.x, vector.y, pointer);
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 vector = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchUp(vector.x, vector.y, pointer);
        return super.touchUp(screenX, screenY, pointer, button);
    }
}
