package dev.gether.getitemshop.listener;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.user.ItemShopCallback;
import dev.gether.getitemshop.user.User;
import dev.gether.getitemshop.utils.ColorFixer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ConnectListener implements Listener {

    private final GetItemShop plugin;
    public ConnectListener(GetItemShop plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if(!plugin.getConfig().isSet("notification"))
            return;

        if(!plugin.getConfig().getBoolean("notification"))
            return;
        plugin.getUserManager().findUserServices(player, new ItemShopCallback() {
            @Override
            public void queryDone(List<Service> services) {
                if(services.size()<=0)
                    return;
                new BukkitRunnable() {

                    @Override
                    public void run() {
                            player.sendTitle(
                                    ColorFixer.addColors(plugin.getConfig().getString("lang.have-serivce.title")),
                                    ColorFixer.addColors(plugin.getConfig().getString("lang.have-serivce.subtitle")),
                                    20,
                                    100,
                                    20
                            );
                            player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.have-serivce.chat")));
                    }
                }.runTaskLater(plugin, 20L*3);
            }

            @Override
            public void queryAddService(boolean status) {

            }
        });
    }


}
