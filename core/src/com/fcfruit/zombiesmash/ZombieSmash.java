package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.screens.MainMenu;


public class ZombieSmash extends Game {

	private TextureAtlas playerAtlas;
	private SkeletonJson json;
	private SkeletonData playerSkeletonData;
	private AnimationStateData playerAnimationData;

	private SpriteBatch batch;
	private SkeletonRenderer skeletonRenderer;

	private Skeleton skeleton;
	private AnimationState animationState;

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
