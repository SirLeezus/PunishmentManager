package lee.code.punishments.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum Lang {

    PREFIX("&4&lPunishments &e➔ &r"),
    ANNOUNCEMENT("&e&lAnnouncement &6➔ &r"),
    USAGE("&6&lUsage: &e{0}"),
    SERVER_UUID("ffffffff-ffff-ffff-ffff-ffffffffffff"),
    SPAM_COMMAND("&cPlease slow down the speed you're requesting commands."),
    SERVER_RESTART("&aThe server is restarting! Journey Chaos will be back online soon!"),
    MUTED("&cYou have been muted from chat forever for: &7{0}"),
    TEMPMUTED("&cYou have been temporarily muted from chat for &e{0} &cfor: &7{1}"),
    TEMPUNMUTED("&aYour temporary mute is now over. Please follow our rules."),
    KICKED("&cYou have been kicked from the server for: &7{0}"),
    BOTMUTED("&cYou need to verify you're not a bot. Relog if you don't see a menu."),
    BANNED("&cYou have been banned from the server forever for: &7{0}"),
    TEMPBANNED("&cYou have been temporarily banned from the server for &e{0} &cfor: &7{1}"),
    BOT_CHECKER_KICK("&cYou failed to verify you're not a bot so you've been kicked."),
    MENU_BOT_CHECKER_ITEM_LORE("&7Click to verify you're not a bot."),
    MENU_BOT_CHECKER_ITEM_NAME("&6&lPlayer Verification"),
    MENU_BOT_CHECKER_TITLE("&e&lPlayer Verification"),
    BROADCAST_BANNED_FOREVER("&cThe player &6{0} &chas been banned forever for: &7{1}"),
    BROADCAST_TEMPBANNED("&cThe player &6{0} &chas been banned for &e{1} &cfor: &7{2}"),
    BROADCAST_KICKED("&cThe player &6{0} &chas been kicked from the server for: &7{1}"),
    BROADCAST_MUTED_FOREVER("&cThe player &6{0} &chas been muted forever for: &7{1}"),
    BROADCAST_TEMPMUTED("&cThe player &6{0} &chas been muted for {1}&c for: &7{2}"),
    BROADCAST_UNBANNED("&aThe player &6{0} &ahas been unbanned!"),
    BROADCAST_UNMUTED("&aThe player &6{0} &ahas been unmuted!"),
    NEXT_PAGE_TEXT("&2&lNext &a&l>>--------"),
    SPAM_CHAT("&cPlease slow down the speed you're sending messages."),
    PREVIOUS_PAGE_TEXT("&a&l--------<< &2&lPrev"),
    PAGE_SPACER(" &e| "),
    NEXT_PAGE_HOVER("&6&lNext Page"),
    PREVIOUS_PAGE_HOVER("&6&lPrevious Page"),
    COMMAND_PUNISHED_TITLE("&a--------- &e[ &2&lPunished Players &e] &a---------"),
    COMMAND_PUNISHED_NO_BANS("&aThere are currently no players banned."),
    ERROR_LIST_PAGE_NOT_NUMBER("&cThe input &6{0} &cis not a number. &a&lUse&7: &e(1)"),
    ERROR_PLAYER_NOT_FOUND("&cThe player &6{0} &ccould not be found."),
    ERROR_PLAYER_NOT_ONLINE("&cThe player &6{0} &cis not online."),

    ;

    @Getter private final String string;

    public String getString(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return BukkitUtils.parseColorString(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return BukkitUtils.parseColorString(value);
    }

    public Component getComponent(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return BukkitUtils.parseColorComponent(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return BukkitUtils.parseColorComponent(value);
    }
}
