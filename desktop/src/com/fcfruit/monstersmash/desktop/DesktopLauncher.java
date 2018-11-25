package com.fcfruit.monstersmash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fcfruit.monstersmash.AdActivityInterface;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.MonsterSmash;
import com.fcfruit.monstersmash.PurchaseActivityInterface;

public class DesktopLauncher implements AdActivityInterface, PurchaseActivityInterface
{
	private static DesktopLauncher instance; // main is static, need this to reference instance

	public static void main (String[] arg) {

		if(instance == null)
		{
			instance = new DesktopLauncher();
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1800;
		config.height = 900;

		Environment.purchaseActivityInterface = instance;
		Environment.adActivityInterface = instance;
		Environment.game = new MonsterSmash();
		new LwjglApplication(Environment.game, config);
	}

	@Override
	public void showAds()
	{

	}

	@Override
	public boolean is_purchased(String item_sku)
	{
		return false;
	}

	@Override
	public boolean purchase(String item_sku)
	{
		return false;
	}

	@Override
	public boolean consume_purchase(String sku)
	{
		return false;
	}

	@Override
	public boolean restore_purchases()
	{
		return false;
	}
}
