package dev.gether.getitemshop.database;
import dev.gether.getitemshop.GetItemShop;
import dev.gether.getitemshop.service.Service;
import dev.gether.getitemshop.service.ServiceStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Mysql {
    private String host;
    private String username;
    private String password;
    private String database;
    private String port;
    private boolean ssl;
    private boolean isFinished;
    private Connection connection;

    public Mysql(String host, String username, String password, String database, String port, boolean ssl) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
        this.ssl = ssl;

        openConnection();
        createTable();
    }

    private String getUsername() {
        return this.username;
    }

    private String getPassword() {
        return this.password;
    }

    private String getHost() {
        return this.host;
    }

    private String getPort() {
        return this.port;
    }

    private String getDatabase() {
        return this.database;
    }

    private boolean useSSL() {
        return this.ssl;
    }

    public boolean isConnected() {
        return (getConnection() != null);
    }

    public Connection getConnection() {
        validateConnection();
        return this.connection;
    }

    private void openConnection() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = 0L;
            //Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", getUsername());
            properties.setProperty("password", getPassword());
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("useSSL", String.valueOf(useSSL()));
            properties.setProperty("requireSSL", String.valueOf(useSSL()));
            properties.setProperty("verifyServerCertificate", "false");
            String str = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();
            this.connection = DriverManager.getConnection(str, properties);
            l2 = System.currentTimeMillis();
            this.isFinished = true;
            System.out.println("[mysql] Connected successfully ["+(l2-l1)+"ms]");
        } catch (ClassNotFoundException classNotFoundException) {
            this.isFinished = false;
            System.out.println("[mysql] Check your configuration.");
            Bukkit.getPluginManager().disablePlugin(GetItemShop.getInstance());
        } catch (SQLException sQLException) {
            this.isFinished = false;
            System.out.println("[mysql] (" + sQLException.getLocalizedMessage() + "). Check your configuration.");
            Bukkit.getPluginManager().disablePlugin(GetItemShop.getInstance());
        }
    }

    private void validateConnection() {
        if (!this.isFinished)
            return;
        try {
            if (this.connection == null) {
                System.out.println("[mysql] aborted. Connecting again");
                reConnect();
            }
            if (!this.connection.isValid(4)) {
                System.out.println("[mysql] timeout.");
                reConnect();
            }
            if (this.connection.isClosed()) {
                System.out.println("[mysql] closed. Connecting again");
                reConnect();
            }
        } catch (Exception exception) {
        }
    }

    private void reConnect() {
        System.out.println("[mysql] connection again");
        openConnection();
    }

    public void closeConnection() {
        if (getConnection() != null) {
            try {
                getConnection().close();
                System.out.println("[mysql] connection closed");
            } catch (SQLException sQLException) {
                System.out.println("[mysql] error when try close connection");
            }
        }
    }

    public void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS get_itemshop (" +
                "id INT(10) AUTO_INCREMENT, PRIMARY KEY (id)," +
                "username VARCHAR(100), " +
                "service_name VARCHAR(100), " +
                "service_key VARCHAR(32), " +
                "service_status VARCHAR(32), " +
                "d_o_c TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "d_o_u TIMESTAMP DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP" +
                ")";

        update(create);
    }
    public void update(String paramString) {
        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                statement.executeUpdate(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong update : '" + paramString + "'!");
        }
    }

    public ResultSet getResult(String paramString) {
        ResultSet resultSet = null;
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                resultSet = statement.executeQuery(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong when want get result: '" + paramString + "'!");
        }
        return resultSet;
    }


}
