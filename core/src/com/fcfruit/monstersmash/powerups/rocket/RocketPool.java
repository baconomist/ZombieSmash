package com.fcfruit.monstersmash.powerups.rocket;

import com.badlogic.gdx.Gdx;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.powerups.rocket.Rocket;

public class RocketPool
{
    private com.fcfruit.monstersmash.powerups.rocket.Rocket[] rockets;

    public RocketPool()
    {
        this.rockets = new com.fcfruit.monstersmash.powerups.rocket.Rocket[50];
        for(int i = 0; i < this.rockets.length; i++)
        {
            this.rockets[i] = new com.fcfruit.monstersmash.powerups.rocket.Rocket();
        }
    }

    public com.fcfruit.monstersmash.powerups.rocket.Rocket getRocket()
    {
        for(com.fcfruit.monstersmash.powerups.rocket.Rocket rocket : this.rockets)
        {
            if(!rocket.enabled)
            {
                rocket.enable();
                return rocket;
            }
        }

        Gdx.app.error("RocketPool", "No available rockets in bool. Make sure rockets are being returned" +
                "to the pool or Increase max pool rocket limit [default is 50]");

        com.fcfruit.monstersmash.powerups.rocket.Rocket rocket = new com.fcfruit.monstersmash.powerups.rocket.Rocket();
        rocket.enable();
        return rocket;
    }

    // This pool isn't as secure as others,
    // I assume 50 rockets is enough
    // Considering this overpowered powerup
    // Won't be used much
    public void returnRocket(Rocket rocket)
    {
        rocket.disable();
    }
}
