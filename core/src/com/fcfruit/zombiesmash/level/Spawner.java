package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.zombies.BigZombie;
import com.fcfruit.zombiesmash.zombies.GirlZombie;
import com.fcfruit.zombiesmash.zombies.PoliceZombie;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-27.
 */

public class Spawner {

    static HashMap<String, Vector2> positions = new HashMap<String, Vector2>();
    static{
        positions.put("left", new Vector2(-1, 0));
        positions.put("right", new Vector2(21, 0));
        positions.put("middle_left", new Vector2(3, 0));
        positions.put("middle_right", new Vector2(16, 0));
    }

    static HashMap<String, Class> zombieType = new HashMap<String, Class>();
    static{
        zombieType.put("reg_zombie", RegularZombie.class);
        zombieType.put("girl_zombie", GirlZombie.class);
        zombieType.put("police_zombie", PoliceZombie.class);
        zombieType.put("big_zombie", BigZombie.class);
    }

    String type;

    JsonValue data;

    int spawned;

    double timer;

    boolean initDelayEnabled;

    int quantity;
    float init_delay;
    float spawn_delay;


    Zombie tempZombie;

    public Spawner(JsonValue d){

        data = d;

        spawned = 0;
        timer = System.currentTimeMillis();

        type = data.name;
        quantity = data.getInt("quantity");
        init_delay = data.getFloat("init_delay");
        spawn_delay = data.getFloat("spawn_delay");

        initDelayEnabled = init_delay != 0;
    }

    void spawnZombie(){

        try {

            tempZombie = (Zombie) zombieType.get(type).getDeclaredConstructor(Integer.class).newInstance(Environment.physics.getZombies().size() + 1);
            tempZombie.setPosition(positions.get(data.getString("position")).x, positions.get(data.getString("position")).y);
            if(data.getString("position").contains("left")) {
                tempZombie.setDirection(0);
            }
            else if(data.getString("position").contains("right")){
                tempZombie.setDirection(1);
            }
            Environment.physics.addBody(tempZombie);
            spawned += 1;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }



    }

    public void update(){
        if(quantity > spawned) {
            if (initDelayEnabled && System.currentTimeMillis() - timer >= init_delay * 1000) {
                initDelayEnabled = false;
                spawnZombie();
                timer = System.currentTimeMillis();
            }

            if (!initDelayEnabled && System.currentTimeMillis() - timer >= spawn_delay * 1000) {
                timer = System.currentTimeMillis();
                spawnZombie();
            }
        }
    }


}
