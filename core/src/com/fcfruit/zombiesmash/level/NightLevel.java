package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.zombies.BigZombie;
import com.fcfruit.zombiesmash.zombies.GirlZombie;
import com.fcfruit.zombiesmash.zombies.PoliceZombie;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-11-18.
 */

public class NightLevel extends Level{

    ArrayList<Spawner> spawners;



    public NightLevel(int lvlnum){

        this.lvlNum = lvlnum;

        this.sprite = new Sprite(new Texture(Gdx.files.internal("maps/night_map/night_map.png")));
        this.objective = new House();
        this.objective.setPosition(9.5f, 2.5f);

        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal("maps/night_map/levels/"+this.lvlNum+".json"));

        this.spawners = new ArrayList<Spawner>();
        for(JsonValue v : this.data){
            this.spawners.add(new Spawner(v));
        }


        /*Zombie z;
        for(int i = 0; i < 10; i++) {
            z = new RegularZombie(i);
            Environment.physics.addBody(z);
            z.setPosition(i, 0);
        }
        for(int i = 0; i < 10; i++) {
            z = new GirlZombie(i+10);
            Environment.physics.addBody(z);
            z.setPosition(i, 0);
        }
        for(int i = 0; i < 10; i++) {
            z = new PoliceZombie(i+20);
            Environment.physics.addBody(z);
            z.setPosition(i, 0);
        }
        for(int i = 0; i < 10; i++) {
            z = new BigZombie(i+20);
            Environment.physics.addBody(z);
            z.setPosition(i, 0);
        }*/

    }

    @Override
    public void update(){
        for(Spawner s : spawners){
            s.update();
        }
    }


}
