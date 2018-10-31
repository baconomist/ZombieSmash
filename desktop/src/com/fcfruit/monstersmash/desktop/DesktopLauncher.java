package com.fcfruit.monstersmash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.MonsterSmash;

public class DesktopLauncher
{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1800;
		config.height = 900;
		Environment.game = Environment.game = new MonsterSmash();
		new LwjglApplication(Environment.game, config);
	}
}
