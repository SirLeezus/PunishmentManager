package lee.code.punishments.database;

import lee.code.core.ormlite.dao.Dao;
import lee.code.core.ormlite.dao.DaoManager;
import lee.code.core.ormlite.jdbc.JdbcConnectionSource;
import lee.code.core.ormlite.jdbc.db.DatabaseTypeUtils;
import lee.code.core.ormlite.logger.LogBackendType;
import lee.code.core.ormlite.logger.LoggerFactory;
import lee.code.core.ormlite.support.ConnectionSource;
import lee.code.core.ormlite.table.TableUtils;
import lee.code.punishments.Punishments;
import lee.code.punishments.database.tables.PlayerTable;
import lee.code.punishments.database.tables.ServerTable;
import lee.code.punishments.lists.ConfigFile;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    private Dao<PlayerTable, UUID> playerDao;
    private Dao<ServerTable, String> serverDao;

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    private String getDatabaseURL() {
        return "jdbc:mysql://" + ConfigFile.MYSQL_IP.getString(null) + ":" + ConfigFile.MYSQL_PORT.getString(null) + "/" + ConfigFile.MYSQL_DATABASE.getString(null) + "?useSSL=true";
    }

    public void initialize() {
        LoggerFactory.setLogBackendFactory(LogBackendType.NULL);

        try {
            String databaseURL = getDatabaseURL();
            connectionSource = new JdbcConnectionSource(
                    databaseURL,
                    ConfigFile.MYSQL_USER.getString(null),
                    ConfigFile.MYSQL_PASSWORD.getString(null),
                    DatabaseTypeUtils.createDatabaseType(databaseURL));
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        CacheManager cacheManager = Punishments.getPlugin().getCacheManager();

        //player data
        TableUtils.createTableIfNotExists(connectionSource, PlayerTable.class);
        playerDao = DaoManager.createDao(connectionSource, PlayerTable.class);
        //load player data into cache
        for (PlayerTable playerTable : playerDao.queryForAll()) cacheManager.setPlayerData(playerTable);

        //server data
        TableUtils.createTableIfNotExists(connectionSource, ServerTable.class);
        serverDao = DaoManager.createDao(connectionSource, ServerTable.class);
        //load server data into cache
        for (ServerTable serverTable : serverDao.queryForAll()) cacheManager.setServerData(serverTable);

    }

    public void closeConnection() {
        try {
            connectionSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void createPlayerTable(PlayerTable playerTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getPlugin(), () -> {
            try {
                playerDao.create(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updatePlayerTable(PlayerTable playerTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getPlugin(), () -> {
            try {
                playerDao.update(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createServerTable(ServerTable serverTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getPlugin(), () -> {
            try {
                serverDao.create(serverTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateServerTable(ServerTable serverTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getPlugin(), () -> {
            try {
                serverDao.update(serverTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
