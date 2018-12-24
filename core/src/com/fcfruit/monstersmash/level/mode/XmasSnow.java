package com.fcfruit.monstersmash.level.mode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.DrawableGraphicsEntity;

import java.util.ArrayList;

public class XmasSnow
{
    private ArrayList<DrawableGraphicsEntity> snowflakes;

    public XmasSnow()
    {
        this.snowflakes = new ArrayList<DrawableGraphicsEntity>();
    }

    public void load()
    {
        for(int i = 0; i < 35; i++)
        {
            DrawableGraphicsEntity snowFlake = new DrawableGraphicsEntity(new Sprite(Environment.assets.get("maps/night_map/night_map.atlas", TextureAtlas.class).findRegion("snow_1")));
            resetSnowFlake(snowFlake);
            snowFlake.setPosition(new Vector2(snowFlake.getPosition().x, snowFlake.getPosition().y + 1f*i));
            this.snowflakes.add(snowFlake);
        }
    }

    private void resetSnowFlake(DrawableGraphicsEntity snowFlake)
    {
        snowFlake.setPosition(new Vector2((Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth/2) + (float)Math.random()*Environment.physicsCamera.viewportWidth,
                Environment.physicsCamera.viewportHeight + (float)(Math.random() * 2 + 1)));
        snowFlake.setAngle((float)Math.random()*360);
        snowFlake.update(Gdx.graphics.getDeltaTime());
    }

    private boolean isOnRoof(DrawableGraphicsEntity snowflake)
    {
        Vector3 point = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(snowflake.getPosition(), 0)));
        point.y = Environment.gameCamera.position.y*2 - point.y;
        point.y = point.y + 150f + (float)(Math.random()*30f); // Make snowflake disappear when it actually hits the roof rather than touches the bounding box of the house

        float x = Environment.level.objective.polygon.getX() + 400f;
        float poly_bound_x = Environment.level.objective.polygon.getX() + Environment.level.objective.polygon.getVertices()[2];

        return point.x > x && point.x < poly_bound_x && Environment.level.objective.polygon.contains(point.x, point.y);
    }

    public void update(float delta)
    {
        for(DrawableGraphicsEntity drawableGraphicsEntity : this.snowflakes)
        {

            if(drawableGraphicsEntity.getPosition().y < (float)(Math.random()*0.25f) || isOnRoof(drawableGraphicsEntity)) // If snowflake hits ground or hits the house roof, reset
                resetSnowFlake(drawableGraphicsEntity);

            // Update snowflake
            drawableGraphicsEntity.setPosition(new Vector2(drawableGraphicsEntity.getPosition().x, drawableGraphicsEntity.getPosition().y-(3f*delta)));
            drawableGraphicsEntity.setAngle(drawableGraphicsEntity.getAngle() + (50f*delta*(drawableGraphicsEntity.getAngle() < 180 ? -1 : 1)));
            drawableGraphicsEntity.update(delta);
        }
    }

    public void draw(SpriteBatch spriteBatch)
    {
        for(DrawableGraphicsEntity drawableGraphicsEntity : this.snowflakes)
        {
            // Draw snowflake
            drawableGraphicsEntity.draw(spriteBatch);
        }
    }


}
