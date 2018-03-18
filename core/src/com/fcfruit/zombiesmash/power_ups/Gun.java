package com.fcfruit.zombiesmash.power_ups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.ExplosionEntityParticle;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.PowerUpEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-03-07.
 */

public class Gun implements PowerUpEntityInterface
{

    private DrawableGraphicsEntity drawableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    private boolean isActive;

    private Array<ExplosionEntityParticle> particles = new Array<ExplosionEntityParticle>();

    public Gun(Sprite sprite)
    {
        this.drawableGraphicsEntity = new DrawableGraphicsEntity(sprite);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;

        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this, polygon);
    }

    @Override
    public void update(float delta)
    {
        this.drawableGraphicsEntity.update(delta);
        this.interactiveGraphicsEntity.update(delta);

        if (this.isActive)
        {
            float angle = (float)Math.toRadians(-this.getAngle() + 90);

            Vector2 rayDir = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));
            this.particles.add(new ExplosionEntityParticle(Environment.physics.getWorld(), this.getPosition(), rayDir));

            for (ExplosionEntityParticle particle : this.particles)
            {
                particle.update(delta);
                if (Math.abs(particle.physicsBody.getLinearVelocity().x) < 5f
                        && Math.abs(particle.physicsBody.getLinearVelocity().y) < 5f)
                {
                    Environment.physics.destroyBody(particle.physicsBody);
                    this.particles.removeValue(particle, true);
                }
            }

        }

    }

    @Override
    public void activate()
    {
        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;

        this.getPolygon().setVertices(new float[]{0, 0, size.x * 2, 0, size.x * 2, size.y * 2, 0, size.y * 2});
        this.getPolygon().setOrigin(size.x / 2, size.y / 2);

        // Reset sprite rotation origin from center to left corner(cus of polygon setup...)
        this.drawableGraphicsEntity.getSprite().setOrigin(0, 0);

        this.setPosition(new Vector2(5, 2));

        this.isActive = true;
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
            Gdx.app.log("pos", "" + pos.x + " " + Gdx.input.getX());

            float angle = (float) Math.atan2((pos.y - screenY), (pos.x - screenX));
            angle = (float) Math.toDegrees(angle);

            if (angle != 0)
                this.setAngle(-angle - 1);
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
