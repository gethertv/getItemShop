package dev.gether.getitemshop.database;

import dev.gether.getitemshop.GetItemShop;

import java.util.logging.Level;

public class MysqlManager {

    private final GetItemShop plugin;
    private final MysqlService sqlService;
    private Mysql sql;



    public MysqlManager(GetItemShop plugin)
    {
        this.plugin = plugin;
        setupSql();
        sqlService = new MysqlService(this, plugin);
    }

    public boolean isConnected()
    {
        if (sql.isConnected()) {
            return true;
        }
        return false;
    }

    private void setupSql() {
        String host = plugin.getConfig().getString("mysql.host");
        String username = plugin.getConfig().getString("mysql.username");
        String password = plugin.getConfig().getString("mysql.password");
        String database = plugin.getConfig().getString("mysql.database");
        String port = plugin.getConfig().getString("mysql.port");

        boolean ssl = false;
        if (plugin.getConfig().get("mysql.ssl") != null) {
            ssl = plugin.getConfig().getBoolean("mysql.ssl");
        }
        this.sql = new Mysql(host, username, password, database, port, ssl);
    }

    public Mysql getSql() {
        return sql;
    }

    public MysqlService getSqlService() {
        return sqlService;
    }
}
