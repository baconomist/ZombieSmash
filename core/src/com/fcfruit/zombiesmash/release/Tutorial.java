package com.fcfruit.zombiesmash.release;

import com.badlogic.gdx.Gdx;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Tutorial {

    public Tutorial(){

       String text = Gdx.files.internal("isTutorialOn.txt").readString();
       // Move checking of tutorial to when tutorial created to optimize!!!!!!!!!!!!!!!!!!!!!!!
       if(text.substring(0, 3).equals("true")){
            runTutorial();
       }

    }

    private void runTutorial(){


    }

}
