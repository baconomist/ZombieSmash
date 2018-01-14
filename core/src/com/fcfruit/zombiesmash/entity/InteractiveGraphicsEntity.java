package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
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
        // Center the polygon on physics body
        polygon.setPosition(pos.x - polygon.getVertices()[2] / 2, pos.y - polygon.getVertices()[5] / 2);
        polygon.setRotation(this.drawableEntity.getAngle());

    }

    @Override
    public void onTouchDown(float x, float y, int p)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
        pos.y = Environment.gameCamera.viewportHeight - pos.y;

        if (Environment.touchedDownItems.size() < 1 && this.polygon.contains(pos.x, pos.y))
        {
            this.pointer = p;
            this.isTouching = true;
            Environment.touchedDownItems.add(this);
        }
    }

    @Override
    public void onTouchDragged(float x, float y, int p)
    {
    }

    @Override
    public void onTouchUp(float x, float y, int p)
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

}
