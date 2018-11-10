package com.fcfruit.monstersmash.level.mode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PostLevelDestroyableInterface;
import com.fcfruit.monstersmash.entity.interfaces.PreLevelDestroyableInterface;
import com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener;
import com.fcfruit.monstersmash.level.Spawner;
import com.fcfruit.monstersmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.HashMap;

public class SurvivalLevel extends Level
{
    public HashMap<String, Vector2> cameraPositions = new HashMap<String, Vector2>();
    private String level_data_file_path;

    private int level_id;
    public boolean level_ended;

    JsonReader json;
    JsonValue data;

    private int brainCounter = 0;

    boolean levelEnd = false;

    ArrayList<Spawner> loaded_spawners;
    ArrayList<Spawner> spawners;

    String currentCameraPosition;

    int currentJsonItem;

    private boolean isCameraMoving = false;
    private boolean zombiesDead = false;
    private boolean preCleared = false;

    public SurvivalLevel(int level_id, String level_data_file_path)
    {
        this.level_id = level_id;
        this.level_data_file_path = level_data_file_path;

        this.level_ended = false;
        this.currentJsonItem = 0;

        this.loaded_spawners = new ArrayList<com.fcfruit.monstersmash.level.Spawner>();
        this.spawners = new ArrayList<com.fcfruit.monstersmash.level.Spawner>();
    }

    @Override
    public void load()
    {
        super.load();

        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal(level_data_file_path));


        Environment.physicsCamera.position.x = this.cameraPositions.get(data.get(0).name).x;
        Environment.physicsCamera.update();
        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * com.fcfruit.monstersmash.physics.Physics.PIXELS_PER_METER;
        Environment.gameCamera.update();
        Environment.physics.constructPhysicsBoundaries();

