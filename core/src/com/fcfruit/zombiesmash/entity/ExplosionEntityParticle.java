package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Lucas on 2018-02-13.
 */

public class ExplosionEntityParticle
{
    public int blastPower = 100;
    public static final int NUMRAYS = 10;
    public Body body;

    public ExplosionEntityParticle(World world, Vector2 particlePos, Vector2 rayDir){
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true; // rotation not necessary
        bd.bullet = true; // prevent tunneling at high speed
        bd.linearDamping = 10; // drag due to moving through air
        bd.gravityScale = 0; // ignore gravity
        bd.position.x = particlePos.x;
        bd.position.y = particlePos.y;// start at blast center
        rayDir.scl(blastPower);
        bd.linearVelocity.x = rayDir.x;
        bd.linearVelocity.y = rayDir.y;
        body = world.createBody( bd );
        //create a reference to this class in the body(this allows us to loop through the world bodies and check if the body is an Explosion particle)
        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.05f); // very small

        FixtureDef fd = new FixtureDef();
        fd.shape = circleShape;
        fd.density = 120 / (float)NUMRAYS; // very high - shared across all particles
        fd.friction = 0; // friction not necessary
        fd.restitution = 0.99f; // high restitution to reflect off obstacles
        fd.filter.groupIndex = -1; // particles should not collide with each other

        Fixture fixture = body.createFixture( fd );
        fixture.setUserData(this);

    }
}
