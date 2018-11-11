package com.fcfruit.monstersmash.level.mode;

public class SandboxLevel extends Level
{
    private boolean isCameraMoving = false;

    public void setCameraMoving(boolean moving)
    {
        this.isCameraMoving = moving;
    }

    @Override
    public boolean isCameraMoving()
    {
        return this.isCameraMoving;
    }

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
    public void incrementBrainCount(int amount)
    {

    }
}
