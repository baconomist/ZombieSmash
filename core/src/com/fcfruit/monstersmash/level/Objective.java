package com.fcfruit.monstersmash.level;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.monstersmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Objective {

    protected Sprite sprite;
    public Polygon polygon;

    public Polygon[] attack_zones;

    private float health = 100.0f;

    float width;
    float height;

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

    public Vector2 getAttackZonePosition(int attack_zone)
    {
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(Environment.level.objective.attack_zones[attack_zone].getX(),
                Environment.level.objective.attack_zones[attack_zone].getX(), 0)));
        pos.y = Environment.physicsCamera.position.y*2 - pos.y;
        return new Vector2(pos.x, pos.y);
    }

    public Vector2 getAttackZoneSize(int attack_zone)
    {
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(Environment.level.objective.attack_zones[attack_zone].getVertices()[2],
                Environment.level.objective.attack_zones[attack_zone].getVertices()[5], 0)));
        pos.y = Environment.physicsCamera.position.y*2 - pos.y;
        return new Vector2(pos.x, pos.y);
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
