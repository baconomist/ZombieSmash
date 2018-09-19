package com.fcfruit.zombiesmash.powerups.rocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.helicopter.BomberHelicopter;
import com.fcfruit.zombiesmash.effects.helicopter.Helicopter;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;

public class RocketPowerup implements PowerupInterface
{

    private Sprite uiDrawable;
    private Helicopter helicopter;

    private boolean isActive = false;

    public RocketPowerup()
    {
        this.uiDrawable = new Sprite(new Texture(Gdx.files.internal("powerups/rocket/rocket_ui.png")));
    }


    @Override
    public void update(float delta)
    {
        if(this.helicopter.getPosition().x <= -19)
        {
            Environment.drawableRemoveQueue.add(this.helicopter);
            Environment.updatableRemoveQueue.add(this);
            this.isActive = false;
        }
    }

    @Override
    public void activate()
    {
        this.helicopter = new BomberHelicopter();
        helicopter.setPosition(new Vector2(40, 8));
        Environment.drawableAddQueue.add(helicopter);
        this.isActive = true;
    }

    @Override
    public boolean hasCompleted()
    {
        return this.isActive() && this.helicopter.getPosition().x <= -19;
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
