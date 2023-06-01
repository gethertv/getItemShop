package dev.gether.getitemshop.service;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class Service {
    private String key;
    private String name;
    private ItemStack itemStack;
    private List<String> commands;

    public Service(String key, String name, ItemStack itemStack, List<String> commands) {
        this.key = key;
        this.name = name;
        this.itemStack = itemStack;
        this.commands = commands;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<String> getCommands() {
        return commands;
    }
}
