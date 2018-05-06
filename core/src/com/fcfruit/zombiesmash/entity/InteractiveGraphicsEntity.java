package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;

/**
 * Created by Lucas on 2018-01-06.
 */

public class InteractiveGraphicsEntity implements InteractiveEntityInterface
{

    private boolean isTouching;
    private int pointer;

    private DrawableEntityInterface drawableEntity;
    private Polygon polygon;

    public InteractiveGraphicsEntity(DrawableEntityInterface drawableEntity, Polygon polygon)
    {
        this.isTouching = false;
        this.pointer = -1;

        this.drawableEntity = drawableEntity;
        this.polygon = polygon;
    }

    public void update(float delta)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawableEntity.getPosition(), 0)));
        pos.y = Environment.gameCamera.viewportHeight - pos.y;
        /**
         * InteractiveGraphicsEntity uses polygon origin for custom offset rather than size.x/2 and size.y/2 like physics does
         * **/
        polygon.setPosition(pos.x - polygon.getOriginX(), pos.y - polygon.getOriginY());
        polygon.setRotation(this.drawableEntity.getAngle());

    }

    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {

        Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));

        if (Environment.touchedDownItems.size() < 1 && this.polygon.contains(pos.x, pos.y))
        {
            this.pointer = p;
            this.isTouching = true;
            Environment.touchedDownItems.add(this);
        }
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int p)
    {
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int p)
    {
        if (pointer == p)
        {
            isTouching = false;
            pointer = -1;
        }
    }

    @Override
    public boolean isTouching()
    {
        return isTouching;
    }

    @Override
    public Polygon getPolygon()
    {
        return this.polygon;
    }
}
