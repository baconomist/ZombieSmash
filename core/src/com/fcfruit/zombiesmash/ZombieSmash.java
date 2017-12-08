package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.screens.MainMenu;


public class ZombieSmash extends Game {

	public static float screenWidth = 1920;
	public static float screenHeight = 1080;

	FPSLogger logger = new FPSLogger();

	public ZombieSmash() {
		//don't put anything here, may not work because game is not yet initialized
		super();

	}

	@Override
	public void create () {
		/*
		called after all game default game functions
		are initialized
		 */
		Environment.gameData = new GameData();
		Environment.settings = new Settings();





		this.setScreen(new MainMenu());


	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	@Override
	public Screen getScreen() {
		return super.getScreen();
	}

	@Override
	public void render () {
		//don't put anything in here, use screens
		// Clear the screen.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		logger.log();
		super.render();
	}
}
