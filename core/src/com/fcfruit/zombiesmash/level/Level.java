package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PostLevelDestroyableInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PreLevelDestroyableInterface;
import com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener;
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
    public boolean level_ended;

    JsonReader json;
    JsonValue data;

    public Sprite sprite;

    public Objective objective;

    private ArrayList<com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface> drawableEntities;

    private ArrayList<com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface> updatableEntities;

    private ArrayList<com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface> inputCaptureEntities;

    private Array<com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener> levelEventListeners;

    public int brainCounter = 0;

    boolean levelEnd = false;

    ArrayList<Spawner> loaded_spawners;
    ArrayList<Spawner> spawners;

    String currentCameraPosition;

    int currentJsonItem;

    boolean isCameraMoving = false;
    boolean zombiesDead = false;
    boolean preCleared = false;

    public Level(int level_id)
    {
        this.level_id = level_id;
        this.level_ended = false;
        this.currentJsonItem = 0;

        this.drawableEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface>();
        this.updatableEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface>();
        this.inputCaptureEntities = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface>();

        this.levelEventListeners = new Array<com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener>();

        this.loaded_spawners = new ArrayList<Spawner>();
        this.spawners = new ArrayList<Spawner>();

        /*Message tempMessage = new Message();
        tempMessage.setContent("Hey There!\nNew Line!\nAnother Line!\nSo Many New Lines!\n HI\n\n\n\\n\n\n\n\n\n\n\n\n\n\naaaaaassssssssssssssssssssssssssssssssssssa");
        Environment.drawableAddQueue.add(tempMessage);
        this.addInputCaptureEntity(tempMessage);*/
    }

    public void create()
    {
        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal("maps/" + this.getClass().getSimpleName().replace("Level", "").toLowerCase() + "_map/levels/" + this.level_id + ".json"));


        Environment.physicsCamera.position.x = this.cameraPositions.get(data.get(0).name).x;
        Environment.physicsCamera.update();
        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
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
                this.loaded_spawners.add(new Spawner(spawnerValue, index));
                index++;
            }
        }
    }

    private void manageSpawners()
    {
        for(JsonValue jsonValue : this.data.get(this.currentJsonItem))
        {
            for(Spawner spawner : this.loaded_spawners)
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

    private boolean isDrawableInLevel(DrawableEntityInterface drawableEntityInterface)
    {
        try
        {
            Vector2 position = drawableEntityInterface.getPosition();
            Vector2 size = drawableEntityInterface.getSize();
            return position.x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - size.x
                    && position.x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + size.x
                    && position.y > Environment.physicsCamera.position.y - Environment.physicsCamera.viewportHeight / 2 - size.y
                    && position.y < Environment.physicsCamera.position.y + Environment.physicsCamera.viewportHeight / 2 + size.y;
        } catch (NullPointerException e)
        {
            Gdx.app.error("Level::isDrawableInLevel", "A nullPointerException error was encountered." +
                    "\n Check if any zombies are failing to instantiate in spawner when loading." +
                    "\n This is typically the cause.");
        }
        return false;
    }

    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.sprite.draw(batch);
        this.objective.draw(batch);

        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            if(this.isDrawableInLevel(drawableEntity))
            {
                drawableEntity.draw(batch);
                drawableEntity.draw(batch, skeletonRenderer);
            }
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
        if(Environment.level.objective.getHealth() <= 0 || (this.data.size - 1 == this.currentJsonItem && this.isZombiesDead()))
        {
            for (LevelEventListener levelEventListener : this.levelEventListeners)
                levelEventListener.onLevelEnd();
            this.onLevelEnd();
        }

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

                    for (com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener levelEventListener : this.levelEventListeners)
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
            this.spawners = new ArrayList<Spawner>();
            this.manageSpawners();

            Environment.physics.constructPhysicsBoundaries();
        }


        // Remove drawableEntities from level
        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : Environment.drawableRemoveQueue)
        {
            this.drawableEntities.remove(drawableEntity);
        }
        Environment.drawableRemoveQueue.clear();

        // Remove updatableEntities from level
        for(UpdatableEntityInterface updatableEntityInterface : Environment.updatableRemoveQueue)
        {
            this.updatableEntities.remove(updatableEntityInterface);
        }
        Environment.updatableRemoveQueue.clear();

        // Remove levelEventListeners from level
        for(LevelEventListener levelEventListener : Environment.levelEventListenerRemoveQueue)
        {
            this.levelEventListeners.removeValue(levelEventListener, true);
        }
        Environment.levelEventListenerRemoveQueue.clear();

        // Add levelEventListeners to level
        for(LevelEventListener levelEventListener : Environment.levelEventListenerAddQueue)
        {
            this.levelEventListeners.add(levelEventListener);
        }
        Environment.levelEventListenerAddQueue.clear();

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

        for(DrawableEntityInterface drawableEntityInterface : copy)
        {
            if(drawableEntityInterface instanceof MultiGroundEntityInterface)
            {
                MultiGroundEntityInterface multiGroundEntityInterface = (MultiGroundEntityInterface) drawableEntityInterface;
                if(multiGroundEntityInterface.getCurrentGround() == 0)
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
                if(multiGroundEntityInterface.getCurrentGround() == 1)
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
                if(multiGroundEntityInterface.getCurrentGround() == 2)
                {
                    this.drawableEntities.remove(drawableEntityInterface);
                    this.drawableEntities.add(0, drawableEntityInterface);
                }
            }
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
            if(spawner.data.name().contains("zombie"))
            {
                zombiesSpawned += spawner.spawnedEntities();
                zombiesToSpawn += spawner.entitiesToSpawn();
            }
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

    public void removeInputCaptureEntity(InputCaptureEntityInterface inputCaptureEntity)
    {
        this.inputCaptureEntities.remove(inputCaptureEntity);
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

    public boolean isCameraMoving()
    {
        return this.isCameraMoving;
    }

    private void onLevelEnd()
    {
        if(!this.level_ended)
        {
            if(this.objective.getHealth() > 0)
            {
                Environment.Prefs.brains.putInteger("userBrainCount", Environment.Prefs.brains.getInteger("userBrainCount", 0) + brainCounter);
                Environment.Prefs.brains.flush();
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

}




























