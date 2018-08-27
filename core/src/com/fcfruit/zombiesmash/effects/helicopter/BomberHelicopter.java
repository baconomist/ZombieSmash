package com.fcfruit.zombiesmash.effects.helicopter;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.powerups.rocket.Rocket;

public class BomberHelicopter extends Helicopter
{

    private double bombTimer;
    private double timeBeforeBomb = 250;

    public BomberHelicopter()
    {
        super();
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        if(!this.isInLevel())
        {
            this.bombTimer = System.currentTimeMillis();
        }
        else if(System.currentTimeMillis() - this.bombTimer >= this.timeBeforeBomb)
        {
            Rocket rocket = Environment.rocketPool.getRocket();
            rocket.setPosition(this.getPosition());
            Environment.drawableBackgroundAddQueue.add(rocket);

            bombTimer = System.currentTimeMillis();
        }
    }
}
