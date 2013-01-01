package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.power_ups.PowerUp;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Level {

    public int lvlNum;

    JsonReader json;
    JsonValue data;

    Sprite sprite;

    public Objective objective;

    public ArrayList<Zombie> zombies = Environment.physics.getZombies();

    public ArrayList<PowerUp> powerUps = Environment.physics.getPowerUps();

    public ArrayList<Part> parts = Environment.physics.getParts();

    private float level_timer = 0;

    private float spawn_timer = 0;


    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer){
        sprite.draw(batch);

        for(Zombie z : this.zombies) {
            z.draw(batch, skeletonRenderer, Gdx.graphics.getDeltaTime());
        }

        for(Part p : this.parts){
            p.draw(batch);
        }

        for(PowerUp pow : this.powerUps){
            pow.draw(batch);
        }

    }

    public void update(){
        // Can't be put in draw bcuz it takes too long
        // When it takes too long in a spritebatch call,
        // it doesn't draw the sprites, only the light
        //Environment.physics.update(Gdx.graphics.getDeltaTime());

    }

}




























