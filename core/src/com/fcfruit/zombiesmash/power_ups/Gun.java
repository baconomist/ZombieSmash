package com.fcfruit.zombiesmash.power_ups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.PowerUpEntityInterface;

/**
 * Created by Lucas on 2018-03-07.
 */

public class Gun implements PowerUpEntityInterface
{

    private DrawableGraphicsEntity drawableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    public Gun(Sprite sprite)
    {
        this.drawableGraphicsEntity = new DrawableGraphicsEntity(sprite);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawableGraphicsEntity.getSize(), 0)));
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.drawableGraphicsEntity, polygon);
    }

    @Override
    public void update(float delta)
    {
        this.drawableGraphicsEntity.update(delta);
        this.interactiveGraphicsEntity.update(delta);
    }

    @Override
    public void activate()
    {

    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, pointer);
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchDragged(screenX, screenY, pointer);
        if (this.isTouching())
        {
            Vector3 pos = Environment.physicsCamera.project(new Vector3(this.getPosition(), 0));
            float angle = (float)Math.acos((pos.y - screenY)/(Math.sqrt(Math.pow(pos.x - screenX, 2) + Math.pow(pos.y - screenY, 2))));

            this.setAngle(angle);
        }
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchUp(screenX, screenY, pointer);
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawableGraphicsEntity.draw(batch);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.drawableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public boolean isTouching()
    {
        return this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }

    @Override
    public Vector2 getPosition()
    {
        return this.drawableGraphicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.drawableGraphicsEntity.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.drawableGraphicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.drawableGraphicsEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize()
    {
        return this.drawableGraphicsEntity.getSize();
    }

    @Override
    public void dispose()
    {
        this.drawableGraphicsEntity.dispose();
    }

    @Override
    public DrawableGraphicsEntity getUIDrawable()
    {
        return new DrawableGraphicsEntity(this.drawableGraphicsEntity.getSprite());
    }
}
