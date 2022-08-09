package lee.code.punishments.commands.cmds;

import lee.code.punishments.Punishments;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UnBanCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 0) {
            OfflinePlayer tPlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (tPlayer != null) {
                UUID tUUID = tPlayer.getUniqueId();
                if (cacheManager.isBanned(tUUID)) {
                    cacheManager.setBannedPlayer(tUUID, null, "0", false);
                    plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_UNBANNED.getComponent(new String[] { tPlayer.getName() })));
                } else if (cacheManager.isTempBanned(tUUID)) {
                    cacheManager.setTempBannedPlayer(tUUID, null, "0", 0);
                    plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_UNBANNED.getComponent(new String[] { tPlayer.getName() })));
                }
            } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        return true;
    }
}
