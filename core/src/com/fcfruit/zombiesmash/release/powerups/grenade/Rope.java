package com.fcfruit.zombiesmash.release.powerups.grenade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.fcfruit.zombiesmash.release.Environment;
import com.fcfruit.zombiesmash.release.entity.DrawablePhysicsEntity;

/**
 * Created by Lucas on 2018-03-22.
 */

public class Rope
{
    private Body[] segments;
    private RevoluteJoint[] joints;
    private RopeJoint[] ropeJoints;
    private DrawablePhysicsEntity[] drawablePhysicsEntities;

    private RevoluteJoint bodyJointTop;
    private RevoluteJoint bodyJointBottom;

    private float width = 0.2f, height = 0.3f;

    public Rope(int length, Vector2 position)
    {

        this.segments = new Body[length];
        this.drawablePhysicsEntities = new DrawablePhysicsEntity[length];
        this.joints = new RevoluteJoint[length - 1];
        this.ropeJoints = new RopeJoint[length - 1];

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        for (int i = 0; i < length; i++)
        {
            this.segments[i] = Environment.physics.createBody(bodyDef);
            this.segments[i].createFixture(shape, 2).setUserData(this);

            this.drawablePhysicsEntities[i] = new DrawablePhysicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/grenade/rope_segment.png"))), this.segments[i]);
            Environment.drawableAddQueue.add(this.drawablePhysicsEntities[i]);
        }

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.localAnchorA.y = -height / 2;
        jointDef.localAnchorB.y = height / 2;

        for (int i = 0; i < joints.length; i++)
        {
            jointDef.bodyA = this.segments[i];
            jointDef.bodyB = this.segments[i + 1];
            this.joints[i] = (RevoluteJoint) Environment.physics.createJoint(jointDef);
        }

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.localAnchorA.set(0, -height / 2);
        ropeJointDef.localAnchorB.set(0, height / 2);
        ropeJointDef.maxLength = height;

        for (int i = 0; i < ropeJoints.length; i++)
        {
            ropeJointDef.bodyA = this.segments[i];
            ropeJointDef.bodyB = this.segments[i + 1];
            this.ropeJoints[i] = (RopeJoint) Environment.physics.createJoint(ropeJointDef);
        }

    }

    public RevoluteJoint attachToTop(Body body){
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.localAnchorA.y = height / 2;
        jointDef.localAnchorB.y = 0;

        jointDef.bodyA = this.segments[0];
        jointDef.bodyB = body;

        this.bodyJointTop = (RevoluteJoint) Environment.physics.createJoint(jointDef);

        return this.bodyJointTop;

    }

    public RevoluteJoint attachToBottom(Body body){
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.localAnchorA.y = -height / 2;
        jointDef.localAnchorB.y = 0;

        jointDef.bodyA = this.segments[this.segments.length - 1];
        jointDef.bodyB = body;
        this.bodyJointBottom = (RevoluteJoint) Environment.physics.createJoint(jointDef);

        return this.bodyJointBottom;

    }





}
