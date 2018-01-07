package com.fcfruit.zombiesmash.entity;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface DetachableEntityInterface
{
    void detach();
    void setState(String state);
    String getState();
}
