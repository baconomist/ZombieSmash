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

    public NightLevel(int lvlnum){

        this.lvlNum = lvlnum;


        this.sprite = new Sprite(new Texture(Gdx.files.internal("maps/night_map/night_map_new.png")));

        this.objective = new House();
        this.objective.setPosition(7f, 0f);

        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal("maps/night_map/levels/"+this.lvlNum+".json"));

        this.spawners = new ArrayList<Spawner>();
        for(JsonValue v : this.data.get(0)){
            this.spawners.add(new Spawner(v));
        }

        currentPosition = 0;

    }



    @Override
    public void update(){

        currentCameraPosition = this.data.get(currentPosition).name;

        if (!movingCamera) {
            super.update();
            for (Spawner s : spawners) {
                s.update();
            }


            if (zombiesDead) {
                if (this.data.size - 1 == this.currentPosition) {
                    this.levelEnd = true;
                } else {
                    this.currentPosition += 1;
                    Environment.physics.clearBodies();
                    zombies = Environment.physics.getZombies();
                    parts = Environment.physics.getParts();
                    powerUps = Environment.physics.getPowerUps();
                    this.spawners = new ArrayList<Spawner>();
                    for (JsonValue v : this.data.get(currentPosition)) {
                        this.spawners.add(new Spawner(v));
                    }
                    zombiesDead = false;
                    movingCamera = true;
                }
            }
        }
        else if(Environment.physicsCamera.position.x - positions.get(data.get(currentPosition).name).x > 0.1f || Environment.physicsCamera.position.x - positions.get(data.get(currentPosition).name).x < -0.1f ){

                if(Environment.physicsCamera.position.x < positions.get(data.get(currentPosition).name).x) {
                    Environment.gameCamera.position.x += 5f;
                    Environment.physicsCamera.position.x += 5f / 192f;
                }
                else{
                    Environment.gameCamera.position.x -= 5f;
                    Environment.physicsCamera.position.x -= 5f / 192f;
                }
                Environment.gameCamera.update();
                Environment.physicsCamera.update();
        }
        else{
            movingCamera = false;
            Environment.physics.constructPhysicsBoundries();
        }
    }




}
