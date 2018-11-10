package com.fcfruit.monstersmash.level.mode;

public class SandboxLevel extends Level
{
    @Override
    public int getBrainCount()
    {
        return 0;
    }

    @Override
    public int getLevelId()
    {
        return 0;
    }

    @Override
    public boolean isCameraMoving()
    {
        return false;
    }

    @Override
    public void incrementBrainCount(int amount)
    {

    }
}
