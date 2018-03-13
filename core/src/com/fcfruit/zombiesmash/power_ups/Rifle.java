package com.fcfruit.zombiesmash.power_ups;

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

public class Rifle implements PowerUpEntityInterface
{

    public Rifle()
    {

    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void update(float delta)
    {

    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void activate()
    {

    }

    @Override
    public void draw(SpriteBatch batch)
    {

    }

    @Override
    public DrawableGraphicsEntity get_ui_image()
    {
        return null;
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

    @Override
    public boolean isTouching()
    {
        return false;
    }

    @Override
    public Polygon getPolygon()
    {
        return null;
    }

    @Override
    public Vector2 getPosition()
    {
        return null;
    }

    @Override
    public void setPosition(Vector2 position)
    {

    }

    @Override
    public float getAngle()
    {
        return 0;
    }

    @Override
    public void setAngle(float angle)
    {

    }

    @Override
    public Vector2 getSize()
    {
        return null;
    }

    @Override
    public void dispose()
    {

    }
}
