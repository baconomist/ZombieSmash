package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface OptimizableEntityInterface
{
    void update(float delta);
    void enable_optimization();
    void disable_optimization();
    boolean isOptimizationEnabled();
}
