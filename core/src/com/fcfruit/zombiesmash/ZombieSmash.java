package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.screens.MainMenu;


public class ZombieSmash extends Game {

	public static float screenWidth = 1920;
	public static float screenHeight = 1080;

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

		Environment.gameCamera = new OrthographicCamera(screenWidth/ com.fcfruit.zombiesmash.physics.Physics.PPM, screenHeight/ com.fcfruit.zombiesmash.physics.Physics.PPM);
		// Camera position/origin is in the middle
		// Not bottom left
		// see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
		// Also cam.project(worldpos) is x and y from bottom left corner
		// But cam.unproject(screenpos) is x and y from top left corner
		Environment.gameCamera.position.set(Environment.gameCamera.viewportWidth/2, Environment.gameCamera.viewportHeight/2, 0);
		Environment.gameCamera.update();

		Environment.physics = new com.fcfruit.zombiesmash.physics.Physics();
		Environment.gameScreen = new GameScreen();


		new GameData();


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
		super.render();
	}
}
