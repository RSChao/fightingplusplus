package com.rschao.plugins.fightingpp.events.definitions;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUseUltimate extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    public String fruitName;
    public boolean isCancelled;
    public Player user;
    public PlayerUseUltimate(String techName, Player user) {
        this.fruitName = techName;
        this.user = user;
        this.isCancelled = false;
    }
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public String getFruitId() {
        return fruitName;
    }
    public Player getUser() {
        return user;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    @Override
    public void setCancelled(boolean arg0) {
        isCancelled = arg0;
    }
    public String getEventAsString(boolean json) {
        if(json) {
            return "{\"techName\":\"" + fruitName + "\",\"user\":\"" + user.getName() + "\"}";
        } else {
            return "PlayerUseUltimate: " + fruitName + " used by " + user.getName();
        }
    }     
}

