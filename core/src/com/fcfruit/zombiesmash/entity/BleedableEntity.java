package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.entity.interfaces.BleedableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-02.
 */

public class BleedableEntity implements BleedableEntityInterface
{

    private DetachableEntityInterface detachableEntity;

    public BleedableEntity(DetachableEntityInterface detachableEntity)
    {
        this.detachableEntity = detachableEntity;

    }

    @Override
    public void update(float delta)
    {

    }
}
