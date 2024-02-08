package dev.revivalo.playerwarps.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    public final static HashMap<UUID, User> USERS = new HashMap<>();
    @NotNull
    public static Object[] getUsersTemp(final UUID uuid){
        final User user = USERS.remove(uuid);
        if (user == null) return null;
        else return user.getTemp();
    }
    public static void createUser(final Player player, final Object[] data){
        USERS.put(player.getUniqueId(), new User(player, data));
    }
    public User getUser(final UUID uuid){
        if (USERS.containsKey(uuid)) return USERS.get(uuid);
        else {
            final User user = new User(Bukkit.getPlayer(uuid), null);
            USERS.put(uuid, user);
            return user;
        }
    }

    public User getUser(final Player player){
        return getUser(player.getUniqueId());
    }
}
