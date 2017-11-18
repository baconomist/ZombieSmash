package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Level {

    public int lvlNum;

    Sprite sprite;

    Objective objective;

    private ArrayList<Zombie> zombies = Environment.physics.getZombies();

    private ArrayList<Part> parts = Environment.physics.getParts();


    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer){
        sprite.draw(batch);

        for(Zombie z : this.zombies) {
            z.draw(batch, skeletonRenderer, Gdx.graphics.getDeltaTime());
        }

        for(Part p : this.parts){
            p.draw(batch);
        }

    }

    public void update(){
        // Can't be put in draw bcuz it takes too long
        // When it takes too long in a spritebatch call,
        // it doesn't draw the sprites, only the light
        Environment.physics.update(Gdx.graphics.getDeltaTime());
    }

}
