package com.fcfruit.monstersmash;

import android.app.ActivityManager;
import android.content.Context;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googleplay.AndroidGooglePlayPurchaseManager;
import com.fcfruit.monstersmash.Environment;
public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		Environment.purchaseManager = new AndroidGooglePlayPurchaseManager(this, 0);
		Environment.game = new MonsterSmash();

		/*
		// Create the layout
       // RelativeLayout layout = new RelativeLayout(this);

        // Create the libgdx View
        View gameView = initializeForView(Environment.game, config);

		MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");

		// Create and setup the AdMob view
        AdView adView = new AdView(this, AdSize.BANNER, "xxxxxxxx"); // Put in your secret key here
        adView.loadAd(new AdRequest());

        // Add the libgdx view
        layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(adView, adParams);

        // Hook it all up
        setContentView(layout);*/

		initialize(Environment.game, config);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Might crash as Environment.settings might not yet be initialized
		try
		{
			// Disable recent apps button
			if (!Environment.settings.isRecentAppsButtonEnabled())
			{
				ActivityManager activityManager = (ActivityManager) getApplicationContext()
						.getSystemService(Context.ACTIVITY_SERVICE);
				// If apk version matches required for this function call
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				{
					activityManager.moveTaskToFront(getTaskId(), 0);
				}
			}
		} catch (NullPointerException e){e.printStackTrace();}
	}

}
