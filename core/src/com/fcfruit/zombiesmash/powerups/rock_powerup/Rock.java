package com.fcfruit.zombiesmash.powerups.rock_powerup;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.zombies.NewZombie;

/**
 * Created by Lucas on 2017-12-02.
 */

public class Rock implements DrawableEntityInterface, PhysicsEntityInterface
{

    private DrawablePhysicsEntity drawablePhysicsEntity;
    private Polygon polygon;

    private boolean isFalling;

    public Rock()
    {

        RubeSceneLoader loader = new RubeSceneLoader(Environment.physics.getWorld());
        RubeScene scene = loader.loadScene(Gdx.files.internal("powerups/rock/rock_rube.json"));

        Body body = scene.getBodies().get(0);
        body.setUserData(this);
        for (Fixture fixture : body.getFixtureList())
        {
            fixture.setUserData(this);
        }

        Sprite sprite = new Sprite(new Texture(Gdx.files.internal("powerups/rock/rock.png")));
        sprite.setSize(scene.getImages().get(0).width * Physics.PIXELS_PER_METER, scene.getImages().get(0).height * Physics.PIXELS_PER_METER);
        sprite.setOriginCenter();

        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, body);


        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;

        this.polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);

    }

    @Override
    public void update(float delta)
    {
        this.drawablePhysicsEntity.update(delta);

        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition(), 0)));
        pos.y = Environment.gameCamera.viewportHeight - pos.y;
        // Center the this.polygon on physics body
        this.polygon.setPosition(pos.x - (this.polygon.getVertices()[2] / 2), pos.y - (this.polygon.getVertices()[5] / 2));
        this.polygon.setRotation(this.getAngle());

        this.isFalling = this.getPosition().y > 0.5f;

        for (DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
        {
            if (drawableEntityInterface instanceof NewZombie)
                for (InteractiveEntityInterface interactiveEntityInterface : ((NewZombie) drawableEntityInterface).getInteractiveEntities().values())
                {
                    if(Environment.areQuadrilaterallsColiding(interactiveEntityInterface.getPolygon(), this.polygon) && this.isFalling)
                    {

                        ((OptimizableEntityInterface) drawableEntityInterface).disable_optimization();
                    }

                }
        }

    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawablePhysicsEntity.draw(batch);
    }


    @Override
    public Vector2 getPosition()
    {
        return this.drawablePhysicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.drawablePhysicsEntity.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.drawablePhysicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.drawablePhysicsEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize()
    {
        return this.drawablePhysicsEntity.getSize();
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.drawablePhysicsEntity.getPhysicsBody();
    }

    @Override
    public void dispose()
    {
        this.drawablePhysicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
