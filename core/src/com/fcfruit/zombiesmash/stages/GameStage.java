package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage {

    public Viewport viewport;

    private ArrayList<Zombie> zombies = Environment.physics.zombies;

    private ArrayList<Part> parts = Environment.physics.parts;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    public GameStage(Viewport v){
        super(v);

        viewport = v;

        Zombie z;
        for(int i = 0; i < 10; i++) {
            z = new RegularZombie();
            z.id = i;
            Environment.physics.addBody(z);
            z.setPosition(i, 0);
        }
        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();



    }


    @Override
    public void draw() {
        super.draw();

        float delta = Gdx.graphics.getDeltaTime();

        // Viewport.getCamera() != Environment.gameCamera
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        for(Zombie z : this.zombies) {
            z.draw(spriteBatch, skeletonRenderer, delta);
        }

        parts = Environment.physics.parts;

        for(Part p : parts){
            p.draw(spriteBatch);
        }

        spriteBatch.end();

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
