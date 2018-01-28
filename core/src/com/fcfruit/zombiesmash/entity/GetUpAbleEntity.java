package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.fcfruit.zombiesmash.entity.interfaces.GetUpAbleEntityInterface;

/**
 * Created by Lucas on 2018-01-27.
 */

public class GetUpAbleEntity implements GetUpAbleEntityInterface
{
    private double getUpTimer;
    private double timeBeforeGetup;
    private boolean isGettingUp;
    private MouseJoint getUpMouseJoint;

    public GetUpAbleEntity(Body physicsBody){

    }

    @Override
    public void update(float delta)
    {

    }

    @Override
    public void get_up()
    {

    }
}
