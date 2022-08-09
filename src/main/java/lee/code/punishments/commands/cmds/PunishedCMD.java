package lee.code.punishments.commands.cmds;

import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.punishments.Punishments;
import lee.code.punishments.database.CacheManager;
import lee.code.punishments.lists.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PunishedCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Punishments plugin = Punishments.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        int index;
        int maxDisplayed = 10;
        int page = 0;

        //page check
        if (args.length > 0) {
            if (BukkitUtils.containOnlyNumbers(args[0])) {
                page = Integer.parseInt(args[0]);
            } else {
                sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_LIST_PAGE_NOT_NUMBER.getComponent(new String[]{ args[2]} )));
                return true;
            }
        }

        if (page < 0) return true;

        Map<UUID, Long> bannedPlayers = cacheManager.getPunishList();
        HashMap<UUID, Long> sortedMap = BukkitUtils.sortByLong(bannedPlayers);
        List<UUID> players = new ArrayList<>(sortedMap.keySet());

        if (players.isEmpty()) {
            sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_PUNISHED_NO_BANS.getComponent(null)));
            return true;
        }

        List<Component> lines = new ArrayList<>();

        lines.add(Lang.COMMAND_PUNISHED_TITLE.getComponent(null));
        lines.add(Component.text(""));

        for (int i = 0; i < maxDisplayed; i++) {
            index = maxDisplayed * page + i;
            if (index >= players.size()) break;
            if (players.get(index) != null) {
                int position = index + 1;
                UUID pUUID = players.get(index);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(pUUID);
                String name = "null";
                String staff = "Console";
                String time = "Forever";
                String type = "None";
                String reason = "None";
                if (offlinePlayer.getName() != null) name = offlinePlayer.getName();
                if (cacheManager.isBanned(pUUID)) {
                    reason = cacheManager.getBanReason(pUUID);
                    staff = cacheManager.getBanStaffName(pUUID);
                    type = "&eBanned";
                } else if (cacheManager.isTempBanned(pUUID)) {
                    long timeBanned = cacheManager.getTempBanTime(pUUID);
                    if (timeBanned < 0) timeBanned = 0;
                    time = BukkitUtils.parseSeconds(timeBanned);
                    reason = cacheManager.getBanReason(pUUID);
                    staff = cacheManager.getBanStaffName(pUUID);
                    type = "&eTempBanned";
                } else if (cacheManager.isMuted(pUUID)) {
                    reason = cacheManager.getMuteReason(pUUID);
                    staff = cacheManager.getMuteStaffName(pUUID);
                    type = "&eMuted";
                } else if (cacheManager.isTempMuted(pUUID)) {
                    long timeMuted = cacheManager.getTempMuteTime(pUUID);
                    if (timeMuted < 0) timeMuted = 0;
                    time = BukkitUtils.parseSeconds(timeMuted);
                    reason = cacheManager.getMuteReason(pUUID);
                    staff = cacheManager.getMuteStaffName(pUUID);
                    type = "&eTempMuted";
                }
                lines.add(BukkitUtils.parseColorComponent("&3" + position + ". &6" + name + " &3Type&7: " + type + " &3Time&7: " + time)
                        .hoverEvent(BukkitUtils.parseColorComponent("&3Date: &7" + BukkitUtils.getDate(cacheManager.getBanDate(pUUID)) + "\n&3Staff Member: &7" + staff + "\n&3Reason: &7" + reason)));
            }
        }

        if (lines.size() <= 2) return true;

        lines.add(Component.text(""));
        Component next = Lang.NEXT_PAGE_TEXT.getComponent(null).hoverEvent(Lang.NEXT_PAGE_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/punished " + (page + 1)));
        Component spacer = Lang.PAGE_SPACER.getComponent(null);
        Component prev = Lang.PREVIOUS_PAGE_TEXT.getComponent(null).hoverEvent(Lang.PREVIOUS_PAGE_HOVER.getComponent(null)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/punished " + (page - 1)));
        lines.add(prev.append(spacer).append(next));

        for (Component message : lines) sender.sendMessage(message);
        return true;
    }
}
