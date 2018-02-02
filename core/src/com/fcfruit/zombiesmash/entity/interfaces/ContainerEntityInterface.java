package com.fcfruit.zombiesmash.entity.interfaces;

import com.fcfruit.zombiesmash.entity.ContainerEntity;

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
    void setContainerEntities(HashMap<String, ContainerEntityInterface> containerEntities);

    HashMap<String, DrawableEntityInterface> getDrawableEntities();
    HashMap<String, InteractiveEntityInterface> getInteractiveEntities();
    HashMap<String, DetachableEntityInterface> getDetachableEntities();
    HashMap<String, ContainerEntityInterface> getContainerEntities();

    ContainerEntityInterface getParent();


}
