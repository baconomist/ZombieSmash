package com.fcfruit.monstersmash.level.mode;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener;
import com.fcfruit.monstersmash.zombies.Zombie;

import java.util.HashMap;

public class SandboxLevel extends Level
{
    private boolean isCameraMoving = false;

    @Override
    public void update(float delta)
    {
        super.update(delta);
    }

    private void updateTutorial()
    {

    }

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
