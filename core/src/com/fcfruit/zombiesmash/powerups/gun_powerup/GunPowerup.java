package com.fcfruit.zombiesmash.powerups.gun_powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerUpInterface;

/**
 * Created by Lucas on 2018-03-19.
 */

public class GunPowerup implements PowerUpInterface, InputCaptureEntityInterface
{

    private Sprite ui_image;

    protected DrawableGraphicsEntity[] guns;

    private boolean isActive;
    private boolean isTerminated;

    private double duration;
    private double durationTimer;

    private int pointer;
    private int currentControllingGun;

    public GunPowerup(Sprite ui_image)
    {
        this.ui_image = ui_image;
        this.duration = 1000000000;
        this.currentControllingGun = -1;
    }

    @Override
    public void update(float delta)
    {

        if (System.currentTimeMillis() - this.durationTimer >= this.duration)
        {
            for (int i = 0; i < this.guns.length; i++)
            {
                Environment.drawableRemoveQueue.add(this.guns[i]);
                this.guns[i].dispose();
            }
            this.dispose();
        }
    }

    @Override
    public void activate()
    {
        Environment.level.addUpdatableEntity(this);
        Environment.level.addInputCaptureEntity(this);

        this.durationTimer = System.currentTimeMillis();
        this.isActive = true;
    }

    @Override
    public Sprite getUIDrawable()
    {
        return this.ui_image;
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        if (Environment.touchedDownItems.size() < 1)
        {
            if ((Environment.level.getCurrentCameraPosition().equals("left") && screenX > Gdx.graphics.getWidth() / 2)
                    || (Environment.level.getCurrentCameraPosition().equals("middle"))
                    || (Environment.level.getCurrentCameraPosition().equals("right") && screenX < Gdx.graphics.getWidth() / 2))
            {
                this.pointer = pointer;
                Environment.touchedDownItems.add(this);

            }

            if (Environment.level.getCurrentCameraPosition().equals("middle") && screenX > Gdx.graphics.getWidth() / 4 && screenX < Gdx.graphics.getWidth() / 2 || Environment.level.getCurrentCameraPosition().equals("left") && screenX > Gdx.graphics.getWidth() / 2)
                this.currentControllingGun = 0;
            else if (Environment.level.getCurrentCameraPosition().equals("middle") && screenX > Gdx.graphics.getWidth() / 2 && screenX < Gdx.graphics.getWidth() * 3/4)
                this.currentControllingGun = 1;
            else if (Environment.level.getCurrentCameraPosition().equals("right") && screenX < Gdx.graphics.getWidth() / 2)
                this.currentControllingGun = 0;

        }
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {
        if (pointer == this.pointer && this.currentControllingGun > -1)
        {

            Vector3 pos = Environment.physicsCamera.project(new Vector3(this.guns[this.currentControllingGun].getPosition(), 0));
            pos.y = Gdx.graphics.getHeight() - pos.y;

            float angle = (float) Math.atan2((pos.y - screenY), (pos.x - screenX));
            angle = (float) Math.toDegrees(angle);

            if (this.currentControllingGun == 0)
            {

                if (angle != 0 && angle > 90 || angle < -90)
                    this.guns[0].setAngle(-angle + 180);

                //Gdx.app.log("aaaa", "a " + angle);

            } else if (this.currentControllingGun == 1)
            {

                if (angle != 0 && angle < 90 && angle > -90)
                    this.guns[1].setAngle(-angle);

            }

        }
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        if (pointer == this.pointer)
        {
            this.currentControllingGun = -1;
            this.pointer = -1;
        }
    }

    private void dispose()
    {
    }


}
