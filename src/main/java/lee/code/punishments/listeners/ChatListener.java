package lee.code.punishments.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.punishments.Data;
import lee.code.punishments.Punishments;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.lists.Lang;
import lee.code.punishments.lists.Setting;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatMuted(AsyncChatEvent e) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        Data data = plugin.getData();

        Player player = e.getPlayer();
        UUID uuid = e.getPlayer().getUniqueId();

        Component mute = cacheManager.shouldMute(uuid);
        if (mute != null) {
            player.sendMessage(mute);
            e.setCancelled(true);
        } else if (data.isSpamTaskActive(uuid)) {
            e.setCancelled(true);
            data.addSpamDelay(uuid);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.SPAM_CHAT.getComponent(null)));
        } else if (data.isSpamLoggerViolation(uuid, e.message())) {
            if (data.addSpamLoggerViolationCount(uuid)) {
                e.setCancelled(true);
                String reason = "Chat Spam";
                int timeMuted = Setting.AUTO_MUTE_TIME.getValue();
                long milliseconds = System.currentTimeMillis();
                long time = TimeUnit.MILLISECONDS.toSeconds(milliseconds) + timeMuted;
                cacheManager.setTempMutedPlayer(uuid, UUID.fromString(Lang.SERVER_UUID.getString()), reason, time);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.TEMPMUTED.getComponent(new String[]{BukkitUtils.parseSeconds(timeMuted), reason})));
                plugin.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_TEMPMUTED.getComponent(new String[]{player.getName(), BukkitUtils.parseSeconds(timeMuted), reason})));
            } else data.addSpamDelay(uuid);
        } else {
            data.addSpamDelay(uuid);
            data.resetSpamLogger(uuid, e.message());
        }
    }
}
