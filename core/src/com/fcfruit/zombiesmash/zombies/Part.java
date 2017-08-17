package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.PartPhysics;

/**
 * Created by Lucas on 2017-08-01.
 */

public class Part extends Sprite {

    private ZombieBody body;

    private PartPhysics physics;

    public float mass;

    public Vector2 attachementPoint;

    public boolean isAttached;

    public boolean isPhysicsEnabled;

    public boolean isGravityEnabled;

    public boolean isRagdollEnabled;

    public Part(Texture t, ZombieBody b){
        super(t);

        body = b;

        physics = new PartPhysics(this);

        mass = 0.5f;

        isAttached = true;

        isPhysicsEnabled = true;

        isGravityEnabled = false;

        isRagdollEnabled = true;

    }

    public void update(float delta){
        physics.update(delta);
    }

    public void setScale(){

    }

    public void setAttachementPoint(Vector2 atchmntpnt){
        atchmntpnt.x = atchmntpnt.x + this.getX();
        atchmntpnt.y = atchmntpnt.y + this.getY();

        attachementPoint = atchmntpnt;
    }

}
