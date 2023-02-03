package cz.revivalo.playerwarps.warp;

import cz.revivalo.playerwarps.categories.Category;
import cz.revivalo.playerwarps.categories.CategoryManager;
import cz.revivalo.playerwarps.configuration.enums.Config;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Warp implements ConfigurationSerializable {
    private UUID warpID;
    private String name;
    private UUID owner;
    private Location location;
    private int rating;
    private Collection<UUID> reviewers;
    private int visits;
    private int todayVisits;
    private Category category;
    private int price;
    private String description;
    private boolean disabled;
    private boolean privateState;
    private long dateCreated;
    private ItemStack menuItem;
    private String stars;

    public Warp(Map<String, Object> map) {
        for (String key : map.keySet()){
            final Object value = map.get(key);
            switch (key){
                case "uuid": warpID = UUID.fromString((String) value);
                case "name": setName((String) value); break;
                case "owner-id": setOwner(UUID.fromString((String) value)); break;
                case "loc": setLocation((Location) value); break;
                case "lore": setDescription((String) value); break;
                case "category": setCategory(CategoryManager.getCategoryFromName((String) value)); break; // Možná přes Optional<>
                case "item": setMenuItem(new ItemStack(Material.valueOf((String) value))); break;
                case "ratings": setRating((int) value); break;
                case "reviewers": setReviewers(((List<String>) value).stream().map(UUID::fromString).collect(Collectors.toCollection(TreeSet::new))); break;
                case "visits": setVisits((int) value); break;
                case "private": setPrivateState((boolean) value); break;
                case "disabled": setDisabled((boolean) value); break;
                case "price": setPrice((int) value); break;
                case "date-created": setDateCreated((long) value); break;
            }
        }

        stars = Config.createRatingFormat(this);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(){{
            put("uuid", getWarpID().toString());
            put("name", getName());
            put("owner-id", getOwner().toString());
            put("loc", getLocation());
            put("lore", getDescription());
            put("item", getMenuItem().getType().name());
            put("ratings", getRating());
            put("reviewers", getReviewers().stream().map(UUID::toString).collect(Collectors.toList()));
            put("category", getCategory().getType());
            put("visits", getVisits());
            put("private", isPrivateState());
            put("disabled", isDisabled());
            put("price", getPrice());
            put("date-created", getDateCreated());
        }};
    }

    @Override
    public String toString(){return warpID.toString();}
}