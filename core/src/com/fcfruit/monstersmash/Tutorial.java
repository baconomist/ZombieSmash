package com.fcfruit.monstersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-21.
 */

public abstract class Tutorial {

    private TutorialAction[] actions;

    public void setActions(TutorialAction[] actions){this.actions = actions;}

    public TutorialAction[] getActions()
    {
        return actions;
    }

    private class TutorialAction
    {
        private boolean waitForMonsterDeath = false;
        private boolean waitForCrateOpen = false;
        private boolean waitForPowerupActivation = false;

        public TutorialAction()
        {

        }

        // Return self instance to allow for TutorialAction.showText().spawnMonster().spawnPowerup() if needed
        public TutorialAction showText(String text, Vector2 ui_position){return this;}
        public TutorialAction spawnMonster(Class monsterClass, boolean waitForDeath){return this;}
        public TutorialAction spawnPowerup(Class powerupType, boolean waitForCrateOpen, boolean waitForActivation){return this;}
        public TutorialAction onActorClick(Actor actor){//actr.addListener()..
            return this;}


        public void update(float delta){}
        // On touch up, if not waiting for something, continue to next tutorial action
        public void onTouchUp(){}

        public void activate(){}
        public void addOnCompleteListener(Runnable runnable){runnable.run();}
        public void onComplete(){onTutorialActionComplete();}
    }




    private void onTutorialActionComplete(){}

    public abstract void onTutorialComplete();

}
