package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;

public class Message implements DrawableEntityInterface, InputCaptureEntityInterface
{
    private BitmapFont bitmapFont;
    private Sprite box;
    private String content;

    private static float WIDTH = 2.5f;
    private static float HEIGHT = 2.5f;

    private GlyphLayout layout;

    public Message()
    {
        this.bitmapFont = new BitmapFont(Gdx.files.internal("gui/defaultSkin/default.fnt"));
        this.bitmapFont.getData().setScale(2);

        this.box = new Sprite(new Texture(Gdx.files.internal("gui/game_ui/message_box.png")));
        this.box.setSize(WIDTH*Physics.PIXELS_PER_METER, HEIGHT*Physics.PIXELS_PER_METER);
        this.box.setOriginCenter();

        this.layout = new GlyphLayout(); //dont do this every frame! Store it as member
    }

    public void setContent(String content)
    {
        this.content = content;
        this.layout.setText(this.bitmapFont, content);
        this.box.setSize(this.layout.width, this.layout.height);
        this.box.setOriginCenter();
    }

    public void display()
    {
        Environment.isPaused = true;
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.box.setPosition(Environment.screens.gamescreen.get_ui_stage().getViewport().getCamera().position.x-this.box.getWidth()/2, Environment.screens.gamescreen.get_ui_stage().getViewport().getCamera().position.y-this.box.getHeight()/2);
        this.box.draw(batch);
        this.bitmapFont.draw(batch, this.content, Environment.screens.gamescreen.get_ui_stage().getViewport().getCamera().position.x-this.layout.width/2, Environment.screens.gamescreen.get_ui_stage().getViewport().getCamera().position.y + layout.height/2);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

    @Override
    public void update(float delta)
    {

    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        Environment.isPaused = false;
        Environment.screens.gamescreen.get_ui_stage().removeMessage(this);
        this.dispose();
    }

    @Override
    public Vector2 getPosition()
    {
        return new Vector2(this.box.getX(), this.box.getY());
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.box.setPosition(position.x, position.y);
    }

    @Override
    public float getAngle()
    {
        return this.box.getRotation();
    }

    @Override
    public void setAngle(float angle)
    {
        this.box.setRotation(angle);
    }

    @Override
    public float getAlpha()
    {
        return 1;
    }

    @Override
    public void setAlpha(float alpha)
    {

    }

    @Override
    public Vector2 getSize()
    {
        return new Vector2(this.box.getWidth(), this.box.getHeight());
    }

    @Override
    public void dispose()
    {
        this.box.getTexture().dispose();
        this.box = null;
        this.bitmapFont.dispose();
    }
}
