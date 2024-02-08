package dev.revivalo.playerwarps.user;

import org.bukkit.entity.Player;

public class User {
    private final Player player;
    private Object[] temp;

    public User(Player player, Object[] temp) {
        this.player = player;
        this.temp = temp;
    }

    public Object[] getTemp() {
        return temp;
    }

    public void setTemp(Object[] temp) {
        this.temp = temp;
    }

    public Player getPlayer() {
        return player;
    }
}
