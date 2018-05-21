package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;

import java.util.Random;

/**
 * Created by Lucas on 2018-05-21.
 */

public class GroundBlood extends DrawableGraphicsEntity
{

    private double timeBeforeDestroy = 3000;
    private double destroyTimer = System.currentTimeMillis();

    public GroundBlood()
    {
        super(new Sprite(Environment.assets.get("effects/blood/ground_blood/"+(new Random().nextInt(4)+1)+".png", Texture.class)));
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        super.draw(spriteBatch);

        this.update();
    }

    private void update()
    {
        if(System.currentTimeMillis() - this.destroyTimer > this.timeBeforeDestroy)
        {
            Environment.drawableRemoveQueue.add(this);
            this.dispose();
        }
    }

}
