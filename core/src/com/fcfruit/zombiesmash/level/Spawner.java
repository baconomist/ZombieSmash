package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.EarClippingTriangulator;
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

    HashMap<String, Class> zombieType;

    String type;

    int spawned;

    double timer;

    boolean initDelayEnabled;

    int quantity;
    float init_delay;
    float spawn_delay;


    Zombie tempZombie;

    public Spawner(JsonValue data){

        spawned = 0;
        timer = System.currentTimeMillis();

        type = data.name;
        quantity = data.getInt("quantity");
        init_delay = data.getFloat("init_delay");
        spawn_delay = data.getFloat("spawn_delay");

        initDelayEnabled = init_delay != 0;


        zombieType = new HashMap<String, Class>();
        zombieType.put("reg_zombie", RegularZombie.class);
        zombieType.put("girl_zombie", GirlZombie.class);
        zombieType.put("police_zombie", PoliceZombie.class);
        zombieType.put("big_zombie", BigZombie.class);


    }

    void spawnZombie(){

        try {
            tempZombie = (Zombie) zombieType.get(type).getDeclaredConstructor(Integer.class).newInstance(Environment.physics.getZombies().size() + 1);
            tempZombie.setPosition(-0.5f, 0);
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
