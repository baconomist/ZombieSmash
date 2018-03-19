package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.zombies.NewZombie;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Level
{

    public static HashMap<String, Vector2> cameraPositions = new HashMap<String, Vector2>();

    static
    {
        cameraPositions.put("left", new Vector2(Environment.physicsCamera.viewportWidth / 2, 0));
        cameraPositions.put("right", new Vector2(Environment.physicsCamera.viewportWidth * 1.4f, 0));
        cameraPositions.put("middle", new Vector2(10, 0));
    }

    public int level_id;

    JsonReader json;
    JsonValue data;

    Sprite sprite;

    public Objective objective;

    private ArrayList<DrawableEntityInterface> drawableEntities;

    private ArrayList<UpdatableEntityInterface> updatableEntities;

    private ArrayList<InputCaptureEntityInterface> inputCaptureEntities;

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

        this.drawableEntities = new ArrayList<DrawableEntityInterface>();
        this.updatableEntities = new ArrayList<UpdatableEntityInterface>();
        this.inputCaptureEntities = new ArrayList<InputCaptureEntityInterface>();

        this.spawners = new ArrayList<Spawner>();
    }

    public void create()
    {
        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal("maps/" + this.getClass().getSimpleName().replace("Level", "").toLowerCase() + "_map/levels/" + this.level_id + ".json"));


        Environment.physicsCamera.position.x = Level.cameraPositions.get(data.get(0).name).x;
        Environment.physicsCamera.update();
        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
        Environment.gameCamera.update();
        Environment.physics.constructPhysicsBoundaries();
        this.createSpawners();
    }


    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.sprite.draw(batch);

        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            drawableEntity.draw(batch);
            drawableEntity.draw(batch, skeletonRenderer);
        }
    }

    public void onTouchDown(float screenX, float screenY, int pointer)
    {

        Collections.reverse(this.drawableEntities);

        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            if (drawableEntity instanceof InteractiveEntityInterface)
            {
                ((InteractiveEntityInterface) drawableEntity).onTouchDown(screenX, screenY, pointer);
            }
        }

        Collections.reverse(this.drawableEntities);

        for (InputCaptureEntityInterface inputCaptureEntity : this.inputCaptureEntities)
        {
            inputCaptureEntity.onTouchDown(screenX, screenY, pointer);
        }

    }

    public void onTouchDragged(float screenX, float screenY, int pointer)
    {
        for (DrawableEntityInterface drawableEntity : drawableEntities)
        {
            if (drawableEntity instanceof InteractiveEntityInterface)
            {
                ((InteractiveEntityInterface) drawableEntity).onTouchDragged(screenX, screenY, pointer);
            }
        }

        for (InputCaptureEntityInterface inputCaptureEntity : this.inputCaptureEntities)
        {
            inputCaptureEntity.onTouchDragged(screenX, screenY, pointer);
        }

    }

    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        for (DrawableEntityInterface drawableEntity : drawableEntities)
        {
            if (drawableEntity instanceof InteractiveEntityInterface)
            {
                ((InteractiveEntityInterface) drawableEntity).onTouchUp(screenX, screenY, pointer);
            }
        }

        for (InputCaptureEntityInterface inputCaptureEntity : this.inputCaptureEntities)
        {
            inputCaptureEntity.onTouchUp(screenX, screenY, pointer);
        }

    }

    public void update(float delta)
    {
        for (DrawableEntityInterface drawableEntity : Environment.drawableRemoveQueue)
        {
            this.drawableEntities.remove(drawableEntity);
        }
        Environment.drawableRemoveQueue.clear();

        for (DrawableEntityInterface drawableEntity : Environment.drawableAddQueue)
        {
            this.drawableEntities.add(drawableEntity);
        }
        Environment.drawableAddQueue.clear();


        // Can't be put in draw bcuz it takes too long
        // When it takes too long in a spritebatch call,
        // it doesn't draw the sprites, only the light
        //Environment.physics.update(Gdx.graphics.getDeltaTime());

        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            drawableEntity.update(delta);
        }

        for (UpdatableEntityInterface updatableEntity : this.updatableEntities)
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
        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            if (drawableEntity instanceof NewZombie)
            {
                if (((NewZombie) drawableEntity).isAlive())
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


    public void addDrawableEntity(DrawableEntityInterface drawableEntity)
    {
        this.drawableEntities.add(drawableEntity);
    }

    public void addUpdatableEntity(UpdatableEntityInterface updatableEntity)
    {
        this.updatableEntities.add(updatableEntity);
    }

    public void addInputCaptureEntity(InputCaptureEntityInterface inputCaptureEntity)
    {
        this.inputCaptureEntities.add(inputCaptureEntity);
    }

    public ArrayList<DrawableEntityInterface> getDrawableEntities()
    {
        return this.drawableEntities;
    }

    public String getCurrentCameraPosition()
    {
        return this.currentCameraPosition;
    }

    public void clear()
    {
        for (DrawableEntityInterface drawableEntity : drawableEntities)
        {
            drawableEntity.dispose();
        }
        drawableEntities = new ArrayList<DrawableEntityInterface>();
    }

}




























