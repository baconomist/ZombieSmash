package com.fcfruit.zombiesmash.release.entity.interfaces;

import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface ContainerEntityInterface
{
    void detach(com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface detachableEntityInterface);

    void setDrawableEntities(HashMap<String, DrawableEntityInterface> drawableEntities);
    void setInteractiveEntities(HashMap<String, InteractiveEntityInterface> interactiveEntities);
    void setDetachableEntities(HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface> detachableEntities);

    HashMap<String, DrawableEntityInterface> getDrawableEntities();
    HashMap<String, InteractiveEntityInterface> getInteractiveEntities();
    HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface> getDetachableEntities();

}
