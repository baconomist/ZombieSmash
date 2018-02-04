package com.fcfruit.zombiesmash.entity;

import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityParentInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-02.
 */

public class DetachableParentEntity implements DetachableEntityParentInterface
{
    private ArrayList<DetachableEntityInterface> children;

    public DetachableParentEntity()
    {
        this.children = new ArrayList<DetachableEntityInterface>();
    }

    @Override
    public void removeChild(DetachableEntityInterface detachableEntity)
    {
        if(this.children.contains(detachableEntity)){
            this.children.remove(detachableEntity);
        }
    }

    @Override
    public void addChild(DetachableEntityInterface detachableEntity)
    {
        if(!this.children.contains(detachableEntity)){
            this.children.add(detachableEntity);
        }
    }

    @Override
    public ArrayList<DetachableEntityInterface> getChildren()
    {
        return this.children;
    }
}
