package dev.gether.getitemshop;

import dev.gether.getitemshop.commands.GetItemShopCommand;
import dev.gether.getitemshop.database.MysqlManager;
import dev.gether.getitemshop.listener.InventoryClickListener;
import dev.gether.getitemshop.service.ServiceManager;
import dev.gether.getitemshop.user.UserManager;
import dev.gether.getitemshop.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class GetItemShop extends JavaPlugin {

    private static GetItemShop instance;
    private ServiceManager serviceManager;
    private UserManager userManager;

    private MysqlManager mysqlManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        mysqlManager = new MysqlManager(this);
        if(!mysqlManager.isConnected())
        {
            getLogger().log (Level.WARNING, "Can not connect to database!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        serviceManager = new ServiceManager(this);
        userManager = new UserManager(this);

        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getCommandMap().register(getConfig().getString("command"), new GetItemShopCommand(getConfig().getString("command"), this));

    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    public static GetItemShop getInstance() {
        return instance;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public MysqlManager getMysqlManager() {
        return mysqlManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
