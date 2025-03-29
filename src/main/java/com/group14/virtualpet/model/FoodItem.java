package com.group14.virtualpet.model;

import java.io.Serializable;

/**
 * Represents a food item that increases fullness.
 * Requirement: 3.1.8a
 */
public class FoodItem extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int fullnessValue;

    public FoodItem(String name, int fullnessValue) {
        super(name);
        this.fullnessValue = fullnessValue;
    }

    public int getFullnessValue() {
        return fullnessValue;
    }

    @Override
    public String toString() {
        return getName() + " (+ " + fullnessValue + " Fullness)";
    }

    // Note: equals/hashCode are inherited from Item and should work correctly
    // if names are unique per item type.

    // --- Save/Load Methods --- Requirement 3.1.5, 3.1.8
    // Removed to use Java Serialization

    // @Override
    // public Map<String, Object> toSavableData() {
    //     Map<String, Object> data = super.toSavableData();
    //     data.put("itemType", "FOOD");
    //     data.put("fullnessValue", fullnessValue);
    //     return data;
    // }

    // /**
    //  * Creates a FoodItem instance from a savable Map.
    //  * Assumes the map contains the necessary keys ("name", "fullnessValue").
    //  * @param data The Map containing the item's data.
    //  * @return A new FoodItem instance, or null if data is invalid.
    //  */
    // public static FoodItem fromSavableData(Map<String, Object> data) {
    //     if (data == null || !data.containsKey("name") || !data.containsKey("fullnessValue")) {
    //         return null;
    //     }
    //     try {
    //         String name = (String) data.get("name");
    //         int fullnessValue = ((Number) data.get("fullnessValue")).intValue();
    //         return new FoodItem(name, fullnessValue);
    //     } catch (Exception e) {
    //         System.err.println("Error loading FoodItem data: " + e.getMessage());
    //         return null;
    //     }
    // }
} 