package dev.revivalo.playerwarps.user;

import org.bukkit.entity.Player;

import java.util.Map;

public class User {
    private final Player player;
    private Map<DataSelectorType, Object> data;

    public User(Player player, Map<DataSelectorType, Object> data) {
        this.player = player;
        this.data = data;
    }

    public Map<DataSelectorType, Object> getData() {
        return data;
    }

    public Object getData(DataSelectorType selection) {
        return data.get(selection);
    }

    public User addData(DataSelectorType key, Object object) {
        // if (key == WA) TODO: Logic for previous and actual opened menu
        data.put(key, object);
        return this;
    }

//    public Menu getActualMenu() {
//        return (Menu) getData(DataSelectorType.ACTUAL_PAGE_TEST);
//    }
//
//    public void setActualMenu(Menu actualMenu) {
//        setPreviousMenu(getActualMenu());
//        addData(DataSelectorType.ACTUAL_PAGE_TEST, actualMenu);
//    }
//
//
//    public Menu getPreviousMenu() {
//        return (Menu) getData().get(DataSelectorType.PREVIOUS_PAGE_TEST);
//    }
//
//    public void setPreviousMenu(Menu previousMenu) {
//        addData(DataSelectorType.PREVIOUS_PAGE_TEST, previousMenu);
//    }

    public void setData(Map<DataSelectorType, Object> data) {
        this.data = data;
    }

    public Player getPlayer() {
        return player;
    }
}
