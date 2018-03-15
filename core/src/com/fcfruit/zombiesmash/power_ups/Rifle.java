package com.fcfruit.zombiesmash.power_ups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.PowerUpEntityInterface;

/**
 * Created by Lucas on 2018-03-07.
 */

public class Rifle extends Gun
{
    public Rifle()
    {
        super(new Sprite(new Texture(Gdx.files.internal("powerups/gun/sniper/sniper.png"))));
    }
}
