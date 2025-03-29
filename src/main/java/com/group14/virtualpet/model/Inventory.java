package com.group14.virtualpet.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the player's inventory, holding counts of different items.
 * Requirement: 3.1.8
 */
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    // Using Item as key relies on Item's equals/hashCode implementation
    private final Map<Item, Integer> itemCounts;

    public Inventory() {
        this.itemCounts = new HashMap<>();
    }

    /**
     * Adds a specified amount of an item to the inventory.
     * @param item The item to add.
     * @param quantity The number of items to add (must be positive).
     */
    public void addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) {
            return; // Or throw exception
        }
        itemCounts.put(item, itemCounts.getOrDefault(item, 0) + quantity);
        System.out.println("Added " + quantity + "x " + item.getName() + " to inventory."); // Logging
    }

    /**
     * Removes one unit of the specified item from the inventory.
     * Does nothing if the item is not present or count is zero.
     * @param item The item to remove.
     * @return true if an item was successfully removed, false otherwise.
     */
    public boolean removeItem(Item item) {
        if (item == null || !itemCounts.containsKey(item)) {
            return false;
        }
        int currentCount = itemCounts.get(item);
        if (currentCount > 0) {
            if (currentCount == 1) {
                itemCounts.remove(item); // Remove entry if count reaches zero
            } else {
                itemCounts.put(item, currentCount - 1);
            }
            System.out.println("Removed 1x " + item.getName() + " from inventory."); // Logging
            return true;
        }
        return false;
    }

    /**
     * Gets the current count of a specific item in the inventory.
     * @param item The item to check.
     * @return The count of the item (0 if not present).
     */
    public int getItemCount(Item item) {
        return itemCounts.getOrDefault(item, 0);
    }

    /**
     * Gets an unmodifiable view of the items currently in the inventory and their counts.
     * Useful for displaying the inventory without allowing direct modification.
     * @return An unmodifiable map of items to their counts.
     */
    public Map<Item, Integer> getAllItems() {
        return Collections.unmodifiableMap(itemCounts);
    }

    /**
     * Gets a set of all unique Item objects currently in the inventory (with count > 0).
     * @return A set of items.
     */
    public Set<Item> getUniqueItems() {
        return Collections.unmodifiableSet(itemCounts.keySet());
    }

     /**
      * Returns a simple string representation of the inventory contents.
      */
     @Override
     public String toString() {
         if (itemCounts.isEmpty()) {
             return "Inventory{empty}";
         }
         StringBuilder sb = new StringBuilder("Inventory{");
         itemCounts.forEach((item, count) ->
             sb.append(item.getName()).append("=").append(count).append(", ")
         );
         // Remove trailing comma and space
         sb.setLength(sb.length() - 2);
         sb.append("}");
         return sb.toString();
     }

    // TODO: Add mechanism to obtain items (Req 3.1.8 - periodically, score-based, minigame?)

    // --- Save/Load Methods --- Requirement 3.1.5, 3.1.8
    // Removed to use Java Serialization

    // /**
    //  * Converts the Inventory state into a savable Map.
    //  * Saves items as a list of maps, each map representing an item and its count.
    //  * @return A Map containing the inventory data.
    //  */
    // public Map<String, Object> toSavableData() {
    //     Map<String, Object> data = new HashMap<>();
    //     List<Map<String, Object>> itemsList = new ArrayList<>();

    //     itemCounts.forEach((item, count) -> {
    //         Map<String, Object> itemEntry = item.toSavableData(); // Get item data
    //         itemEntry.put("count", count); // Add the count to the item's map
    //         itemsList.add(itemEntry);
    //     });

    //     data.put("items", itemsList);
    //     return data;
    // }

    // /**
    //  * Creates an Inventory instance from a savable Map.
    //  * Reconstructs items from the list of maps.
    //  * @param data The Map containing the inventory data.
    //  * @return A new Inventory instance, or null if data is invalid.
    //  */
    // public static Inventory fromSavableData(Map<String, Object> data) {
    //     if (data == null || !data.containsKey("items")) {
    //         return new Inventory(); // Return empty inventory if data is missing/invalid
    //     }

    //     Inventory inventory = new Inventory();
    //     try {
    //         @SuppressWarnings("unchecked") // Suppress warning for casting Object to List<Map<...>>
    //         List<Map<String, Object>> itemsList = (List<Map<String, Object>>) data.get("items");

    //         if (itemsList != null) {
    //             for (Map<String, Object> itemEntry : itemsList) {
    //                 Item item = Item.fromSavableData(itemEntry); // Recreate item
    //                 if (item != null && itemEntry.containsKey("count")) {
    //                     int count = ((Number) itemEntry.get("count")).intValue();
    //                     if (count > 0) {
    //                         // Use the internal map directly for loading to avoid logging messages
    //                         inventory.itemCounts.put(item, count);
    //                     }
    //                 }
    //             }
    //         }
    //     } catch (Exception e) {
    //         System.err.println("Error loading inventory data: " + e.getMessage());
    //         // Return partially loaded or empty inventory on error
    //         return inventory; // Or return null;
    //     }
    //     return inventory;
    // }
} 