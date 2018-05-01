package com.fcfruit.zombiesmash.release.entity;

import com.fcfruit.zombiesmash.release.entity.interfaces.InteractiveEntityInterface;

import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-25.
 */

public class ContainerEntity implements com.fcfruit.zombiesmash.release.entity.interfaces.ContainerEntityInterface
{
    private HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface> drawableEntities;
    private HashMap<String, InteractiveEntityInterface> interactiveEntities;
    private HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface> detachableEntities;
    private HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.ContainerEntityInterface> containerEntities;


    public ContainerEntity()
    {
        this.drawableEntities = new HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface>();
        this.containerEntities = new HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.ContainerEntityInterface>();
    }

    @Override
    public HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface> getDrawableEntities()
    {
        return this.drawableEntities;
    }

    @Override
    public HashMap<String, InteractiveEntityInterface> getInteractiveEntities()
    {
        return this.interactiveEntities;
    }

    @Override
    public HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface> getDetachableEntities()
    {
        return this.detachableEntities;
    }

    @Override
    public void setDrawableEntities(HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface> drawableEntities)
    {
        this.drawableEntities = drawableEntities;
    }

    @Override
    public void setInteractiveEntities(HashMap<String, InteractiveEntityInterface> interactiveEntities)
    {
        this.interactiveEntities = interactiveEntities;
    }

    @Override
    public void setDetachableEntities(HashMap<String, com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface> detachableEntities)
    {
        this.detachableEntities = detachableEntities;
    }

    @Override
    public void detach(com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface detachableEntity)
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

        this.detachableEntities.remove(key);

        if (this.drawableEntities.containsValue(detachableEntity))
        {
            this.drawableEntities.remove(key);
        }
        if (this.interactiveEntities.containsValue(detachableEntity))
        {
            this.detachableEntities.remove(key);
        }
        if (this.containerEntities.containsValue(detachableEntity))
        {
            this.containerEntities.remove(key);
        }

    }


}
