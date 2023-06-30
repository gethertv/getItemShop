package dev.gether.getitemshop.listener;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class InventoryClickListener implements Listener {

    private final GetItemShop plugin;
    public InventoryClickListener(GetItemShop plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onClickInv(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory()==null)
            return;

        User user = getPlugin().getUserManager().getUserData().get(player.getUniqueId());
        if(user==null)
            return;

        if(user.getInventory().equals(event.getInventory()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(user.getInventory()))
            {
                ItemStack itemStack = event.getCurrentItem();
                if(itemStack==null)
                    return;

                Optional<Service> serviceByItem = plugin.getServiceManager().getServiceByItem(itemStack);
                if(!serviceByItem.isPresent())
                    return;

                getPlugin().getUserManager().useService(user, serviceByItem.get());
                return;
            }
            return;
        }

        getPlugin().getUserManager().getUserData().remove(player.getUniqueId());

    }

    public GetItemShop getPlugin() {
        return plugin;
    }
}
