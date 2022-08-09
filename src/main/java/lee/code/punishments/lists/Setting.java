package lee.code.punishments.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Setting {
    BOT_KICK_DELAY(300),
    SPAM_ATTEMPTS(5),
    AUTO_MUTE_TIME(1800),
    ;

    @Getter private final int value;
}
