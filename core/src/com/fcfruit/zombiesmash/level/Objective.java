package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Objective {

    public Polygon polygon;

    float health = 100;

    float width;
    float height;

    public void setPosition(float x, float y){
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
        this.polygon.setPosition(pos.x, Environment.gameCamera.position.y*2 - pos.y);
    }

    public Vector2 getPosition(){
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(polygon.getX(), polygon.getY(), 0)));
        return new Vector2(pos.x, pos.y);
    }

    public void takeDamage(float damage){
        health -= damage;
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
