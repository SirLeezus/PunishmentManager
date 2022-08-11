package lee.code.punishments.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.punishments.Punishments;
import lee.code.punishments.database.tables.PlayerTable;
import lee.code.punishments.database.tables.ServerTable;
import lee.code.punishments.lists.Lang;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheManager {

    private final String serverKey = "server";

    @Getter
    private final Cache<UUID, PlayerTable> playerCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .recordStats()
            .build();

    @Getter
    private final Cache<String, ServerTable> serverCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .recordStats()
            .build();

    public void createDefaults(UUID uuid) {
        if (hasPlayerData(uuid)) {
            PlayerTable playerTable = Punishments.getPlugin().getDatabaseManager().fetchPlayerTable(uuid);
            getPlayerCache().put(playerTable.getPlayer(), playerTable);
        } else {
            createPlayerData(new PlayerTable(uuid));
        }
    }

    //server table
    public ServerTable getServerTable() {
        return getServerCache().getIfPresent(serverKey);
    }

    public void updateServerTable(ServerTable serverTable) {
        getServerCache().put(serverTable.getServer(), serverTable);
        Punishments.getPlugin().getDatabaseManager().updateServerTable(serverTable);
    }

    public void createServerData() {
        if (getServerCache().asMap().isEmpty()) {
            ServerTable serverTable = new ServerTable(serverKey);
            getServerCache().put(serverTable.getServer(), serverTable);
            Punishments.getPlugin().getDatabaseManager().createServerTable(serverTable);
        }
    }

    public void setServerData(ServerTable serverTable) {
        getServerCache().put(serverTable.getServer(), serverTable);
    }

    public boolean isBotCheckEnabled() {
        return getServerTable().isBotCheckEnabled();
    }

    //player table
    public boolean hasPlayerData(UUID uuid) {
        return getPlayerCache().getIfPresent(uuid) != null;
    }

    public void setPlayerData(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
    }

    public void createPlayerData(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        Punishments.getPlugin().getDatabaseManager().createPlayerTable(playerTable);
    }

    private PlayerTable getPlayerTable(UUID player) {
        return getPlayerCache().getIfPresent(player);
    }

    private void updatePlayerTable(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        Punishments.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public Map<UUID, Long> getPunishList() {
        Map<UUID, Long> punMap = new HashMap<>();
        for (PlayerTable playerTable : getPlayerCache().asMap().values()) {
            if (playerTable.isBanned()) punMap.put(playerTable.getPlayer(), playerTable.getDateBanned());
            else if (playerTable.getTempBanned() > 0) punMap.put(playerTable.getPlayer(), playerTable.getDateBanned());
            else if (playerTable.isMuted()) punMap.put(playerTable.getPlayer(), playerTable.getDateMuted());
            else if (playerTable.getTempMuted() > 0) punMap.put(playerTable.getPlayer(), playerTable.getDateMuted());
        }
        return punMap;
    }

    public String getBanReason(UUID uuid) {
        return getPlayerTable(uuid).getBanReason();
    }

    public long getTempBanTime(UUID uuid) {
        return getPlayerTable(uuid).getTempBanned() - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public long getTempMuteTime(UUID uuid) {
        return getPlayerTable(uuid).getTempMuted() - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public boolean isBanned(UUID uuid) {
        return getPlayerTable(uuid).isBanned();
    }

    public boolean isTempBanned(UUID uuid) {
        return getPlayerTable(uuid).getTempBanned() != 0;
    }

    public boolean isTempMuted(UUID uuid) {
        return getPlayerTable(uuid).getTempMuted() != 0;
    }

    public Component shouldMute(UUID uuid) {
        PlayerTable playerTable = getPlayerTable(uuid);
        if (playerTable.isMuted()) {
            return Lang.PREFIX.getComponent(null).append(Lang.MUTED.getComponent(new String[]{ playerTable.getMuteReason() }));
        } else if (playerTable.getTempMuted() != 0) {
            long time = playerTable.getTempMuted() - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            if (time > 0) {
                return Lang.PREFIX.getComponent(null).append(Lang.TEMPMUTED.getComponent(new String[]{ BukkitUtils.parseSeconds(time), playerTable.getMuteReason() }));
            } else {
                setTempMutedPlayer(uuid, null, "", 0);
                return Lang.PREFIX.getComponent(null).append(Lang.TEMPUNMUTED.getComponent(null));
            }
        } else if (isBotCheckEnabled() && playerTable.isBot()) {
            return Lang.PREFIX.getComponent(null).append(Lang.BOTMUTED.getComponent(null));
        } else return null;
    }

    public boolean isMuted(UUID uuid) {
        return getPlayerTable(uuid).isMuted();
    }

    public String getMuteReason(UUID uuid) {
        return getPlayerTable(uuid).getMuteReason();
    }

    public long getBanDate(UUID uuid) {
        return getPlayerTable(uuid).getDateBanned();
    }

    public void setBanDate(UUID uuid, long time) {
        PlayerTable punishmentTable = getPlayerTable(uuid);
        punishmentTable.setDateBanned(time);
        updatePlayerTable(punishmentTable);
    }

    public String getBanStaffName(UUID uuid) {
        OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(UUID.fromString(getPlayerTable(uuid).getBanStaff()));
        if (oPlayer.getName() != null) return oPlayer.getName();
        else return "Console";
    }

    public String getMuteStaffName(UUID uuid) {
        OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(UUID.fromString(getPlayerTable(uuid).getMuteStaff()));
        if (oPlayer.getName() != null) return oPlayer.getName();
        else return "Console";
    }

    public void setMutedPlayer(UUID uuid, UUID staff, String reason, boolean isMuted) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setMuted(isMuted);
        playerTable.setMuteReason(reason);
        playerTable.setMuteStaff(String.valueOf(staff));
        playerTable.setDateMuted(System.currentTimeMillis());
        updatePlayerTable(playerTable);
    }

    public void setBannedPlayer(UUID uuid, UUID staff, String reason, boolean isBanned) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setBanned(isBanned);
        playerTable.setBanStaff(staff == null ? "0" : String.valueOf(staff));
        playerTable.setBanReason(reason);
        playerTable.setDateBanned(System.currentTimeMillis());
        updatePlayerTable(playerTable);
    }

    public void setTempBannedPlayer(UUID uuid, UUID staff, String reason, long time) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setTempBanned(time);
        playerTable.setBanReason(reason);
        playerTable.setBanStaff(staff == null ? "0" : String.valueOf(staff));
        playerTable.setDateBanned(System.currentTimeMillis());
        updatePlayerTable(playerTable);
    }

    public void setTempMutedPlayer(UUID uuid, UUID staff, String reason, long time) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setTempMuted(time);
        playerTable.setMuteReason(reason);
        playerTable.setMuteStaff(staff == null ? "0" : String.valueOf(staff));
        playerTable.setDateMuted(System.currentTimeMillis());
        updatePlayerTable(playerTable);
    }

    public boolean isBot(UUID uuid) {
        return getPlayerTable(uuid).isBot();
    }

    public void setBotStatus(UUID uuid, boolean isBot) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setBot(isBot);
        updatePlayerTable(playerTable);
    }
}
