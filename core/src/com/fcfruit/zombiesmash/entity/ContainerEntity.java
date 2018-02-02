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
    private HashMap<String, DrawableEntityInterface> drawableEntities;
    private HashMap<String, InteractiveEntityInterface> interactiveEntities;
    private HashMap<String, DetachableEntityInterface> detachableEntities;
    private HashMap<String, ContainerEntityInterface> containerEntities;

    private ContainerEntityInterface parent;


    public ContainerEntity()
    {
        this.drawableEntities = new HashMap<String, DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, DetachableEntityInterface>();
        this.containerEntities = new HashMap<String, ContainerEntityInterface>();
    }

    public ContainerEntity(ContainerEntityInterface parent)
    {
        this.parent = parent;

        this.drawableEntities = new HashMap<String, DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, DetachableEntityInterface>();
        this.containerEntities = new HashMap<String, ContainerEntityInterface>();
    }


    @Override
    public HashMap<String, DrawableEntityInterface> getDrawableEntities()
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
    public HashMap<String, ContainerEntityInterface> getContainerEntities()
    {
        return this.containerEntities;
    }

    @Override
    public void setDrawableEntities(HashMap<String, DrawableEntityInterface> drawableEntities)
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
    public void setContainerEntities(HashMap<String, ContainerEntityInterface> containerEntities)
    {
        this.containerEntities = containerEntities;
    }

    @Override
    public ContainerEntityInterface getParent()
    {
        return this.parent;
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
