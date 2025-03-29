package com.group14.virtualpet.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents a gift item that increases happiness.
 * Requirement: 3.1.8b
 */
public class GiftItem extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int happinessValue;

    public GiftItem(String name, int happinessValue) {
        super(name);
        this.happinessValue = happinessValue;
    }

    public int getHappinessValue() {
        return happinessValue;
    }

    @Override
    public String toString() {
        return getName() + " (+ " + happinessValue + " Happiness)";
    }

    // Note: equals/hashCode are inherited from Item and should work correctly
    // if names are unique per item type.

    @Override
    public Map<String, Object> toSavableData() {
        Map<String, Object> data = super.toSavableData();
        data.put("itemType", "GIFT");
        data.put("happinessValue", happinessValue);
        return data;
    }

    /**
     * Creates a GiftItem instance from a savable Map.
     * @param data The Map containing the item's data
     * @return A new GiftItem instance, or null if data is invalid
     */
    public static GiftItem fromSavableData(Map<String, Object> data) {
        if (data == null || !data.containsKey("name") || !data.containsKey("happinessValue")) {
            return null;
        }
        try {
            String name = (String) data.get("name");
            int happinessValue = ((Number) data.get("happinessValue")).intValue();
            return new GiftItem(name, happinessValue);
        } catch (Exception e) {
            System.err.println("Error loading GiftItem data: " + e.getMessage());
            return null;
        }
    }
} 