package dev.gether.getitemshop.commands;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.user.ItemShopCallback;
import dev.gether.getitemshop.utils.ColorFixer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GetItemShopCommand extends Command {

    private HashMap<UUID, Long> cooldown = new HashMap<>();
    private GetItemShop plugin;
    public GetItemShopCommand(@NotNull String name, GetItemShop plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length==3)
        {
            if(!sender.hasPermission("getitemshop.admin"))
                return false;

            if(args[0].equalsIgnoreCase("add"))
            {
                String username = args[1];
                Service service = plugin.getServiceManager().getServices().get(args[2]);
                if(service==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodana usluga nie istnieje!"));
                    return false;
                }
                getPlugin().getUserManager().addService(username, service, sender);
                return true;
            }
            if(args[0].equalsIgnoreCase("remove"))
            {
                String username = args[1];
                Service service = plugin.getServiceManager().getServices().get(args[2]);
                if(service==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodana usluga nie istnieje!"));
                    return false;
                }
                getPlugin().getUserManager().removeService(username, service, sender);
                return true;
            }
        }
        if((sender instanceof Player))
        {

            Player player = (Player) sender;
            if(!player.hasPermission("getitemshop.use"))
                return false;

            if(hasCooldown(player))
            {
                player.sendMessage(ColorFixer.addColors(
                        getPlugin().getConfig().getString("lang.cooldown-cmd")
                            .replace("{time}", getTime(player)))
                );
                return false;
            }
            getPlugin().getUserManager().openService(player);
            addCooldown(player);
            return true;
        }
        return false;
    }

    private void addCooldown(Player player) {
        cooldown.put(player.getUniqueId(), System.currentTimeMillis()+getPlugin().getConfig().getInt("cooldown")*1000L);
    }
    private String getTime(Player player)
    {
        Long time = getCooldown().get(player.getUniqueId());
        double secondLong = (double) (time-System.currentTimeMillis());
        double second = secondLong/1000;
        return String.format("%.2f", second);
    }

    private boolean hasCooldown(Player player)
    {
        Long time = getCooldown().get(player.getUniqueId());
        if(time!=null)
        {
          if(time>System.currentTimeMillis())
          {
              return true;
          }
        }

        return false;
    }

    public HashMap<UUID, Long> getCooldown() {
        return cooldown;
    }

    public GetItemShop getPlugin() {
        return plugin;
    }
}
