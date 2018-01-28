package com.fcfruit.zombiesmash.entity;

import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;

import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-25.
 */

public class ContainerEntity implements ContainerEntityInterface
{
    public HashMap<String, DrawableEntityInterface> drawableEntities;
    public HashMap<String, InteractiveEntityInterface> interactiveEntities;
    public HashMap<String, DetachableEntityInterface> detachableEntities;


    public ContainerEntity()
    {
        this.drawableEntities = new HashMap<String, DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, DetachableEntityInterface>();
    }


    @Override
    public void detach(DetachableEntityInterface detachableEntityInterface)
    {
        String key = "";

        for (String k : this.detachableEntities.keySet())
        {
            if (this.detachableEntities.get(k) == detachableEntityInterface)
            {
                key = k;
                break;
            }
        }

        this.detachableEntities.remove(key);

        if (detachableEntityInterface instanceof InteractiveEntityInterface)
        {
            this.interactiveEntities.remove(key);
        }

        if (detachableEntityInterface instanceof DrawableEntityInterface)
        {
            this.drawableEntities.remove(key);
        }
    }
}
