package com.fcfruit.monstersmash.entity.interfaces;

import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;

import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface ContainerEntityInterface
{
    void detach(com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface detachableEntityInterface);

    void setDrawableEntities(HashMap<String, com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface> drawableEntities);
    void setInteractiveEntities(HashMap<String, com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface> interactiveEntities);
    void setDetachableEntities(HashMap<String, com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface> detachableEntities);

    HashMap<String, DrawableEntityInterface> getDrawableEntities();
    HashMap<String, InteractiveEntityInterface> getInteractiveEntities();
    HashMap<String, DetachableEntityInterface> getDetachableEntities();

}
