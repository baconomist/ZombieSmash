package com.fcfruit.zombiesmash.powerups.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.physics.Physics;

public class TimePowerup implements PowerupInterface
{
    public static final int timeFactor = 4;

    private Sprite uiDrawable;

    private double destroyTimer;
    private double timeBeforeDestroy = 10000;

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
            Environment.powerupManager.isSlowMotionEnabled = false;
            Environment.updatableRemoveQueue.add(this);
            this.isActive = false;
        }
    }

    @Override
    public void activate()
    {
        Environment.powerupManager.isSlowMotionEnabled = true;
        Physics.STEP_TIME = Physics.STEP_TIME/TimePowerup.timeFactor; // Slow down physics simulation
        this.destroyTimer = System.currentTimeMillis();
        this.isActive = true;
    }

    @Override
    public boolean hasCompleted()
    {
        return !this.isActive();
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
