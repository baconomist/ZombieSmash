package com.fcfruit.zombiesmash.powerups.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.zombies.Zombie;

public class TimePowerup implements PowerupInterface
{
    public static final int timeFactor = 4;

    private Sprite uiDrawable;

    private double destroyTimer;
    private double timeBeforeDestroy = 10000*Environment.Prefs.upgrades.getInteger("time", 1);

    private boolean isActive;

    public TimePowerup()
    {
        this.uiDrawable = new Sprite(new Texture(Gdx.files.internal("powerups/time/time.png")));
        this.isActive = false;
    }

    @Override
    public void update(float delta)
    {
        if(this.isActive() && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy)
        {
            Physics.STEP_TIME = Physics.STEP_TIME*TimePowerup.timeFactor; // Return physics to default time_step
            for(DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
            {
                if(drawableEntityInterface instanceof Zombie)
                    ((Zombie) drawableEntityInterface).getState().setTimeScale(((Zombie) drawableEntityInterface).getState().getTimeScale()*TimePowerup.timeFactor); // Speed up zombie animations to default speed
            }

            Environment.powerupManager.isSlowMotionEnabled = false;
            Environment.updatableRemoveQueue.add(this);
            this.isActive = false;
        }
    }

    @Override
    public void activate()
    {
        // Prevent 2x slow motion or 2x speed up
        if(Environment.powerupManager.isSlowMotionEnabled);
        else
        {
            Environment.powerupManager.isSlowMotionEnabled = true;
            Physics.STEP_TIME = Physics.STEP_TIME / TimePowerup.timeFactor; // Slow down physics simulation

            for (DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
            {
                if (drawableEntityInterface instanceof Zombie)
                    ((Zombie) drawableEntityInterface).getState().setTimeScale(((Zombie) drawableEntityInterface).getState().getTimeScale() / TimePowerup.timeFactor); // Slow down zombie animations
            }

            this.destroyTimer = System.currentTimeMillis();
            this.isActive = true;
        }
    }

    @Override
    public boolean hasCompleted()
    {
        return this.isActive() && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
    }

    @Override
    public boolean isActive()
    {
        return this.isActive;
    }

    @Override
    public Sprite getUIDrawable()
    {
        return this.uiDrawable;
    }

}
