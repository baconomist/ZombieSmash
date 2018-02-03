package com.fcfruit.zombiesmash.entity.interfaces;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-02.
 */

public interface DetachableEntityParentInterface
{
    void removeChild(DetachableEntityInterface detachableEntity);
    void addChild(DetachableEntityInterface detachableEntity);
    ArrayList<DetachableEntityInterface> getChildren();
}
