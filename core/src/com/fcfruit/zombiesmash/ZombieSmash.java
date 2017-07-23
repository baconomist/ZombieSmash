package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

import com.fcfruit.zombiesmash.screens.MainMenu;


public class ZombieSmash extends Game {

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

		new GameData();

		this.setScreen(new MainMenu(this));

		Gdx.app.log("Game", "Initialized");

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
		super.render(); // still called after setting screen, whole game render

	}
}
