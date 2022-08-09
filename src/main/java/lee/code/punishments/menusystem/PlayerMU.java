package lee.code.punishments.menusystem;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerMU {

    private final UUID owner;
    public Player getOwner() { return Bukkit.getPlayer(owner); }
    public UUID getOwnerUUID() { return owner; }
}
