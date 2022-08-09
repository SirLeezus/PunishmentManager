package lee.code.punishments.menusystem.menus;

import lee.code.punishments.Punishments;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.lists.Lang;
import lee.code.punishments.menusystem.Menu;
import lee.code.punishments.menusystem.PlayerMU;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BotCheckerMenu extends Menu {

    public BotCheckerMenu(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_BOT_CHECKER_TITLE.getComponent(null);
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Punishments plugin = Punishments.getPlugin();
        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        CacheManager cacheManager = plugin.getCacheManager();

        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null) return;
        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        if (clickedItem.equals(botChecker)) {
            cacheManager.setBotStatus(uuid, false);
            player.closeInventory();
            playClickSound(player);
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();
        int upper = 54;
        int lower = 0;
        int slot = (int) (Math.random() * (upper - lower)) + lower;
        inventory.setItem(slot, botChecker);
    }
}
