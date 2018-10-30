package com.fcfruit.monstersmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ImageButton extends Actor
{
    private Sprite currentImage;

    private Sprite defaultImage;
    private Sprite imageDown;
    private Sprite imageUp;

    public ImageButton(Sprite sprite)
    {
        this.defaultImage = sprite;
        this.currentImage = this.defaultImage;
        this.setSize(this.currentImage.getWidth(), this.currentImage.getHeight());
        this.onImageChange();

        this.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                onTouchDown();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onTouchUp();
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    private void onTouchDown()
    {
        if(this.imageDown != null)
        {
            this.currentImage = this.imageDown;
            this.onImageChange();
        }
    }

    private void onTouchUp()
    {
        if(this.imageUp != null)
        {
            this.currentImage = this.imageUp;
            this.onImageChange();
        } else
        {
            this.currentImage = this.defaultImage;
            this.onImageChange();
        }
    }

    private void onImageChange()
    {
        this.currentImage.setPosition(super.getX(), super.getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        this.currentImage.draw(batch, parentAlpha);
    }

    public void setImageDown(Sprite sprite)
    {
        sprite.setSize(this.getWidth(), this.getHeight());
        this.imageDown = sprite;
    }

    public void setImageUp(Sprite sprite)
    {
        sprite.setSize(this.getWidth(), this.getHeight());
        this.imageUp = sprite;
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.currentImage.setPosition(x, y);
        this.setBounds(x, y, this.getWidth(), this.getHeight());
    }

    @Override
    public void setSize(float width, float height)
    {
        super.setSize(width, height);
        this.currentImage.setSize(width, height);
        this.setBounds(this.getX(), this.getY(), width, height);
    }

    @Override
    public void setRotation(float degrees)
    {
        super.setRotation(degrees);
        this.currentImage.setRotation(degrees);
    }
}
