package com.fcfruit.monstersmash.level.mode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;

import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PostLevelDestroyableInterface;
import com.fcfruit.monstersmash.entity.interfaces.PreLevelDestroyableInterface;
import com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener;
import com.fcfruit.monstersmash.level.Objective;
import com.fcfruit.monstersmash.level.Spawner;
import com.fcfruit.monstersmash.physics.Physics;
import com.fcfruit.monstersmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-18.
 */

public abstract class Level
{

    public Sprite sprite;

    public Objective objective;

    private XmasSnow xmasSnow;

    protected ArrayList<DrawableEntityInterface> drawableEntities;
    protected ArrayList<UpdatableEntityInterface> updatableEntities;
    protected ArrayList<InputCaptureEntityInterface> inputCaptureEntities;
    protected Array<LevelEventListener> levelEventListeners;

    public Level()
    {
        this.xmasSnow = new XmasSnow();

        this.drawableEntities = new ArrayList<DrawableEntityInterface>();
        this.updatableEntities = new ArrayList<UpdatableEntityInterface>();
        this.inputCaptureEntities = new ArrayList<InputCaptureEntityInterface>();

        this.levelEventListeners = new Array<LevelEventListener>();
    }

    public void load()
    {
        xmasSnow.load();
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

        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            if(this.isDrawableInLevel(drawableEntity))
            {
                drawableEntity.draw(batch);
                drawableEntity.draw(batch, skeletonRenderer);
            }
        }

        xmasSnow.draw(batch);
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
        xmasSnow.update(delta);

        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            try
            {
                drawableEntity.update(delta);
            } catch(Exception e)
            {
                Environment.crashLoggerInterface.crash_log("Level->update()->drawableEntity.update()", "DrawableEntity: "+drawableEntity + " Entities: " + this.drawableEntities
                + " Current Level ID: " + this.getLevelId());
                throw new RuntimeException(e);
            }
        }

        for (UpdatableEntityInterface updatableEntity : this.updatableEntities)
        {
            updatableEntity.update(delta);
        }

        // Remove drawableEntities from level
        for (DrawableEntityInterface drawableEntity : Environment.drawableRemoveQueue)
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
        for (UpdatableEntityInterface updatableEntityInterface : Environment.updatableAddQueue)
        {
            this.updatableEntities.add(updatableEntityInterface);
        }
        Environment.updatableAddQueue.clear();

        // Add drawableEntities to background
        for(DrawableEntityInterface drawableEntityInterface : Environment.drawableBackgroundAddQueue)
        {
            this.drawableEntities.add(0, drawableEntityInterface);
        }
        Environment.drawableBackgroundAddQueue.clear();

        // Add drawableEntities to level
        for (DrawableEntityInterface drawableEntity : Environment.drawableAddQueue)
        {
            this.drawableEntities.add(drawableEntity);
        }
        Environment.drawableAddQueue.clear();


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

    protected void preClear()
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
    }

    protected void postClear()
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

    public abstract int getBrainCount();
    public abstract int getLevelId();
    public abstract boolean isCameraMoving();
    public abstract void incrementBrainCount(int amount);

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

    public void removeInputCaptureEntity(InputCaptureEntityInterface inputCaptureEntity)
    {
        this.inputCaptureEntities.remove(inputCaptureEntity);
    }

    public void addEventListener(LevelEventListener levelEventListener)
    {
        this.levelEventListeners.add(levelEventListener);
    }

    public ArrayList<DrawableEntityInterface> getDrawableEntities()
    {
        return this.drawableEntities;
    }

    public ArrayList<UpdatableEntityInterface> getUpdatableEntities()
    {
        return this.updatableEntities;
    }

    public ArrayList<InputCaptureEntityInterface> getInputCaptureEntities()
    {
        return inputCaptureEntities;
    }

    public Array<LevelEventListener> getLevelEventListeners()
    {
        return levelEventListeners;
    }
}




























