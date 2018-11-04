package com.fcfruit.monstersmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.monstersmash.entity.DrawableGraphicsEntity;
import com.fcfruit.monstersmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.physics.Physics;

public class Message implements DrawableEntityInterface, InputCaptureEntityInterface
{
    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private BitmapFont bitmapFont;
    private Sprite box;
    private String content;

    private static String CONTINUE_STRING = "Tap to Continue...";
    private static float WIDTH = 2.5f;
    private static float HEIGHT = 2.5f;
    private static float TEXT_OFFSET = 50;

    private GlyphLayout layout;
    private GlyphLayout continueLayout;

    private int pointer = -1;

    public Message()
    {
        TextureAtlas atlas = Environment.assets.get("ui/game_ui/message_box/message_guy/message_guy.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.5f); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("ui/game_ui/message_box/message_guy/message_guy.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation("talking");
        this.animatableGraphicsEntity.getState().addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                super.complete(entry);
                if(entry.getAnimation().getName().equals("talking"))
                    animatableGraphicsEntity.setAnimation("blinking");
            }
        });

        this.bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));

        this.box = new Sprite(new Texture(Gdx.files.internal("ui/game_ui/message_box/message_box.png")));
        this.box.setSize(WIDTH*Physics.PIXELS_PER_METER, HEIGHT*Physics.PIXELS_PER_METER);
        this.box.setOriginCenter();

        this.layout = new GlyphLayout();
        this.continueLayout = new GlyphLayout();
        this.continueLayout.setText(this.bitmapFont, CONTINUE_STRING);
    }

    public void setContent(String content)
    {
        this.content = content;
        this.layout.setText(this.bitmapFont, content);
        this.box.setSize(Math.max(this.layout.width, this.continueLayout.width  + (this.animatableGraphicsEntity.getSize().x*Physics.PIXELS_PER_METER)) + TEXT_OFFSET*2,
                this.layout.height + TEXT_OFFSET*2 + (this.animatableGraphicsEntity.getSize().y*Physics.PIXELS_PER_METER)); // Update size to accommodate for CONTINUE_TEXT
        this.box.setOriginCenter();
    }

    public void display()
    {
        Environment.isPaused = true;
    }

    @Override
    public void draw(SpriteBatch batch)
    {

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.box.draw(batch);
        // Message content
        this.bitmapFont.draw(batch, this.content, this.box.getX() + TEXT_OFFSET,
                this.box.getY() + TEXT_OFFSET + (this.animatableGraphicsEntity.getSize().y*Physics.PIXELS_PER_METER) + layout.height);
        // Tap to Continue...
        this.bitmapFont.draw(batch, CONTINUE_STRING, this.box.getX() + (this.animatableGraphicsEntity.getSize().x*Physics.PIXELS_PER_METER),
                this.box.getY() + TEXT_OFFSET + this.animatableGraphicsEntity.getSize().y*Physics.PIXELS_PER_METER/2);

        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.box.setPosition(Environment.screens.gamescreen.get_ui_stage().getViewport().getCamera().position.x-this.box.getWidth()/2, Environment.screens.gamescreen.get_ui_stage().getViewport().getCamera().position.y-this.box.getHeight()/2);

        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.box.getX(), this.box.getY(), 0)));
        pos.y = Environment.physicsCamera.position.y*2 - pos.y;
        this.animatableGraphicsEntity.setPosition(new Vector2(pos.x + this.animatableGraphicsEntity.getSize().x/2, pos.y));
        this.animatableGraphicsEntity.update(delta);
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        this.pointer = pointer;
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        if(this.pointer == pointer)
        {
            Environment.isPaused = false;
            Environment.screens.gamescreen.get_ui_stage().removeMessage(this);
            this.dispose();
        }
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
