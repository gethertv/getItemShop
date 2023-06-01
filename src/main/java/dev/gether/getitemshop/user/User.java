package dev.gether.getitemshop.user;

import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.utils.ItemBackground;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class User {

    private Player player;
    private Inventory inventory;

    public User(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }


    public void refreshInv(int slot)
    {
        ItemStack item = inventory.getItem(slot);
        if(item!=null)
        {
            if(item.getAmount()<=1)
            {
                inventory.setItem(slot, ItemBackground.BACKGROUND_ITEM);
                return;
            }
            item.setAmount(item.getAmount()-1);
        }
    }
    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
