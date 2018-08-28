package com.fcfruit.zombiesmash.effects.helicopter;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.powerups.rocket.Rocket;

public class BomberHelicopter extends Helicopter
{

    private double accumilator = 0d;
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
            this.accumilator = 0d;
        }
        else if(this.accumilator*1000 >= this.timeBeforeBomb)
        {
            Rocket rocket = Environment.rocketPool.getRocket();
            rocket.setPosition(this.getPosition());
            Environment.drawableBackgroundAddQueue.add(rocket);
            this.accumilator = 0d;
        }

        this.accumilator += delta;
    }
}
