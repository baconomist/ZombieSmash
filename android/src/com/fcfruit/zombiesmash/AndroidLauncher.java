package com.fcfruit.zombiesmash;

import android.app.ActivityManager;
import android.content.Context;
import android.os.*;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(Environment.game, config);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Disable recent apps button
		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		// If apk version matches required for this function call
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activityManager.moveTaskToFront(getTaskId(), 0);
		}
	}

}
