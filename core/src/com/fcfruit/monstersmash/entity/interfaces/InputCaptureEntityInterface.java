package com.fcfruit.monstersmash.entity.interfaces;

/**
 * Created by Lucas on 2018-03-19.
 */

public interface InputCaptureEntityInterface
{
    void onTouchDown(float screenX, float screenY, int pointer);
    void onTouchDragged(float screenX, float screenY, int pointer);
    void onTouchUp(float screenX, float screenY, int pointer);
}
