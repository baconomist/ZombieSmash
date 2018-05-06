package com.fcfruit.zombiesmash.powerups.gun_powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;

public class RiflePowerup extends GunPowerup
{
    public RiflePowerup()
    {
        super(new Sprite(new Texture(Gdx.files.internal("powerups/gun/sniper/sniper_ui.png"))));
    }

    @Override
    public void activate()
    {
        super.activate();

        if (Environment.level.getCurrentCameraPosition().equals("left"))
        {
            com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity tempGun = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/gun/sniper/sniper.png"))));
            tempGun.setPosition(new Vector2(7, 3));
            tempGun.getSprite().setFlip(true, false);
            this.guns = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity[]{tempGun};
        } else if (Environment.level.getCurrentCameraPosition().equals("middle"))
        {
            com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity tempGun_left = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/gun/sniper/sniper.png"))));
            tempGun_left.setPosition(new Vector2(7, 3));
            tempGun_left.getSprite().setFlip(true, false);

            com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity tempGun_right = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/gun/sniper/sniper.png"))));
            tempGun_right.setPosition(new Vector2(11, 3));

            this.guns = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity[]{tempGun_left, tempGun_right};
        } else if (Environment.level.getCurrentCameraPosition().equals("right"))
        {
            com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity tempGun = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/gun/sniper/sniper.png"))));
            tempGun.setPosition(new Vector2(11, 3));
            this.guns = new com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity[]{tempGun};
        }

        for (com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity gun : this.guns)
        {
            gun.getSprite().setScale(0.25f);
            gun.getSprite().setOriginCenter();
            Environment.level.addDrawableEntity(gun);
        }

    }
}