        this.createSpawners();
        this.manageSpawners();
    }

    private void createSpawners()
    {
        int index = 0;
        for(JsonValue jsonValue : this.data)
        {
            for(JsonValue spawnerValue : jsonValue)
            {
                this.loaded_spawners.add(new com.fcfruit.monstersmash.level.Spawner(spawnerValue, index));
                index++;
            }
        }
    }

    private void manageSpawners()
    {
        for(JsonValue jsonValue : this.data.get(this.currentJsonItem))
        {
            for(com.fcfruit.monstersmash.level.Spawner spawner : this.loaded_spawners)
            {
                // If spawner is in current section/camera by checking data
                if(jsonValue.equals(spawner.data))
                {
                    this.spawners.add(spawner);
                    break;
                }
            }
        }
    }

    private boolean isZombiesDead()
    {

        boolean zombiesDead = false;
        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            if (drawableEntity instanceof Zombie)
            {
                if (((Zombie) drawableEntity).isAlive())
                {
                    zombiesDead = false;
                    break;
                } else
                {
                    zombiesDead = true;
                }
            }
        }

        return this.allZombiesSpawned() && zombiesDead;

    }

    private boolean allZombiesSpawned()
    {
        int zombiesSpawned = 0;
        int zombiesToSpawn = 0;
        for (Spawner spawner : this.spawners)
        {
            if(spawner.data.name().contains("zombie"))
            {
                zombiesSpawned += spawner.spawnedEntities();
                zombiesToSpawn += spawner.entitiesToSpawn();
            }
        }
        return zombiesSpawned == zombiesToSpawn;
    }


    public String getCurrentCameraPosition()
    {
        return this.currentCameraPosition;
    }

    private void onLevelEnd()
    {
        if(!this.level_ended)
        {
            if(this.objective.getHealth() > 0)
            {
                Environment.Prefs.brains.putInteger("userBrainCount", Environment.Prefs.brains.getInteger("userBrainCount", 0) + brainCounter);
                Environment.Prefs.brains.flush();
                int last_completed_level = Environment.Prefs.progress.getInteger("lastCompletedLevel", 1);
                if(Environment.Prefs.progress.getInteger("lastCompletedLevel", 1) < 31 && objective.getHealth() > 0
                        && last_completed_level + 1 == level_id)
                {
                    Environment.Prefs.progress.putInteger("lastCompletedLevel", last_completed_level + 1);
                    Environment.Prefs.progress.flush();
                }
            }
            Environment.screens.gamescreen.get_ui_stage().onLevelEnd();
        }
        this.level_ended = true;
    }

    private void preClear()
    {
        for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
        {
            if(drawableEntityInterface instanceof PreLevelDestroyableInterface)
                ((PreLevelDestroyableInterface) drawableEntityInterface).destroy();
        }
        for(UpdatableEntityInterface updatableEntityInterface : this.updatableEntities)
        {
            if(updatableEntityInterface instanceof PreLevelDestroyableInterface)
                ((PreLevelDestroyableInterface) updatableEntityInterface).destroy();
        }
        for(InputCaptureEntityInterface inputCaptureEntityInterface : this.inputCaptureEntities)
        {
            if(inputCaptureEntityInterface instanceof PreLevelDestroyableInterface)
                ((PreLevelDestroyableInterface) inputCaptureEntityInterface).destroy();
        }

        this.preCleared = true;
    }

    private void postClear()
    {
        for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
        {
            if(drawableEntityInterface instanceof PostLevelDestroyableInterface)
                ((PostLevelDestroyableInterface) drawableEntityInterface).destroy();
        }
        for(UpdatableEntityInterface updatableEntityInterface : this.updatableEntities)
        {
            if(updatableEntityInterface instanceof PostLevelDestroyableInterface)
                ((PostLevelDestroyableInterface) updatableEntityInterface).destroy();
        }
        for(InputCaptureEntityInterface inputCaptureEntityInterface : this.inputCaptureEntities)
        {
            if(inputCaptureEntityInterface instanceof PostLevelDestroyableInterface)
                ((PostLevelDestroyableInterface) inputCaptureEntityInterface).destroy();
        }
    }

    @Override
    public void update(float delta)
    {
        if(Environment.level.objective.getHealth() <= 0 || (this.data.size - 1 == this.currentJsonItem && this.isZombiesDead()))
        {
            for (LevelEventListener levelEventListener : this.levelEventListeners)
                levelEventListener.onLevelEnd();
            this.onLevelEnd();
        }

        this.currentCameraPosition = this.data.get(this.currentJsonItem).name;

        if (!this.isCameraMoving)
        {

            for (Spawner spawner : spawners)
            {
                if(this.data.get(this.currentJsonItem).hasChild(spawner.data.name())) // If spawner is part of our current level section
                    spawner.update(delta);
            }

            if (this.isZombiesDead())
            {
                if (this.data.size - 1 == this.currentJsonItem)
                {
                    this.levelEnd = true;
                } else
                {
                    if(!this.preCleared && !this.data.get(this.currentJsonItem + 1).name().equals(this.currentCameraPosition))
                        this.preClear();

                    for (LevelEventListener levelEventListener : this.levelEventListeners)
                    {
                        levelEventListener.onCameraMoved();
                    }

                    this.isCameraMoving = true;
                }
            }
        } else if (Environment.physicsCamera.position.x - cameraPositions.get(data.get(this.currentJsonItem + 1).name).x > 0.1f || Environment.physicsCamera.position.x - cameraPositions.get(data.get(this.currentJsonItem + 1).name).x < -0.1f)
        {
            if (Environment.physicsCamera.position.x < cameraPositions.get(data.get(this.currentJsonItem + 1).name).x)
            {
                Environment.gameCamera.position.x += 5f;
                Environment.physicsCamera.position.x += 5f / 192f;
            } else
            {
                Environment.gameCamera.position.x -= 5f;
                Environment.physicsCamera.position.x -= 5f / 192f;
            }
            Environment.gameCamera.update();
            Environment.physicsCamera.update();
        } else
        {
            this.preCleared = false;
            this.isCameraMoving = false;

            if(!this.data.get(this.currentJsonItem + 1).name().equals(this.currentCameraPosition))
                this.postClear();

            this.currentJsonItem += 1;
            this.spawners = new ArrayList<com.fcfruit.monstersmash.level.Spawner>();
            this.manageSpawners();

            Environment.physics.constructPhysicsBoundaries();
        }

        super.update(delta);
    }

    @Override
    public int getBrainCount()
    {
        return this.brainCounter;
    }

    @Override
    public int getLevelId()
    {
        return this.level_id;
    }

    @Override
    public boolean isCameraMoving()
    {
        return this.isCameraMoving;
    }

    @Override
    public void incrementBrainCount(int amount)
    {
        this.brainCounter += amount;
    }
}
