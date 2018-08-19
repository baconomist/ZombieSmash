package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;

import java.util.Random;

/**
 * Created by Lucas on 2018-05-21.
 */

public class GroundBlood extends DrawableGraphicsEntity
{

    private double timeBeforeDisable = 3000;
    private double disableTimer = System.currentTimeMillis();

    public GroundBlood()
    {
        super(new Sprite(Environment.assets.get("effects/blood/ground_blood/ground_blood.atlas", TextureAtlas.class).findRegion(""+(new Random().nextInt(4)+1))));
        this.getSprite().setScale(0.5f);
    }

    public void enable()
    {
        this.disableTimer = System.currentTimeMillis();
    }

    public void disable()
    {
        Environment.drawableRemoveQueue.add(this);
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        super.draw(spriteBatch);

        this.update();
    }

    private void update()
    {
        if(System.currentTimeMillis() - this.disableTimer > this.timeBeforeDisable)
        {
            this.disable();
        }
    }

}
