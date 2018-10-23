package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Objective {

    private Sprite sprite;
    public Polygon polygon;

    private float health = 100.0f;

    float width;
    float height;

    public Objective()
    {
        this.sprite = new Sprite(Environment.assets.get("maps/night_map/night_map.atlas", TextureAtlas.class).findRegion("house"));
        this.sprite.setPosition(2496, 90f);
    }

    public void draw(SpriteBatch batch)
    {
        this.sprite.draw(batch);
    }

    public void setPosition(float x, float y){
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
        pos.y = Environment.gameCamera.position.y*2 - pos.y;
        this.polygon.setPosition(pos.x, pos.y);
    }

    public Vector2 getPosition(){
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(polygon.getX(), polygon.getY(), 0)));
        pos.y = Environment.physicsCamera.position.y*2 - pos.y;
        return new Vector2(pos.x, pos.y);
    }

    public void takeDamage(float damage){
        health -= damage*Environment.difficulty_multipliers.get(Environment.currentDifficulty);
        if(this.health <= 0)
        {
            this.sprite.setRegion(Environment.assets.get("maps/night_map/night_map.atlas", TextureAtlas.class).findRegion("rubble"));
            this.sprite.setSize(this.sprite.getRegionWidth(), this.sprite.getRegionHeight());
            this.sprite.setPosition(this.sprite.getX(), 30f);
        }
    }

    public float getHealth(){return health;}

    public float getWidth(){
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(width, 0, 0)));
        return pos.x;
    }

    public float getHeight(){
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(0, height, 0)));
        return Environment.physicsCamera.position.y*2 - pos.y;
    }

}
