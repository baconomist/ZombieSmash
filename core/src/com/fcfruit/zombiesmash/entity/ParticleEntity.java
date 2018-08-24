package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2018-02-13.
 */

public class ParticleEntity
{
    public float blastPower;
    private Vector2 rayDir;

    public Body physicsBody;
    public Fixture fixture;

    private Vector2 initialPos;

    public boolean enabled = false;

    public ParticleEntity()
    {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true; // rotation not necessary
        bd.bullet = true; // prevent tunneling at high speed

        bd.gravityScale = 0; // ignore gravity

        // Keep body deactivated on creation, faster, no lag
        bd.active = false;

        this.physicsBody = Environment.physics.createBody(bd);

        //create a reference to this class in the body(this allows us to loop through the world bodies and check if the body is an Explosion particle)
        this.physicsBody.setUserData(new PhysicsData(this));

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.05f); // very small

        FixtureDef fd = new FixtureDef();
        fd.shape = circleShape;
        fd.friction = 0; // friction not necessary
        fd.restitution = 0.99f; // high restitution to reflect off obstacles
        //fd.filter.groupIndex = -1; // particles should not collide with each other

        this.fixture = this.physicsBody.createFixture(fd);
        this.fixture.setUserData(new PhysicsData(this));

    }

    public void enable(Vector2 particlePos, Vector2 rayDir, float NUMRAYS, float blastPower, float drag)
    {
        this.blastPower = blastPower;
        this.initialPos = particlePos;

        this.rayDir = rayDir;
        this.rayDir.scl(blastPower); // scale raydir to blastPower

        // Reactivate physicsBody
        this.physicsBody.setActive(true);
        
        this.physicsBody.setTransform(particlePos, this.physicsBody.getAngle());
        this.physicsBody.setLinearDamping(drag); // drag due to moving through air
        this.physicsBody.setLinearVelocity(rayDir);

        this.fixture.setDensity(120 / NUMRAYS); // very high - shared across all particles);

        this.enabled = true;
    }

    public void disable()
    {
        this.physicsBody.setActive(false);
        this.physicsBody.setLinearVelocity(0, 0);
        this.physicsBody.setTransform(99, 99, 0);

        this.enabled = false;
    }

    public void update(float delta)
    {
        for (DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
        {
            if (drawableEntity instanceof InteractiveEntityInterface && drawableEntity instanceof OptimizableEntityInterface)
            {

                Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.physicsBody.getPosition(), 0)));
                pos.y = Environment.gameCamera.position.y*2 - pos.y;

                if (drawableEntity instanceof Zombie && ((InteractiveEntityInterface) drawableEntity).getPolygon().contains(pos.x, pos.y)) // Boom! And all the zombies are gone from a grenade!
                {
                    ((Zombie) drawableEntity).stopGetUp();
                    ((Zombie) drawableEntity).enable_physics();

                    // Apply impulse to torso to make the entire zombie fly! (also torso is not a detachableEntity so the loop below does not cover it)
                    ((PhysicsEntityInterface) ((Zombie) drawableEntity).getDrawableEntities().get("torso")).getPhysicsBody().applyLinearImpulse(this.rayDir, this.initialPos, true);

                    for(DetachableEntityInterface detachableEntityInterface : ((Zombie) drawableEntity).getDetachableEntities().values())
                    {
                        if(detachableEntityInterface.getState().equals("attached") && detachableEntityInterface instanceof InteractiveEntityInterface && ((InteractiveEntityInterface) detachableEntityInterface).getPolygon().contains(pos.x, pos.y))
                        {
                            // Pretty much detach all zombie limbs by setting detach force to nothing
                            detachableEntityInterface.setForceForDetach(0.01f);
                        }
                    }
                } else if(drawableEntity instanceof Zombie && !((Zombie) drawableEntity).isAlive() && ((Zombie) drawableEntity).getInteractiveEntities().get("torso").getPolygon().contains(pos.x, pos.y)) // Need this for torso to move when zombie is dead
                {
                    Gdx.app.log("zomzom", "bombom");
                    ((Zombie) drawableEntity).enable_physics();
                    ((PhysicsEntityInterface) ((Zombie) drawableEntity).getDrawableEntities().get("torso")).getPhysicsBody().setActive(true);
                    ((PhysicsEntityInterface) ((Zombie) drawableEntity).getDrawableEntities().get("torso")).getPhysicsBody().applyLinearImpulse(this.rayDir, this.initialPos, true);
                }
                else if(drawableEntity instanceof PhysicsEntityInterface) // Make detached zombie limbs fly too!
                {
                    ((OptimizableEntityInterface) drawableEntity).disable_optimization();
                    ((PhysicsEntityInterface) drawableEntity).getPhysicsBody().applyLinearImpulse(this.rayDir, this.initialPos, true);
                }
            }
        }
    }

}
