package lee.code.punishments.listeners;

import lee.code.punishments.Data;
import lee.code.punishments.Punishments;
import lee.code.punishments.lists.Lang;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;


public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandMute(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Punishments plugin = Punishments.getPlugin();
        Data data = plugin.getData();
        String[] commandParts =  e.getMessage().toUpperCase().split(" ", 2);
        String command = commandParts[0].replace("/", "");
        Component mute = plugin.getCacheManager().shouldMute(player.getUniqueId());
        if (mute != null) {
            if (data.getMuteCommands().contains(command)) {
                e.setCancelled(true);
                player.sendMessage(mute);
                return;
            }
        }
        if (data.isSpamTaskActive(uuid)) {
            e.setCancelled(true);
            data.addSpamDelay(uuid);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.SPAM_COMMAND.getComponent(null)));
        } else data.addSpamDelay(uuid);
    }
}
