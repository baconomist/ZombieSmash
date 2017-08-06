package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage {

    public Viewport viewport;

    public GameScreen gameScreen;


    private Zombie regZombie;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    private float delta;

    public GameStage(Viewport v, GameScreen gmscrn){
        viewport = v;

        gameScreen = gmscrn;


        regZombie = new RegularZombie();
        regZombie.setPosition(300, 300);

        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();

    }

    @Override
    public void draw() {
        super.draw();

        delta = Gdx.graphics.getDeltaTime();

        regZombie.update(delta);

        spriteBatch.begin();
        skeletonRenderer.draw(spriteBatch, regZombie.body.skeleton);
        spriteBatch.end();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
