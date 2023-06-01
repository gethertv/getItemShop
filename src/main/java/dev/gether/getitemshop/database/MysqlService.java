package dev.gether.getitemshop.database;

import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.service.ServiceStatus;
import dev.gether.getitemshop.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MysqlService {

    private final MysqlManager sqlManager;
    private final GetItemShop plugin;
    public MysqlService(MysqlManager sqlManager, GetItemShop plugin)
    {
        this.plugin = plugin;
        this.sqlManager = sqlManager;

    }


    public List<Service> loadUserService(Player player) {

        List<Service> servicesUser = new ArrayList<>();

        String str = "SELECT * FROM get_itemshop WHERE username = ? AND service_status = ?";
        try (PreparedStatement statement = getSqlManager().getSql().getConnection().prepareStatement(str)) {
            statement.setString(1, player.getName());
            statement.setString(2, ServiceStatus.AWAITING_PICKUP.name());

            ResultSet resultSet = statement.executeQuery();
            HashMap<String, Service> services = getPlugin().getServiceManager().getServices();
            while (resultSet.next()) {
                Service service = services.get(resultSet.getString("service_key"));
                servicesUser.add(service);
            }
        } catch (SQLException | NullPointerException exception) {
            System.out.println("Error loading user service: " + exception.getMessage());
        }

        return servicesUser;
    }

    public boolean checkServiceExists(String username, Service service, ServiceStatus toStatus) {
        String str = "UPDATE get_itemshop SET service_status = ? WHERE username = ? AND service_status = ? AND service_key = ? LIMIT 1";
        try (PreparedStatement statement = getSqlManager().getSql().getConnection().prepareStatement(str)) {
            statement.setString(1, toStatus.name());
            statement.setString(2, username);
            statement.setString(3, ServiceStatus.AWAITING_PICKUP.name());
            statement.setString(4, service.getKey());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException | NullPointerException exception) {
            System.out.println("Error updating service status: " + exception.getMessage());
        }

        return false;
    }


    public boolean addService(String username, Service service) {
        String sql = "INSERT INTO get_itemshop (username, service_name, service_key, service_status, d_o_c, d_o_u) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getSqlManager().getSql().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, service.getName());
            statement.setString(3, service.getKey());
            statement.setString(4, ServiceStatus.AWAITING_PICKUP.name());
            Timestamp timestamp = getCurrentTimestamp();
            statement.setTimestamp(5, timestamp);
            statement.setTimestamp(6, timestamp);
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            System.out.println("Error adding service: " + exception.getMessage());
        }

        return false;
    }

    public Timestamp getCurrentTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Timestamp.valueOf(currentDateTime);
    }

    public MysqlManager getSqlManager() {
        return sqlManager;
    }

    public GetItemShop getPlugin() {
        return plugin;
    }
}
