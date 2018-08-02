package com.fcfruit.zombiesmash.entity;

import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;

import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-25.
 */

public class ContainerEntity implements ContainerEntityInterface
{
    private HashMap<String, com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface> drawableEntities;
    private HashMap<String, InteractiveEntityInterface> interactiveEntities;
    private HashMap<String, DetachableEntityInterface> detachableEntities;
    private HashMap<String, ContainerEntityInterface> containerEntities;


    public ContainerEntity()
    {
        this.drawableEntities = new HashMap<String, com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, DetachableEntityInterface>();
        this.containerEntities = new HashMap<String, ContainerEntityInterface>();
    }

    @Override
    public HashMap<String, com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface> getDrawableEntities()
    {
        return this.drawableEntities;
    }

    @Override
    public HashMap<String, InteractiveEntityInterface> getInteractiveEntities()
    {
        return this.interactiveEntities;
    }

    @Override
    public HashMap<String, DetachableEntityInterface> getDetachableEntities()
    {
        return this.detachableEntities;
    }

    @Override
    public void setDrawableEntities(HashMap<String, com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface> drawableEntities)
    {
        this.drawableEntities = drawableEntities;
    }

    @Override
    public void setInteractiveEntities(HashMap<String, InteractiveEntityInterface> interactiveEntities)
    {
        this.interactiveEntities = interactiveEntities;
    }

    @Override
    public void setDetachableEntities(HashMap<String, DetachableEntityInterface> detachableEntities)
    {
        this.detachableEntities = detachableEntities;
    }

    @Override
    public void detach(DetachableEntityInterface detachableEntity)
    {

        String key = "";

        for (String k : this.detachableEntities.keySet())
        {
            if (this.detachableEntities.get(k) == detachableEntity)
            {
                key = k;
                break;
            }
        }

        this.drawableEntities.remove(key);
        this.interactiveEntities.remove(key);
        this.containerEntities.remove(key);

    }


}
