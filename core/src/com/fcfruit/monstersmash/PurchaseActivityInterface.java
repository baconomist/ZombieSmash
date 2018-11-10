package com.fcfruit.monstersmash;

public interface PurchaseActivityInterface
{
    boolean is_purchased(String item_sku);
    boolean purchase(String item_sku);
    boolean consume_purchase(String sku); // Used for consumable items, need to "unpurchase" an item after purchased so that the user can purchase again
    boolean restore_purchases();
}
