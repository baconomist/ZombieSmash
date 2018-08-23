package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.MultiGroundEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Level
{

    public HashMap<String, Vector2> cameraPositions = new HashMap<String, Vector2>();

    public int level_id;

    JsonReader json;
    JsonValue data;

    public Sprite sprite;

    public Objective objective;

    private ArrayList<com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface> drawableEntities;

    private ArrayList<com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface> updatableEntities;

    private ArrayList<com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface> inputCaptureEntities;

    private Array<com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener> levelEventListeners;

    public int starsTouched = 0;

    boolean levelEnd = false;

    ArrayList<Spawner> spawners;

    String currentCameraPosition;

    int currentJsonItem;

    boolean isCameraMoving = false;
    boolean zombiesDead = false;

    public Level(int level_id)
    {
        this.level_id = level_id;
        this.currentJsonItem = 0;

        this.drawableEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface>();
        this.updatableEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface>();
        this.inputCaptureEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface>();

        this.levelEventListeners = new Array<com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener>();

        this.spawners = new ArrayList<Spawner>();
    }

    public void create()
    {
        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal("maps/" + this.getClass().getSimpleName().replace("Level", "").toLowerCase() + "_map/levels/" + this.level_id + ".json"));


        Environment.physicsCamera.position.x = Environment.level.cameraPositions.get(data.get(0).name).x;
        Environment.physicsCamera.update();
        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
        Environment.gameCamera.update();
        Environment.physics.constructPhysicsBoundaries();

        this.createSpawners();
    }


    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.sprite.draw(batch);

        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            drawableEntity.draw(batch);
            drawableEntity.draw(batch, skeletonRenderer);
        }
    }

    public void onTouchDown(float screenX, float screenY, int pointer)
    {

        Collections.reverse(this.drawableEntities);

        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            if (drawableEntity instanceof com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface)
            {
                ((com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface) drawableEntity).onTouchDown(screenX, screenY, pointer);
            }
        }

        Collections.reverse(this.drawableEntities);

        for (com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface inputCaptureEntity : this.inputCaptureEntities)
        {
            inputCaptureEntity.onTouchDown(screenX, screenY, pointer);
        }

    }

    public void onTouchDragged(float screenX, float screenY, int pointer)
    {
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : drawableEntities)
        {
            if (drawableEntity instanceof com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface)
            {
                ((com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface) drawableEntity).onTouchDragged(screenX, screenY, pointer);
            }
        }

        for (com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface inputCaptureEntity : this.inputCaptureEntities)
        {
            inputCaptureEntity.onTouchDragged(screenX, screenY, pointer);
        }

    }

    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : drawableEntities)
        {
            if (drawableEntity instanceof com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface)
            {
                ((com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface) drawableEntity).onTouchUp(screenX, screenY, pointer);
            }
        }

        for (com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface inputCaptureEntity : this.inputCaptureEntities)
        {
            inputCaptureEntity.onTouchUp(screenX, screenY, pointer);
        }

    }

    public void update(float delta)
    {

        // Can't be put in draw bcuz it takes too long
        // When it takes too long in a spritebatch call,
        // it doesn't draw the sprites, only the light
        //Environment.physics.update(Gdx.graphics.getDeltaTime());

        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            drawableEntity.update(delta);
        }

        for (com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface updatableEntity : this.updatableEntities)
        {
            updatableEntity.update(delta);
        }

        this.currentCameraPosition = this.data.get(this.currentJsonItem).name;

        if (!this.isCameraMoving)
        {

            for (Spawner s : spawners)
            {
                s.update();
            }

            if (this.isZombiesDead())
            {
                if (this.data.size - 1 == this.currentJsonItem)
                {
                    this.levelEnd = true;
                } else
                {
                    this.clear();

                    this.currentJsonItem += 1;
                    this.spawners = new ArrayList<Spawner>();

                    this.createSpawners();

                    for (com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener levelEventListener : this.levelEventListeners)
                    {
                        levelEventListener.onCameraMoved();
                    }

                    this.isCameraMoving = true;
                }
            }
        } else if (Environment.physicsCamera.position.x - cameraPositions.get(data.get(this.currentJsonItem).name).x > 0.1f || Environment.physicsCamera.position.x - cameraPositions.get(data.get(this.currentJsonItem).name).x < -0.1f)
        {

            if (Environment.physicsCamera.position.x < cameraPositions.get(data.get(this.currentJsonItem).name).x)
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
            this.isCameraMoving = false;
            Environment.physics.constructPhysicsBoundaries();
        }


        // Remove drawableEntities from level
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : Environment.drawableRemoveQueue)
        {
            this.drawableEntities.remove(drawableEntity);
        }
        Environment.drawableRemoveQueue.clear();

        // Add updatableEntities to level
        for (com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface updatableEntityInterface : Environment.updatableAddQueue)
        {
            this.updatableEntities.add(updatableEntityInterface);
        }
        Environment.updatableAddQueue.clear();

        // Add drawableEntities to background
        for(DrawableEntityInterface drawableEntityInterface : Environment.drawableBackgroundAddQueue)
        {
            this.drawableEntities.add(0, drawableEntityInterface);
        }

        // Add drawableEntities to level
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : Environment.drawableAddQueue)
        {
            this.drawableEntities.add(drawableEntity);
        }
        Environment.drawableAddQueue.clear();
        Environment.drawableBackgroundAddQueue.clear();

        // Add depth to drawableEntities
        this.sortMultiGroundEntities();

    }

    /**
     * @warning may be expensive, might need to test performance
     * **/
    private void sortMultiGroundEntities()
    {

        // Move all ground0 entities to front
        ArrayList<DrawableEntityInterface> copy = new ArrayList<DrawableEntityInterface>(this.drawableEntities);
        // Reverse to prevent switching of draw order
        Collections.reverse(copy);

        int last_0ground = -1;
        for(DrawableEntityInterface drawableEntityInterface : copy)
        {
            if(drawableEntityInterface instanceof MultiGroundEntityInterface)
            {
                MultiGroundEntityInterface multiGroundEntityInterface = (MultiGroundEntityInterface) drawableEntityInterface;
                if(multiGroundEntityInterface.getCurrentGround() == 0)
                {
                    this.drawableEntities.remove(drawableEntityInterface);
                    this.drawableEntities.add(0, drawableEntityInterface);
                    last_0ground += 1;
                }
            }
        }

        copy = new ArrayList<DrawableEntityInterface>(this.drawableEntities);
        // Reverse to prevent switching of draw order
        Collections.reverse(copy);

        int last_1ground = -1;
        for(DrawableEntityInterface drawableEntityInterface : copy)
        {
            if(drawableEntityInterface instanceof MultiGroundEntityInterface)
            {
                MultiGroundEntityInterface multiGroundEntityInterface = (MultiGroundEntityInterface) drawableEntityInterface;
                if(multiGroundEntityInterface.getCurrentGround() == 1 && last_0ground != -1)
                {
                    this.drawableEntities.remove(drawableEntityInterface);
                    this.drawableEntities.add(last_0ground, drawableEntityInterface);
                    last_1ground += 1;
                }
                else if(last_0ground == -1)
                {
                    this.drawableEntities.remove(drawableEntityInterface);
                    this.drawableEntities.add(0, drawableEntityInterface);
                }
            }
        }

        copy = new ArrayList<DrawableEntityInterface>(this.drawableEntities);
        // Reverse to prevent switching of draw order
        Collections.reverse(copy);

        for(DrawableEntityInterface drawableEntityInterface : copy)
        {
            if(drawableEntityInterface instanceof MultiGroundEntityInterface)
            {
                MultiGroundEntityInterface multiGroundEntityInterface = (MultiGroundEntityInterface) drawableEntityInterface;
                if(multiGroundEntityInterface.getCurrentGround() == 2 && last_1ground != -1)
                {
                    this.drawableEntities.remove(drawableEntityInterface);
                    this.drawableEntities.add(last_1ground, drawableEntityInterface);
                    last_1ground += 1;
                }
                else if(last_1ground == -1)
                {
                    this.drawableEntities.remove(drawableEntityInterface);
                    this.drawableEntities.add(0, drawableEntityInterface);
                }
            }
        }



    }

    private void createSpawners()
    {
        for (JsonValue jsonValue : this.data.get(this.currentJsonItem))
        {
            this.spawners.add(new Spawner(jsonValue));
        }
    }

    private boolean isZombiesDead()
    {

        boolean zombiesDead = false;
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : this.drawableEntities)
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
            zombiesSpawned += spawner.spawnedEntities();
            zombiesToSpawn += spawner.entitiesToSpawn();
        }
        return zombiesSpawned == zombiesToSpawn;
    }


    public void addDrawableEntity(com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity)
    {
        this.drawableEntities.add(drawableEntity);
    }

    public void addUpdatableEntity(com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface updatableEntity)
    {
        this.updatableEntities.add(updatableEntity);
    }

    public void addInputCaptureEntity(com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface inputCaptureEntity)
    {
        this.inputCaptureEntities.add(inputCaptureEntity);
    }

    public void addEventListener(com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener levelEventListener)
    {
        this.levelEventListeners.add(levelEventListener);
    }

    public ArrayList<com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface> getDrawableEntities()
    {
        return this.drawableEntities;
    }

    public String getCurrentCameraPosition()
    {
        return this.currentCameraPosition;
    }

    public void clear()
    {
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : drawableEntities)
        {
            drawableEntity.dispose();
        }
        drawableEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface>();
    }

}




























