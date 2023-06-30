package dev.gether.getitemshop.user;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Player player;
    private Inventory inventory;
    private List<Service> services;

    public User(Player player, List<Service> services) {
        this.player = player;
        this.inventory = Bukkit.createInventory(
                null,
                GetItemShop.getInstance().getConfig().getInt("inv.size"),
                ColorFixer.addColors(GetItemShop.getInstance().getConfig().getString("inv.title")));
        this.services = services;
    }


    public void openInv()
    {
        GetItemShop plugin = GetItemShop.getInstance();
        inventory.clear();
        inventory = fillBackground(inventory);

        for (int i = 0; i < services.size(); i++) {
            List<Integer> slots = new ArrayList<>();
            slots.addAll(plugin.getConfig().getIntegerList("serv-slots"));
            Service service = services.get(i);
            Integer integer = slots.get(i);
            if(integer!=null)
                inventory.setItem(integer, service.getItemStack());
            else
                inventory.addItem(service.getItemStack());
        }
        player.openInventory(inventory);
    }

    private Inventory fillBackground(Inventory inventory) {
        ConfigurationSection background = GetItemShop.getInstance().getConfig().getConfigurationSection("background");
        for(String key : background.getKeys(false))
        {
            ConfigurationSection bg = background.getConfigurationSection("."+key);
            ItemStack itemStack = new ItemStack(Material.valueOf(bg.getString(".material").toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors(bg.getString(".displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(bg.getStringList(".lore"));
            itemMeta.setLore(ColorFixer.addColors(lore));
            itemStack.setItemMeta(itemMeta);

            List<Integer> slots = new ArrayList<>();
            slots.addAll(bg.getIntegerList(".slots"));
            for(Integer slot : slots)
                inventory.setItem(slot, itemStack);

        }
        return inventory;
    }

    public List<Service> getServices() {
        return services;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
