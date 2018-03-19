package com.fcfruit.zombiesmash.entity.interfaces;

import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface ContainerEntityInterface
{
    void detach(DetachableEntityInterface detachableEntityInterface);

    void setDrawableEntities(HashMap<String, DrawableEntityInterface> drawableEntities);
    void setInteractiveEntities(HashMap<String, InteractiveEntityInterface> interactiveEntities);
    void setDetachableEntities(HashMap<String, DetachableEntityInterface> detachableEntities);

    HashMap<String, DrawableEntityInterface> getDrawableEntities();
    HashMap<String, InteractiveEntityInterface> getInteractiveEntities();
    HashMap<String, DetachableEntityInterface> getDetachableEntities();

}
