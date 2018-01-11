package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;

/**
 * Created by Lucas on 2017-07-30.
 */

public class NewZombie implements DrawableEntityInterface, InteractiveEntityInterface
{

    private int id;

    private DrawableGraphicsEntity drawableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    public NewZombie(int id, DrawableGraphicsEntity drawableGraphicsEntity, InteractiveGraphicsEntity interactiveGraphicsEntity){
        this.id = id;

        this.drawableGraphicsEntity = drawableGraphicsEntity;
        this.interactiveGraphicsEntity = interactiveGraphicsEntity;

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer) {
        this.drawableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta) {
        this.drawableGraphicsEntity.update(delta);
    }


    @Override
    public void onTouchDown(float x, float y, int p)
    {

    }

    @Override
    public void onTouchDragged(float x, float y, int p)
    {

    }

    @Override
    public void onTouchUp(float x, float y, int p)
    {

    }

    @Override
    public Vector2 getPosition() {
        return this.drawableGraphicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position) {
        this.drawableGraphicsEntity.setPosition(position);
    }

    @Override
    public float getAngle() {
        return this.drawableGraphicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle) {
        this.drawableGraphicsEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize() {
        return this.drawableGraphicsEntity.getSize();
    }

    @Override
    public boolean isTouching()
    {
        return false;
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch) {

    }

}