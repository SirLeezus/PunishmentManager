package lee.code.punishments.commands.cmds;

import lee.code.core.util.bukkit.BukkitUtils;
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

public class MuteCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 1) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (targetPlayer != null) {
                UUID senderUUID = sender instanceof Player player ? player.getUniqueId() : UUID.fromString(Lang.SERVER_UUID.getString());
                UUID tUUID = targetPlayer.getUniqueId();
                String reason = BukkitUtils.buildStringFromArgs(args, 1).replaceAll("[^a-zA-Z0-9 ]", "");
                if (!reason.isBlank()) {
                    if (!cacheManager.isMuted(tUUID)) {
                        cacheManager.setMutedPlayer(tUUID, senderUUID, reason, true);
                        if (targetPlayer.isOnline()) {
                            Player tPlayer = targetPlayer.getPlayer();
                            if (tPlayer != null) tPlayer.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MUTED.getComponent(new String[] { reason })));
                        }
                        plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_MUTED_FOREVER.getComponent(new String[] { targetPlayer.getName(), reason })));
                    }
                }
            } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        return true;
    }
}
