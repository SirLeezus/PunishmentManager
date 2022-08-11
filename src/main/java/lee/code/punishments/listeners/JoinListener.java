package lee.code.punishments.listeners;

import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.punishments.Punishments;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.lists.Lang;
import lee.code.punishments.lists.Setting;
import lee.code.punishments.menusystem.menus.BotCheckerMenu;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        try {
            Punishments.getPlugin().getCacheManager().createDefaults(e.getUniqueId());
        } catch (NullPointerException exception) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Lang.ERROR_COULD_NOT_LOAD_PROFILE.getComponent(null));
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Punishments plugin = Punishments.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        CacheManager cacheManager = plugin.getCacheManager();

        //ban check
        if (cacheManager.isBanned(uuid)) {
            e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            e.kickMessage(Lang.BANNED.getComponent(new String[] { cacheManager.getBanReason(uuid) }));
        } else if (cacheManager.isTempBanned(uuid)) {
            e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            long secondsLeft = cacheManager.getTempBanTime(uuid);
            if (secondsLeft > 0) {
                e.kickMessage(Lang.TEMPBANNED.getComponent(new String[] { BukkitUtils.parseSeconds(secondsLeft), cacheManager.getBanReason(uuid) }));
            } else cacheManager.setTempBannedPlayer(uuid, null, "0", 0);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        //bot check
        if (cacheManager.isBotCheckEnabled() && cacheManager.isBot(uuid)) {
            new BotCheckerMenu(plugin.getData().getPlayerMU(uuid)).open();
            player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (cacheManager.isBot(uuid)) player.kick(Lang.BOT_CHECKER_KICK.getComponent(null));
            }, Setting.BOT_KICK_DELAY.getValue() * 20L);
        }
    }
}
