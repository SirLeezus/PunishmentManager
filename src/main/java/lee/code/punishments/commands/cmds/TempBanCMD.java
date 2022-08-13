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

public class TempBanCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
            UUID senderUUID = sender instanceof Player player ? player.getUniqueId() : UUID.fromString(Lang.SERVER_UUID.getString());
            if (target != null) {
                UUID tUUID = target.getUniqueId();
                if (!cacheManager.isTempBanned(tUUID) && !cacheManager.isBanned(tUUID)) {
                    long secondsBanned = BukkitUtils.serializeSeconds(args[1]);
                    if (secondsBanned != 0) {
                        long milliseconds = System.currentTimeMillis();
                        long time = TimeUnit.MILLISECONDS.toSeconds(milliseconds) + secondsBanned;
                        String reason = BukkitUtils.buildStringFromArgs(args, 2).replaceAll("[^a-zA-Z0-9 ]", "");
                        if (!reason.isBlank()) {
                            cacheManager.setTempBannedPlayer(tUUID, senderUUID, reason, time);
                            if (target.isOnline()) {
                                Player tPlayer = target.getPlayer();
                                if (tPlayer != null) BukkitUtils.kickNetwork(tPlayer, Lang.TEMPBANNED.getString(new String[] { BukkitUtils.parseSeconds(secondsBanned), reason }));
                            }
                            plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_TEMPBANNED.getComponent(new String[] { target.getName(), BukkitUtils.parseSeconds(secondsBanned), reason })));
                        }
                    }
                }
            } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        return true;
    }
}
