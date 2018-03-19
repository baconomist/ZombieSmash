package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.zombies.NewZombie;

/**
 * Created by Lucas on 2018-02-13.
 */

public class ParticleEntity
{
    public int blastPower = 100;

    public Body physicsBody;
    public Fixture fixture;

    private Vector2 initialPos;

    public ParticleEntity(World world, Vector2 particlePos, Vector2 rayDir, float NUMRAYS)
    {
        this.initialPos = particlePos;

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true; // rotation not necessary
        bd.bullet = true; // prevent tunneling at high speed
        bd.linearDamping = 10; // drag due to moving through air
        bd.gravityScale = 0; // ignore gravity
        bd.position.x = particlePos.x;
        bd.position.y = particlePos.y;// start at blast center
        rayDir.scl(blastPower);
        bd.linearVelocity.x = rayDir.x*blastPower;
        bd.linearVelocity.y = rayDir.y*blastPower;
        physicsBody = world.createBody(bd);
        //create a reference to this class in the body(this allows us to loop through the world bodies and check if the body is an Explosion particle)
        physicsBody.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.05f); // very small

        FixtureDef fd = new FixtureDef();
        fd.shape = circleShape;
        fd.density = 120 / NUMRAYS; // very high - shared across all particles
        fd.friction = 0; // friction not necessary
        fd.restitution = 0.99f; // high restitution to reflect off obstacles
        //fd.filter.groupIndex = -1; // particles should not collide with each other

        this.fixture = physicsBody.createFixture(fd);
        this.fixture.setUserData(this);

    }

    public void update(float delta)
    {
        for (DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
        {
            if (drawableEntity instanceof InteractiveEntityInterface && drawableEntity instanceof OptimizableEntityInterface)
            {

                Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.physicsBody.getPosition(), 0)));
                if (((InteractiveEntityInterface) drawableEntity).getPolygon().contains(pos.x, Environment.gameCamera.viewportHeight - pos.y))
                {
                    ((OptimizableEntityInterface)drawableEntity).disable_optimization();

                    if(drawableEntity instanceof NewZombie)
                    {
                        ((NewZombie) drawableEntity).stopGetUp();
                        ((PhysicsEntityInterface) ((NewZombie) drawableEntity).getDrawableEntities().get("torso")).getPhysicsBody().applyLinearImpulse(new Vector2(1, 1), this.initialPos, true);
                    }
                    
                }
            }
        }
    }

}
