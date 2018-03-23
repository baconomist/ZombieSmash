package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.entity.ContainerEntity;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface DetachableEntityInterface extends StateEntityInterface
{
    void detach();
    boolean shouldDetach();
    ContainerEntityInterface getContainer();
    ArrayList<Joint> getJoints();
    void setForceForDetach(float force);
}
