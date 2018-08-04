package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface OptimizableEntityInterface
{
    void force_instant_optimize();
    void enable_optimization();
    void disable_optimization();
    boolean isOptimizationEnabled();
}
