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
    private DrawableGraphicsEntity box;
    private String content;

    private static float WIDTH = 2.5f;
    private static float HEIGHT = 2.5f;

    private GlyphLayout layout;

    public Message()
    {
        this.bitmapFont = new BitmapFont(Gdx.files.internal("gui/defaultSkin/default.fnt"));
        this.bitmapFont.getData().setScale(4);
        this.box = new DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("gui/game_ui/message_box.png"))));
        this.box.getSprite().setSize(WIDTH* Physics.PIXELS_PER_METER, HEIGHT*Physics.PIXELS_PER_METER);
        this.box.getSprite().setOriginCenter();

        this.layout = new GlyphLayout(); //dont do this every frame! Store it as member
    }

    public void setContent(String content)
    {
        this.content = content;
        this.layout.setText(this.bitmapFont, content);
        this.box.getSprite().setSize(this.layout.width, this.layout.height);
    }

    public void display()
    {
        //Environment.game.pause somehow();
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.box.setPosition(new Vector2(Environment.physicsCamera.position.x-this.box.getSize().x/2, Environment.physicsCamera.position.y-this.box.getSize().y/2));
        this.box.draw(batch);
        this.bitmapFont.draw(batch, this.content, Environment.gameCamera.position.x-this.layout.width/2, Environment.gameCamera.position.y + layout.height/2);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

    @Override
    public void update(float delta)
    {
        this.box.update(delta);
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
        //Environment.level.resume somehow();
        Environment.drawableRemoveQueue.add(this);
        this.box.dispose();
        this.bitmapFont.dispose();
    }

    @Override
    public Vector2 getPosition()
    {
        return this.box.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {

    }

    @Override
    public float getAngle()
    {
        return 0;
    }

    @Override
    public void setAngle(float angle)
    {

    }

    @Override
    public float getAlpha()
    {
        return 0;
    }

    @Override
    public void setAlpha(float alpha)
    {

    }

    @Override
    public Vector2 getSize()
    {
        return new Vector2(WIDTH, HEIGHT);
    }

    @Override
    public void dispose()
    {

    }
}
