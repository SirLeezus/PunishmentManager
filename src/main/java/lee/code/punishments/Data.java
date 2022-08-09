package lee.code.punishments;

import lee.code.core.util.files.CustomYML;
import lee.code.core.util.files.FileManager;
import lee.code.punishments.lists.ConfigFile;
import lee.code.punishments.lists.MuteCommand;
import lee.code.punishments.lists.Setting;
import lee.code.punishments.menusystem.PlayerMU;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    @Getter private final List<String> muteCommands = new ArrayList<>();

    private final ConcurrentHashMap<UUID, PlayerMU> playerMUList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> playerSpamTask = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Component> playerSpamLogger = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> playerSpamLoggerCount = new ConcurrentHashMap<>();

    public boolean isSpamTaskActive(UUID player) {
        return playerSpamTask.containsKey(player);
    }
    public void addSpamTaskActive(UUID player, int task) {
        playerSpamTask.put(player, task);
    }
    public void removeSpamTaskActive(UUID player) {
        playerSpamTask.remove(player);
    }
    public int getSpamDelayTask(UUID uuid) {
        return playerSpamTask.get(uuid);
    }

    public boolean isSpamLoggerViolation(UUID uuid, Component component) {
        if (!playerSpamLogger.containsKey(uuid)) return false;
        else return playerSpamLogger.get(uuid).equals(component);
    }
    public void resetSpamLogger(UUID uuid, Component component) {
        playerSpamLogger.put(uuid, component);
        playerSpamLoggerCount.put(uuid, 1);
    }
    public boolean addSpamLoggerViolationCount(UUID uuid) {
        if (!playerSpamLoggerCount.containsKey(uuid)) playerSpamLoggerCount.put(uuid, 1);
        else playerSpamLoggerCount.put(uuid, playerSpamLoggerCount.get(uuid) + 1);
        return playerSpamLoggerCount.get(uuid) >= Setting.SPAM_ATTEMPTS.getValue();
    }
    public PlayerMU getPlayerMU(UUID uuid) {
        if (playerMUList.containsKey(uuid)) {
            return playerMUList.get(uuid);
        } else {
            PlayerMU pmu = new PlayerMU(uuid);
            playerMUList.put(uuid, pmu);
            return pmu;
        }
    }

    public void addSpamDelay(UUID uuid) {
        if (isSpamTaskActive(uuid)) {
            Bukkit.getScheduler().cancelTask(getSpamDelayTask(uuid));
        }
        addSpamTaskActive(uuid, new BukkitRunnable() {
            @Override
            public void run() {
                removeSpamTaskActive(uuid);
            }
        }.runTaskLater(Punishments.getPlugin(), 10).getTaskId());
    }

    public void load() {
        //config
        FileManager fileManager = Punishments.getPlugin().getFileManager();
        String fileName = "config";
        fileManager.createYML(fileName);
        CustomYML configYML = fileManager.getYML(fileName);
        YamlConfiguration fileConfig = configYML.getFile();
        for (ConfigFile value : ConfigFile.values()) {
            if (!fileConfig.contains(value.getPath())) fileConfig.set(value.getPath(), value.getString());
        }
        configYML.saveFile();

        //muted commands
        muteCommands.addAll(EnumSet.allOf(MuteCommand.class).stream().map(MuteCommand::name).toList());
    }
}
