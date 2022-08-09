package lee.code.punishments.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.punishments.Punishments;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum ConfigFile {
    MYSQL_IP("mysql-ip", "localhost"),
    MYSQL_USER("mysql-user", "admin"),
    MYSQL_PASSWORD("mysql-password", "00000000"),
    MYSQL_PORT("mysql-port", "5555"),
    MYSQL_DATABASE("mysql-database", "database"),
    ;

    @Getter private final String path;
    @Getter private final String string;

    public String getString(String[] variables) {
        return BukkitUtils.getStringFromFileManager(Punishments.getPlugin().getFileManager(), "config", path, variables);
    }

    public int getValue() {
        return BukkitUtils.getValueFromFileManager(Punishments.getPlugin().getFileManager(),"config", path);
    }

    public void setValue(int value) {
        BukkitUtils.setValueInFileManager(Punishments.getPlugin().getFileManager(),"config", path, value);
    }
}
