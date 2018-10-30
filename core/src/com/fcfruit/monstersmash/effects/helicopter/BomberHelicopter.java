package com.fcfruit.monstersmash.effects.helicopter;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.powerups.rocket.Rocket;

public class BomberHelicopter extends Helicopter
{

    private double accumilator = 0d;
    private double timeBeforeBomb = 250;

    public BomberHelicopter()
    {
        super();
    }

    public void setTimeBeforeBomb(double time)
    {
        this.timeBeforeBomb = time;
    }

    public double getTimeBeforeBomb(){return this.timeBeforeBomb;}

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
            com.fcfruit.monstersmash.powerups.rocket.Rocket rocket = Environment.rocketPool.getRocket();
            rocket.setPosition(this.getPosition());
            Environment.drawableBackgroundAddQueue.add(rocket);
            this.accumilator = 0d;
        }

        this.accumilator += Math.min(delta, 0.25f);
    }
}
