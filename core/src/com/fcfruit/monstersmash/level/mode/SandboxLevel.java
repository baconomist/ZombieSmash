package com.fcfruit.monstersmash.level.mode;

import com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener;

public class SandboxLevel extends Level
{
    private boolean isCameraMoving = false;

    public void setCameraMoving(boolean moving)
    {
        this.isCameraMoving = moving;

        if(isCameraMoving)
        {
            for (LevelEventListener levelEventListener : this.levelEventListeners)
            {
                levelEventListener.onCameraMoved();
            }
        }
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
