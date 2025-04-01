/**
 * Abstract class representing a generic item for the virtual pet game.
 * Can be extended by specific item types like FoodItem and GiftItem.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract base class for items in the game.
 * Requirement: 3.1.8
 */
public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // It's crucial to implement equals and hashCode if items are used as keys in Maps (like in Inventory)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
    
    public Map<String, Object> toSavableData() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        return data;
    }

    public static Item fromSavableData(Map<String, Object> data) {
        if (data == null || !data.containsKey("itemType")) {
            return null;
        }
        String itemType = (String) data.get("itemType");

        try {
            switch (itemType) {
                case "FOOD":
                    return FoodItem.fromSavableData(data);
                case "GIFT":
                    return GiftItem.fromSavableData(data);
                default:
                    System.err.println("Unknown item type in save data: " + itemType);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading item from data: " + e.getMessage());
            return null;
        }
    }
} 