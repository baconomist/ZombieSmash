package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.Star;
import com.fcfruit.zombiesmash.power_ups.PowerUp;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Level {

    public static HashMap<String, Vector2> positions = new HashMap<String, Vector2>();
    static{
        positions.put("left", new Vector2(Environment.physicsCamera.viewportWidth/2, 0));
        positions.put("right", new Vector2(Environment.physicsCamera.viewportWidth*1.4f, 0));
        positions.put("middle", new Vector2(10, 0));
    }

    public int lvlNum;

    JsonReader json;
    JsonValue data;

    Sprite sprite;

    public Objective objective;

    public ArrayList<Zombie> zombies = Environment.physics.getZombies();

    public ArrayList<PowerUp> powerUps = Environment.physics.getPowerUps();

    public ArrayList<Part> parts = Environment.physics.getParts();

    public ArrayList<Star> stars = Environment.physics.getStars();

    public int starsTouched = 0;

    public boolean levelEnd = false;

    ArrayList<Spawner> spawners;

    private float level_timer = 0;

    private float spawn_timer = 0;

    int currentPosition;

    public String currentCameraPosition;

    boolean zombiesDead = false;

    boolean movingCamera = false;


    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer){
        sprite.draw(batch);

        for(Part p : this.parts){
            p.draw(batch);
        }

        for(Zombie z : this.zombies) {
            z.draw(batch, skeletonRenderer, Gdx.graphics.getDeltaTime());
        }

        for(PowerUp pow : this.powerUps){
            pow.draw(batch);
        }

        for(Star s : this.stars){
            s.draw(batch);
        }

    }

    public void update(){
        // Can't be put in draw bcuz it takes too long
        // When it takes too long in a spritebatch call,
        // it doesn't draw the sprites, only the light
        //Environment.physics.update(Gdx.graphics.getDeltaTime());


        for(Zombie z : zombies){
            if(z.alive){
                zombiesDead = false;
                break;
            }
            else{
                zombiesDead = true;
            }

        }

    }

    public void updateCamera(){
        Environment.gameCamera.position.x = positions.get(data.get(currentPosition).name).x*192;
        Environment.gameCamera.update();
        Environment.physicsCamera.position.x = positions.get(data.get(currentPosition).name).x;
        Environment.physicsCamera.update();
        Environment.physics.constructPhysicsBoundries();
    }


}




























