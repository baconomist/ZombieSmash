package com.fcfruit.monstersmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CheckBox extends Actor
{
    private Sprite defaultImage;
    private Sprite checkedImage;

    private boolean checked;

    public CheckBox(Sprite defaultImage, Sprite checkedImage){

        this.defaultImage = defaultImage;
        this.checkedImage = checkedImage;
        this.onImageChange();

        this.checked = false;


        this.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onTouchUp();
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    private void onTouchUp()
    {
        this.checked = !this.checked;
    }

    private void onImageChange()
    {
        this.defaultImage.setPosition(super.getX(), super.getY());
        this.checkedImage.setPosition(super.getX(), super.getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        if(this.checked)
            this.checkedImage.draw(batch);
        else
            this.defaultImage.draw(batch);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.defaultImage.setPosition(x, y);
        this.checkedImage.setPosition(x, y);
        this.setBounds(x, y, this.getWidth(), this.getHeight());
    }

    @Override
    public void setSize(float width, float height)
    {
        super.setSize(width, height);
        this.defaultImage.setSize(width, height);
        this.checkedImage.setSize(width, height);
        this.setBounds(this.getX(), this.getY(), width, height);
    }

    @Override
    public void setRotation(float degrees)
    {
        super.setRotation(degrees);
        this.defaultImage.setRotation(degrees);
        this.checkedImage.setRotation(degrees);
    }

    public boolean isChecked()
    {
        return this.checked;
    }
}
