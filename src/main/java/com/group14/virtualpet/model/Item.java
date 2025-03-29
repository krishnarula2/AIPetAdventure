package com.group14.virtualpet.model;

import java.io.Serializable;
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

    // --- Save/Load Methods --- Requirement 3.1.5, 3.1.8
    // Removed to use Java Serialization

    // /**
    //  * Returns a Map representation of the item for saving.
    //  * Subclasses must override this to add their specific data and item type.
    //  * @return A Map containing the item's data.
    //  */
    // public Map<String, Object> toSavableData() {
    //     Map<String, Object> data = new HashMap<>();
    //     data.put("name", name);
    //     // Subclasses will add "itemType" and specific properties
    //     return data;
    // }

    // /**
    //  * Creates an Item instance from a savable Map.
    //  * Delegates to specific subclass implementations based on "itemType".
    //  * @param data The Map containing the item's data.
    //  * @return An Item instance, or null if data is invalid or type is unknown.
    //  */
    // public static Item fromSavableData(Map<String, Object> data) {
    //     if (data == null || !data.containsKey("itemType")) {
    //         return null;
    //     }
    //     String itemType = (String) data.get("itemType");

    //     try {
    //         switch (itemType) {
    //             case "FOOD":
    //                 return FoodItem.fromSavableData(data);
    //             case "GIFT":
    //                 return GiftItem.fromSavableData(data);
    //             default:
    //                 System.err.println("Unknown item type in save data: " + itemType);
    //                 return null;
    //         }
    //     } catch (Exception e) {
    //         System.err.println("Error loading item from data: " + e.getMessage());
    //         return null;
    //     }
    // }
} 