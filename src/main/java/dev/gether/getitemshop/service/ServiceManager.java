package dev.gether.getitemshop.service;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ServiceManager {

    private GetItemShop plugin;
    private HashMap<String, Service> services = new HashMap<>();
    private HashMap<ItemStack, String> keyToService = new HashMap<>();

    public ServiceManager(GetItemShop plugin) {
        this.plugin = plugin;
        injectServices();
    }

    private void injectServices() {
        for (String key : plugin.getConfig().getConfigurationSection("services").getKeys(false)) {
            String name = plugin.getConfig().getString("services." + key + ".name");
            ItemStack item = getItemService("services." + key + ".item");
            List<String> commands = new ArrayList<>(plugin.getConfig().getStringList("services." + key + ".commands"));
            services.put(key, new Service(key, name, item, commands));

            keyToService.put(item, key);
        }
    }

    private ItemStack getItemService(String path) {
        ItemStack itemStack = new ItemStack(Material.valueOf(plugin.getConfig().getString(path + ".material").toUpperCase()));

        if (plugin.getConfig().getBoolean(path + ".glow"))
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemMeta.setDisplayName(ColorFixer.addColors(plugin.getConfig().getString(path + ".displayname")));

        List<String> lore = new ArrayList<>();
        lore.addAll(plugin.getConfig().getStringList(path + ".lore"));
        itemMeta.setLore(ColorFixer.addColors(lore));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public Optional<Service> getServiceByItem(ItemStack itemStack)
    {
        ItemStack item = itemStack.clone();
        item.setAmount(1);

        String key = getKeyToService().get(item);
        if(key!=null)
        {
            Service service = getServices().get(key);
            if(service!=null)
                return Optional.of(service);

        }

        return Optional.empty();
    }

    public HashMap<ItemStack, String> getKeyToService() {
        return keyToService;
    }

    public HashMap<String, Service> getServices() {
        return services;
    }
}
