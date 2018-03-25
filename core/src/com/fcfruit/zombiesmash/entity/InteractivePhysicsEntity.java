package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;

/**
 * Created by lucas on 2018-01-07.
 */

public class InteractivePhysicsEntity implements InteractivePhysicsEntityInterface
{

    private ContainerEntityInterface containerEntity;

    private boolean isTouching;
    private boolean powerfulJoint;

    private int pointer;

    private Body physicsBody;
    private Polygon polygon;
    private MouseJoint mouseJoint;

    public InteractivePhysicsEntity(Body physicsBody, Polygon polygon)
    {
        this.isTouching = false;
        this.pointer = -1;

        this.physicsBody = physicsBody;
        this.polygon = polygon;

        this.powerfulJoint = true;
    }

    public InteractivePhysicsEntity(Body physicsBody, Polygon polygon, ContainerEntityInterface containerEntity)
    {
        this.containerEntity = containerEntity;

        this.isTouching = false;
        this.pointer = -1;

        this.physicsBody = physicsBody;
        this.polygon = polygon;

        this.powerfulJoint = true;
    }


    public void update(float delta)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(physicsBody.getPosition(), 0)));
        pos.y = Environment.gameCamera.viewportHeight - pos.y;
        // Center the polygon on physics body
        polygon.setPosition(pos.x - (polygon.getVertices()[2] / 2), pos.y - (polygon.getVertices()[5] / 2));
        polygon.setRotation((float) Math.toDegrees(physicsBody.getAngle()));

    }

    void createMouseJoint(float x, float y)
    {

        /*
        To make dragging faster:

        myObjectBody.applyForceToCenter(new Vector2((float)Math.cos(myMouseDirectionAngle)
        forceYouWantToApply, (float)Math.sin(myMouseDirectionAngle) * forceYouWantToApply));
        */


        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = Environment.physics.getGroundBodies().get(0);
        mouseJointDef.bodyB = this.physicsBody;
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(this.physicsBody.getPosition());

        // Makes the joint moveBy body faster
        //mouseJointDef.frequencyHz = 10;

        // Idk what this does, may want to play with it
        // Default is 0.7, I do know that it makes things
        // A lot slower for the mousejoint though when you set it to 10
        //mouseJointDef.dampingRatio = 10;

        // Makes the joint moveBy body faster
        mouseJointDef.dampingRatio = 0.1f;

        if (this.powerfulJoint)
        {
            // Force applied to body to get to point
            mouseJointDef.maxForce = 10000f * this.physicsBody.getMass();

        } else
        {
            // Force applied to body to get to point
            mouseJointDef.maxForce = 100f * this.physicsBody.getMass();
        }

        // Destroy the current mouseJoint
        if (mouseJoint != null)
        {
            Environment.physics.destroyJoint(mouseJoint);
        }
        mouseJoint = (MouseJoint) Environment.physics.createJoint(mouseJointDef);
        mouseJoint.setTarget(new Vector2(x, y));
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));

        if (Environment.touchedDownItems.size() < 1 && this.polygon.contains(pos.x, pos.y) && mouseJoint == null)
        {
            this.pointer = p;
            this.isTouching = true;
            Environment.touchedDownItems.add(this);

            pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
            createMouseJoint(pos.x, pos.y);
        }
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int p)
    {
        Vector3 pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        if (mouseJoint != null && pointer == p)
        {
            mouseJoint.setTarget(new Vector2(pos.x, pos.y));
            if (this.powerfulJoint)
            {
                mouseJoint.setMaxForce(10000f * physicsBody.getMass());
            }
        }

    }

    @Override
    public void onTouchUp(float screenX, float screenY, int p)
    {
        if (mouseJoint != null && pointer == p)
        {
            Environment.physics.destroyJoint(mouseJoint);
            mouseJoint = null;
            isTouching = false;
            pointer = -1;
        }
    }

    @Override
    public void overrideTouching(boolean touching, float screenX, float screenY, int p)
    {
        if (touching)
        {
            this.pointer = p;
            this.isTouching = true;
            Environment.touchedDownItems.add(this);

            Vector3 pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
            createMouseJoint(pos.x, pos.y);
        } else
        {
            Environment.physics.destroyJoint(mouseJoint);
            mouseJoint = null;
            isTouching = false;
            pointer = -1;
        }
        this.isTouching = touching;
    }

    @Override
    public boolean isTouching()
    {
        return isTouching;
    }

    @Override
    public Polygon getPolygon()
    {
        return this.polygon;
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.physicsBody;
    }

    @Override
    public boolean isUsingPowerfulJoint()
    {
        return powerfulJoint;
    }

    @Override
    public void setUsingPowerfulJoint(boolean usingPowerfulJoint)
    {
        this.powerfulJoint = usingPowerfulJoint;
    }

}
