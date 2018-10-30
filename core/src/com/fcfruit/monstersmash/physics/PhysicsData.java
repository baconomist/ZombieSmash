package com.fcfruit.monstersmash.physics;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Lucas on 2018-07-30.
 */

public class PhysicsData
{
    private Array data;
    public PhysicsData(Object o)
    {
        this.data = new Array();
        this.data.add(o);
    }

    public void add_data(Object o)
    {
        this.data.add(o);
    }

    public boolean containsInstanceOf(Class instanceType)
    {
        for(Object o : this.data)
        {
            if(instanceType.isInstance(o))
                return true;
        }
        return false;
    }

    public Object getClassInstance(Class instanceType)
    {
        for(Object o : this.data)
        {
            if(instanceType.isInstance(o))
                return o;
        }
        return null;
    }

    public Array getData()
    {
        return this.data;
    }

}
