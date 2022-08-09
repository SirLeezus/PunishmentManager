package lee.code.punishments.listeners;

import lee.code.punishments.Punishments;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandMute(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        Punishments plugin = Punishments.getPlugin();
        String[] commandParts =  e.getMessage().toUpperCase().split(" ", 2);
        String command = commandParts[0].replace("/", "");
        Component mute = plugin.getCacheManager().shouldMute(player.getUniqueId());
        if (mute != null) {
            if (plugin.getData().getMuteCommands().contains(command)) {
                e.setCancelled(true);
                player.sendMessage(mute);
            }
        }
    }
}
