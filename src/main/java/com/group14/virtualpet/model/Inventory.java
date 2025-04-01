/**
 * Class representing the virtual pet's inventory, which contains various items.
 * Allows items to be added, removed, and used by the pet.
 * 
 * @author Group 14
 * @version 1.0
 */

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

} 