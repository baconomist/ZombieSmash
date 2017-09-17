package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.Physics;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class RegularZombie extends Zombie{

    public RegularZombie(Physics physics){
        super(physics);
    }

}
