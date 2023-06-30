package dev.gether.getitemshop.user;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.service.ServiceStatus;
import dev.gether.getitemshop.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserManager {

    public final GetItemShop plugin;
    private HashMap<UUID, User> userData = new HashMap<>();

    public UserManager(GetItemShop plugin)
    {
        this.plugin = plugin;
    }

    public void openService(Player player)
    {
        findUserServices(player, new ItemShopCallback() {
            @Override
            public void queryDone(List<Service> services) {
                if(services.size()<=0)
                {
                    player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.no-services")));
                    return;
                }
                User user = new User(player, services);
                userData.put(player.getUniqueId(), user);
                user.openInv();
            }

            @Override
            public void queryAddService(boolean status) {

            }
        });
    }

    public void useService(User user, Service service)
    {
        checkUserService(user.getPlayer().getName(), service, ServiceStatus.TO_BE_COLLECTED, new ItemShopCallback() {
            @Override
            public void queryDone(List<Service> services) {

            }
            @Override
            public void queryAddService(boolean status) {
                if(status)
                {
                    user.getPlayer().sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.successfully-recived").replace("{service}", service.getName())));
                   service.getCommands().forEach(cmd -> {
                       Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", user.getPlayer().getName()));
                   });

                   user.getServices().remove(service);
                   user.openInv();
                }
            }
        });
    }

    public void addService(String username, Service service, CommandSender sender)
    {
        addUserService(username, service, new ItemShopCallback() {
            @Override
            public void queryDone(List<Service> services) {

            }

            @Override
            public void queryAddService(boolean status) {
                if(status)
                {
                    sender.sendMessage(ColorFixer.addColors("&aPomyslnie dodano usluge dla {player}".replace("{player}", username)));
                    return;
                }
                sender.sendMessage(ColorFixer.addColors("&cNie udalo sie dodac uslugi dla {player}".replace("{player}", username)));
                return;
            }
        });
    }

    public void removeService(String username, Service service, CommandSender sender) {
        checkUserService(username, service, ServiceStatus.ADMIN_DELETE, new ItemShopCallback() {
            @Override
            public void queryDone(List<Service> services) {

            }
            @Override
            public void queryAddService(boolean status) {
                if(status)
                {
                    sender.sendMessage(ColorFixer.addColors("&aPomyslnie usunieto usluge dla {player}".replace("{player}", username)));
                    return;
                }
                sender.sendMessage(ColorFixer.addColors("&cPodany uzytkownik nie posiada takiej uslugi!"));
            }
        });
    }


    public void checkUserService(String username, Service service, ServiceStatus serviceStatus, final ItemShopCallback callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final boolean status = plugin.getMysqlManager().getSqlService().checkServiceExists(username, service, serviceStatus);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.queryAddService(status);
                    }
                });
            }
        });
    }
    public void addUserService(String username, Service service, final ItemShopCallback callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final boolean status = plugin.getMysqlManager().getSqlService().addService(username, service);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.queryAddService(status);
                    }
                });
            }
        });
    }
    public void findUserServices(Player player, final ItemShopCallback callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final List<Service> services = plugin.getMysqlManager().getSqlService().loadUserService(player);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.queryDone(services);
                    }
                });
            }
        });
    }

    public HashMap<UUID, User> getUserData() {
        return userData;
    }


}
