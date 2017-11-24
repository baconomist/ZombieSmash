package com.fcfruit.zombiesmash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(Environment.game, config);
	}
}
