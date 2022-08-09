package lee.code.punishments;

import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.core.util.files.FileManager;
import lee.code.punishments.commands.cmds.*;
import lee.code.punishments.commands.tabs.*;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.database.DatabaseManager;
import lee.code.punishments.listeners.ChatListener;
import lee.code.punishments.listeners.CommandListener;
import lee.code.punishments.listeners.JoinListener;
import lee.code.punishments.lists.Lang;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Punishments extends JavaPlugin {

    @Getter private DatabaseManager databaseManager;
    @Getter private CacheManager cacheManager;
    @Getter private FileManager fileManager;
    @Getter private Data data;

    @Override
    public void onEnable() {
        this.fileManager = new FileManager(this);
        this.databaseManager = new DatabaseManager();
        this.cacheManager = new CacheManager();
        this.data = new Data();

        registerCommands();
        registerListeners();
        data.load();
        databaseManager.initialize();
        cacheManager.createServerData();
    }

    @Override
    public void onDisable() {
        BukkitUtils.kickOnlinePlayers(Lang.SERVER_RESTART.getComponent(null));
        databaseManager.closeConnection();
    }

    private void registerCommands() {
        getCommand("ban").setExecutor(new BanCMD());
        getCommand("ban").setTabCompleter(new BanTab());
        getCommand("unban").setExecutor(new UnBanCMD());
        getCommand("unban").setTabCompleter(new UnBanTab());
        getCommand("mute").setExecutor(new MuteCMD());
        getCommand("mute").setTabCompleter(new MuteTab());
        getCommand("unmute").setExecutor(new UnMuteCMD());
        getCommand("unmute").setTabCompleter(new UnMuteTab());
        getCommand("kick").setExecutor(new KickCMD());
        getCommand("kick").setTabCompleter(new KickTab());
        getCommand("punished").setExecutor(new PunishedCMD());
        getCommand("punished").setTabCompleter(new PunishedTab());
        getCommand("tempban").setExecutor(new TempBanCMD());
        getCommand("tempban").setTabCompleter(new TempBanTab());
        getCommand("tempmute").setExecutor(new TempMuteCMD());
        getCommand("tempmute").setTabCompleter(new TempBanTab());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }

    public static Punishments getPlugin() {
        return Punishments.getPlugin(Punishments.class);
    }
}
