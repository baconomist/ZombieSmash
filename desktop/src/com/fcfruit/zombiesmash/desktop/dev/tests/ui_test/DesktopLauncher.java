package com.fcfruit.zombiesmash.desktop.dev.tests.ui_test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fcfruit.zombiesmash.dev.tests.ui_test.Environment;

public class DesktopLauncher
{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(Environment.game, config);
	}
}
