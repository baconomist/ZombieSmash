package com.fcfruit.monstersmash;

import com.badlogic.gdx.Preferences;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class PurchaseManager
{
    private Preferences purchase_prefs;

    public PurchaseManager()
    {
        this.purchase_prefs = Environment.Prefs.purchases;
    }

    public void save_purchase(String item_sku)
    {
        String[] purchased_items_old = json.fromJson(String[].class, Environment.Prefs.purchases.getString("purchased_items"));
        String[] purchased_items = new String[purchased_items_old.length + 1];

        // Copy old list into new one
        for(int i = 0; i < purchased_items_old.length; i++)
        {
            purchased_items[i] = purchased_items_old[i];
        }

        purchased_items[purchased_items.length - 1] = item_sku;

        Environment.Prefs.purchases.putString("purchased_items", json.toJson(purchased_items));
        Environment.Prefs.purchases.flush();
    }

    public boolean is_cache_purchased(String item_sku)
    {
        String[] purchased_items = json.fromJson(String[].class, Environment.Prefs.purchases.getString("purchased_items"));
        for (String purchased_item : purchased_items)
        {
            if (purchased_item != null && purchased_item.equals(item_sku))
                return true;
        }
        return false;
    }


}
