package cz.revivalo.playerwarps.warp;

import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class Warp {
    private String name;
    private UUID owner;
    private Location loc;
    private String item;
    private int rating;
    private List<UUID> reviewers;
    private int visits;
    private int todayVisits;
    private String type;
    private int price;
    private String lore;
    private boolean disabled;
    private boolean privateState;
    private long dateCreated;

    public Warp(String name, UUID owner, Location loc, String item, int rating, List<UUID> reviewers, int visits, String type, int price, String lore, boolean disabled, boolean privateState, long dateCreated, int todayVisits) {
        setName(name);
        setOwner(owner);
        setLoc(loc);
        setItem(item);
        setRating(rating);
        setReviewers(reviewers);
        setVisits(visits);
        setTodayVisits(todayVisits);
        setType(type);
        setPrice(price);
        setLore(lore);
        setDisabled(disabled);
        setPrivateState(privateState);
        setDateCreated(dateCreated);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<UUID> getReviewers() {
        return reviewers;
    }

    public void setReviewers(List<UUID> reviewers) {
        this.reviewers = reviewers;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isPrivateState() {
        return privateState;
    }

    public void setPrivateState(boolean privateState) {
        this.privateState = privateState;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getTodayVisits() {
        return todayVisits;
    }

    public void setTodayVisits(int todayVisits) {
        this.todayVisits = todayVisits;
    }
}