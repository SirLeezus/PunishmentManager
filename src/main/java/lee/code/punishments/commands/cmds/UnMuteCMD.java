package lee.code.punishments.commands.cmds;

import lee.code.punishments.Punishments;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UnMuteCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 0) {
            OfflinePlayer tPlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (tPlayer != null) {
                UUID senderUUID = sender instanceof Player player ? player.getUniqueId() : UUID.fromString(Lang.SERVER_UUID.getString());
                UUID tUUID = tPlayer.getUniqueId();
                if (cacheManager.isMuted(tUUID)) {
                    cacheManager.setMutedPlayer(tUUID, senderUUID, "0", false);
                    plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_UNMUTED.getComponent(new String[] { tPlayer.getName() })));
                } else if (cacheManager.isTempMuted(tUUID)) {
                    cacheManager.setTempMutedPlayer(tUUID,senderUUID, "0", 0);
                    plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_UNMUTED.getComponent(new String[] { tPlayer.getName() })));
                }
            } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        return true;
    }
}
