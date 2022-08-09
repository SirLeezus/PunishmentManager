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
import java.util.concurrent.TimeUnit;

public class TempMuteCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 2) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (targetPlayer != null) {
                UUID senderUUID = sender instanceof Player player ? player.getUniqueId() : UUID.fromString(Lang.SERVER_UUID.getString());
                UUID tUUID = targetPlayer.getUniqueId();
                if (!cacheManager.isTempMuted(tUUID) && !cacheManager.isMuted(tUUID)) {
                    long secondsBanned = BukkitUtils.serializeSeconds(args[1]);
                    if (secondsBanned != 0) {
                        long milliseconds = System.currentTimeMillis();
                        long time = TimeUnit.MILLISECONDS.toSeconds(milliseconds) + secondsBanned;
                        String reason = BukkitUtils.buildStringFromArgs(args, 2).replaceAll("[^a-zA-Z0-9 ]", "");
                        if (!reason.isBlank()) {
                            cacheManager.setTempMutedPlayer(tUUID, senderUUID, reason, time);
                            if (targetPlayer.isOnline()) {
                                Player tPlayer = targetPlayer.getPlayer();
                                if (tPlayer != null) tPlayer.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TEMPMUTED.getComponent(new String[] { BukkitUtils.parseSeconds(secondsBanned), reason })));
                            }
                            plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_TEMPMUTED.getComponent(new String[] { targetPlayer.getName(), BukkitUtils.parseSeconds(secondsBanned), reason })));
                        }
                    }
                }
            } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        return true;
    }
}
