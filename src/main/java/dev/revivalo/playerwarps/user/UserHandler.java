package dev.revivalo.playerwarps.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserHandler {
    public final static HashMap<UUID, User> USERS = new HashMap<>();
//    @NotNull
//    public static Set<Object> getUsersTemp(final UUID uuid){
//        final User user = USERS.remove(uuid);
//        if (user == null) return Collections.emptySet();
//        else return user.getData();
//    }
    public static void createUser(final Player player, final Map<DataSelectorType, Object> data){
        USERS.put(player.getUniqueId(), new User(player, data));
    }

    public static User getUser(final UUID uuid){
        if (USERS.containsKey(uuid)) return USERS.get(uuid);
        else {
            final User user = new User(Bukkit.getPlayer(uuid), null);
            USERS.put(uuid, user);
            return user;
        }
    }

    public static User getUser(final Player player){
        return getUser(player.getUniqueId());
    }
}
