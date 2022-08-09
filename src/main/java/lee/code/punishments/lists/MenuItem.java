package lee.code.punishments.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum MenuItem {

    BOT_CHECKER(Material.LIME_STAINED_GLASS_PANE, Lang.MENU_BOT_CHECKER_ITEM_NAME.getString(), Lang.MENU_BOT_CHECKER_ITEM_LORE.getString()),
    FILLER_GLASS(Material.BLACK_STAINED_GLASS_PANE, "&r", null),
    TRADE_COUNT_DOWN(Material.LIME_STAINED_GLASS_PANE, "&r", null),
    NEXT_PAGE(Material.PAPER, "&eNext Page >", null),
    PREVIOUS_PAGE(Material.PAPER, "&e< Previous Page", null),

    ;

    @Getter private final Material type;
    @Getter private final String name;
    @Getter private final String lore;

    public ItemStack getItem() {
        return BukkitUtils.getItem(type, name, lore, null, true);
    }
}
