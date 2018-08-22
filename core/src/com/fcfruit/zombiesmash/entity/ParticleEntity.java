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

    public ParticleEntity(Vector2 particlePos, Vector2 rayDir, float NUMRAYS, float blastPower, float drag)
    {
        this.blastPower = blastPower;
        this.initialPos = particlePos;

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true; // rotation not necessary
        bd.bullet = true; // prevent tunneling at high speed

        bd.linearDamping = drag; // drag due to moving through air

        bd.gravityScale = 0; // ignore gravity
        bd.position.x = particlePos.x;
        bd.position.y = particlePos.y;// start at blast center
        rayDir.scl(blastPower); // scale raydir to blastPower
        bd.linearVelocity.x = rayDir.x;
        bd.linearVelocity.y = rayDir.y;

        this.physicsBody = Environment.physics.createBody(bd);

        //create a reference to this class in the body(this allows us to loop through the world bodies and check if the body is an Explosion particle)
        this.physicsBody.setUserData(new PhysicsData(this));

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.05f); // very small

        FixtureDef fd = new FixtureDef();
        fd.shape = circleShape;
        fd.density = 120 / NUMRAYS; // very high - shared across all particles
        fd.friction = 0; // friction not necessary
        fd.restitution = 0.99f; // high restitution to reflect off obstacles
        //fd.filter.groupIndex = -1; // particles should not collide with each other

        this.fixture = this.physicsBody.createFixture(fd);
        this.fixture.setUserData(new PhysicsData(this));

        this.rayDir = rayDir;

    }

    public void update(float delta)
    {
        for (DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
        {
            if (drawableEntity instanceof InteractiveEntityInterface && drawableEntity instanceof OptimizableEntityInterface)
            {

                Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.physicsBody.getPosition(), 0)));
                pos.y = Environment.gameCamera.position.y*2 - pos.y;

                if (((InteractiveEntityInterface) drawableEntity).getPolygon().contains(pos.x, pos.y))
                {
                    ((OptimizableEntityInterface)drawableEntity).disable_optimization();

                    if(drawableEntity instanceof Zombie)
                    {
                        ((Zombie) drawableEntity).stopGetUp();
                        //((PhysicsEntityInterface) ((Zombie) drawableEntity).getDrawableEntities().get("torso")).getthis.physicsBody().applyLinearImpulse(this.rayDir, this.initialPos, true);

                        for(DetachableEntityInterface detachableEntityInterface : ((Zombie) drawableEntity).getDetachableEntities().values())
                        {
                            if(detachableEntityInterface.getState().equals("attached") && detachableEntityInterface instanceof InteractiveEntityInterface && ((InteractiveEntityInterface) detachableEntityInterface).getPolygon().contains(pos.x, pos.y))
                            {
                                detachableEntityInterface.setState("waiting_for_detach");
                                Environment.detachableEntityDetachQueue.add(detachableEntityInterface);
                                ((PhysicsEntityInterface) detachableEntityInterface).getPhysicsBody().applyLinearImpulse(this.rayDir, this.initialPos, true);
                            }
                        }

                    }
                    
                }
            }
        }
    }

}
