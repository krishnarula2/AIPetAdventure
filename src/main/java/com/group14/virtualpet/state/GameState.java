package com.group14.virtualpet.state;

import java.io.Serializable;

import com.group14.virtualpet.model.Inventory;
import com.group14.virtualpet.model.Pet;

/**
 * Represents the complete state of the game that can be saved or loaded.
 * Requirement: 3.1.5, 3.1.11.1
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private Pet pet;
    private Inventory inventory;
    private int score; // Make score mutable if needed, e.g., for parental stats reset

    // Parental Controls - Time Limit (Req 3.1.11.1)
    private boolean timeLimitEnabled = false;
    private int maxPlaytimeMinutes = 30; // Default limit if enabled

    // Parental Controls - Statistics (Req 3.1.11.2)
    private long totalPlaytimeMillis = 0;
    // TODO: Add session count? last played date?

    // Transient field: Not saved, reset on load/start
    private transient long currentSessionStartTimeMillis = -1; // -1 indicates session not started

    /**
     * Creates a GameState snapshot.
     * @param pet The current pet.
     * @param inventory The current inventory.
     * @param score The current score.
     */
    public GameState(Pet pet, Inventory inventory, int score) {
        this.pet = pet;
        this.inventory = inventory;
        this.score = score;
        // Time limits default to disabled (false, 30 min)
        // Session start time is implicitly -1
    }

    public Pet getPet() {
        return pet;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getScore() {
        return score;
    }

    // --- Time Limit Getters/Setters (Req 3.1.11.1) ---

    public boolean isTimeLimitEnabled() {
        return timeLimitEnabled;
    }

    public void setTimeLimitEnabled(boolean timeLimitEnabled) {
        this.timeLimitEnabled = timeLimitEnabled;
    }

    public int getMaxPlaytimeMinutes() {
        return maxPlaytimeMinutes;
    }

    public void setMaxPlaytimeMinutes(int maxPlaytimeMinutes) {
        // Add validation if desired (e.g., ensure positive)
        this.maxPlaytimeMinutes = Math.max(1, maxPlaytimeMinutes); // Ensure at least 1 minute
    }

    // --- Statistics Getters/Setters/Modifiers (Req 3.1.11.2) ---

    public long getTotalPlaytimeMillis() {
        return totalPlaytimeMillis;
    }

    /** Adds the specified milliseconds to the total playtime. */
    public void addPlaytimeMillis(long millisToAdd) {
        if (millisToAdd > 0) {
            this.totalPlaytimeMillis += millisToAdd;
        }
    }

    /** Resets the tracked playtime statistics. */
    public void resetPlaytimeStats() {
        this.totalPlaytimeMillis = 0;
        // TODO: Reset session count etc. if added
        System.out.println("Playtime statistics reset.");
    }

    // --- Session Time Management (Internal, used by GameplayPanel) ---

    /** Gets the start time of the current session in milliseconds since epoch. */
    public long getCurrentSessionStartTimeMillis() {
        return currentSessionStartTimeMillis;
    }

    /** Resets the session timer, called when a new game/session starts. */
    public void startSessionTimer() {
        this.currentSessionStartTimeMillis = System.currentTimeMillis();
        System.out.println("Session timer started at: " + this.currentSessionStartTimeMillis);
    }

    /** Stops the session timer (e.g., when game is paused or saved). */
    public void stopSessionTimer() {
         // We don't actually need to *stop* the timer, just record the start.
         // Resetting to -1 could indicate the session is inactive.
        this.currentSessionStartTimeMillis = -1;
        System.out.println("Session timer stopped/reset.");
    }

    /** Calculates the elapsed time in the current session in minutes. */
    public long getElapsedSessionTimeMinutes() {
        if (currentSessionStartTimeMillis <= 0) {
            return 0; // Session not started or stopped
        }
        long elapsedMillis = System.currentTimeMillis() - currentSessionStartTimeMillis;
        return elapsedMillis / (1000 * 60);
    }

    // TODO: Add methods/fields for Parental Statistics (Play Time) - Req 3.1.11.2
}