package com.fcfruit.monstersmash.level;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.fcfruit.monstersmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class House extends Objective
{


    House() {

        width = 1190;
        height = 900;

        sprite = new Sprite(Environment.assets.get("maps/night_map/night_map.atlas", TextureAtlas.class).findRegion("house"));
        sprite.setPosition(2496, 90f);

        polygon = new Polygon(new float[]{0, 0, width, 0, width, height, 0, height});

        attack_zones = new Polygon[2];
        attack_zones[0] = new Polygon(new float[]{0, 0, 400, 0, 400, 500, 0, 500});
        attack_zones[0].setPosition(this.sprite.getX() + 100, this.sprite.getY());

        attack_zones[1] = new Polygon(new float[]{0, 0, 450, 0, 450, 500, 0, 500});
        attack_zones[1].setPosition(this.sprite.getX() + this.sprite.getWidth() - attack_zones[1].getVertices()[2] - 50, this.sprite.getY());

    }


}
