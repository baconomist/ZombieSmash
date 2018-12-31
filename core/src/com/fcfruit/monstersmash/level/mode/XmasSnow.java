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

    private boolean roofCheck = true;

    private ArrayList<Sprite> snowflakes;

    public XmasSnow()
    {
        this.snowflakes = new ArrayList<Sprite>();
    }

    public void load()
    {
        for(int i = 0; i < 35; i++)
        {
            Sprite snowFlake = new Sprite(Environment.assets.get("maps/night_map/night_map.atlas", TextureAtlas.class).findRegion("snow_1"));
            snowFlake.setScale(1.25f);
            resetSnowFlake(snowFlake);
            snowFlake.setPosition(snowFlake.getX(), snowFlake.getY() + 192f*i);
            this.snowflakes.add(snowFlake);
        }
    }

    private void resetSnowFlake(Sprite snowFlake)
    {
        snowFlake.setPosition((Environment.gameCamera.position.x - Environment.gameCamera.viewportWidth/2) + (float)Math.random()*Environment.gameCamera.viewportWidth, Environment.gameCamera.viewportHeight + (float)(Math.random() * 200 + 50));
        snowFlake.setRotation((float)Math.random()*360);
    }

    private boolean isOnRoof(Sprite snowflake)
    {
        float x = snowflake.getX();
        float y = snowflake.getY();
        y = y + 150f + (float)(Math.random()*30f); // Make snowflake disappear when it actually hits the roof rather than touches the bounding box of the house

        float min_x = Environment.level.objective.polygon.getX() + 400f;
        float poly_bound_x = Environment.level.objective.polygon.getX() + Environment.level.objective.polygon.getVertices()[2];

        return x > min_x && x < poly_bound_x && Environment.level.objective.polygon.contains(x, y);
    }

    public void setRoofCheck(boolean roofCheck)
    {
        this.roofCheck = roofCheck;
    }

    public void update(float delta)
    {
        Gdx.app.log("delta", ""+delta + " " + (3f*delta));
        for(Sprite snowFlake : this.snowflakes)
        {
            if(snowFlake.getY() < (float)(Math.random()*48f)
                    || (this.roofCheck && isOnRoof(snowFlake))) // If snowflake hits ground or hits the house roof, reset
                resetSnowFlake(snowFlake);

            // Update snowflake
            snowFlake.setPosition(snowFlake.getX(), snowFlake.getY()-(300f*delta));
            snowFlake.setRotation(snowFlake.getRotation() + (50f*delta*(snowFlake.getRotation() < 180 ? -1 : 1)));
        }
    }

    public void draw(SpriteBatch spriteBatch)
    {
        for(Sprite snowFlake : this.snowflakes)
        {
            // Draw snowflake
            snowFlake.draw(spriteBatch);
        }
    }


}
