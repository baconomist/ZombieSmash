package com.fcfruit.zombiesmash.powerups.gun_powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;

public class PistolPowerup extends GunPowerup
{
    public PistolPowerup()
    {
        super(new Sprite(new Texture(Gdx.files.internal("powerups/gun/pistol/pistol_ui.png"))), 100, 1, new Vector2(6, 6));
    }

    @Override
    public void activate()
    {
        super.activate();

        Vector2 pos = Environment.level.objective.getPosition();

        DrawableGraphicsEntity tempGun_left = new DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/gun/pistol/pistol.png"))));
        tempGun_left.setPosition(new Vector2(pos.x + 0.8f, pos.y + 2.8f));
        tempGun_left.getSprite().setFlip(true, false);
        tempGun_left.getSprite().setScale(0.25f);
        tempGun_left.getSprite().setOriginCenter();

        DrawableGraphicsEntity tempGun_right = new DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/gun/pistol/pistol.png"))));
        tempGun_right.setPosition(new Vector2(pos.x + 4f, pos.y + 1f));
        tempGun_right.getSprite().setScale(0.25f);
        tempGun_right.getSprite().setOriginCenter();

        this.guns = new DrawableGraphicsEntity[]{tempGun_left, tempGun_right};

        if(Environment.level.getCurrentCameraPosition().equals("left"))
            Environment.drawableAddQueue.add(tempGun_left);
        else if(Environment.level.getCurrentCameraPosition().equals("right"))
            Environment.drawableAddQueue.add(tempGun_right);
        else
        {
            Environment.drawableAddQueue.add(tempGun_left);
            Environment.drawableAddQueue.add(tempGun_right);
        }

    }
}