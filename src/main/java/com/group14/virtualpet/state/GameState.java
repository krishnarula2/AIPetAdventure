/**
 * Class representing the overall game state, including the pet's state and inventory.
 * Manages game progress and saving/loading game data.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet.state;

import java.io.Serializable;
import java.util.Date;

import com.group14.virtualpet.model.Inventory;
import com.group14.virtualpet.model.Pet;

/**
 * Represents the complete state of the game that can be saved or loaded.
 * Requirement: 3.1.5, 3.1.11.1, 3.1.11.2
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private Pet pet;
    private Inventory inventory;
    private int score; // Make score mutable if needed, e.g., for parental stats reset

    private Date playtimeStart;
    private Date playtimeEnd;

    // Parental Controls - Time Limit (Req 3.1.11.1)
    private boolean timeLimitEnabled = false;
    private int maxPlaytimeMinutes = 30; // Default limit if enabled

    // Parental Controls - Statistics (Req 3.1.11.2)
    private long totalPlaytimeMillis = 0;
    private int sessionCount = 0; // Added field to track the number of play sessions

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
        // Ensure at least 1 minute
        this.maxPlaytimeMinutes = Math.max(1, maxPlaytimeMinutes);
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
        this.sessionCount = 0;
        System.out.println("Playtime statistics reset.");
    }

    /** Gets the number of sessions played. */
    public int getSessionCount() {
        return sessionCount;
    }

    /** Calculates the average session length in milliseconds. */
    public long getAverageSessionMillis() {
        if (sessionCount == 0) {
            return 0;
        }
        return totalPlaytimeMillis / sessionCount;
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

    /**
     * Stops the session timer (e.g., when game is paused or saved) and updates playtime statistics.
     * This method adds the elapsed session time to total playtime and increments the session count.
     */
    public void stopSessionTimer() {
        if (currentSessionStartTimeMillis > 0) {
            long elapsedMillis = System.currentTimeMillis() - currentSessionStartTimeMillis;
            addPlaytimeMillis(elapsedMillis);
            sessionCount++;
            System.out.println("Session ended. Elapsed time: " + elapsedMillis +
                               " ms. Total sessions: " + sessionCount);
        }
        this.currentSessionStartTimeMillis = -1;
    }

    /** Calculates the elapsed time in the current session in minutes. */
    public long getElapsedSessionTimeMinutes() {
        if (currentSessionStartTimeMillis <= 0) {
            return 0; // Session not started or already stopped
        }
        long elapsedMillis = System.currentTimeMillis() - currentSessionStartTimeMillis;
        return elapsedMillis / (1000 * 60);
    }

    public Date getPlaytimeStart() {
        return playtimeStart;
    }
    
    public void setPlaytimeStart(Date playtimeStart) {
        this.playtimeStart = playtimeStart;
    }
    
    public Date getPlaytimeEnd() {
        return playtimeEnd;
    }
    
    public void setPlaytimeEnd(Date playtimeEnd) {
        this.playtimeEnd = playtimeEnd;
    }
}
