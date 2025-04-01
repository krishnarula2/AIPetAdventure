/**
 * Class representing the state of the pet, such as its health, hunger, and mood.
 * This class holds the various attributes that describe the current status of the pet.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet.model;

import java.io.Serializable;

/**
 * Represents the possible states a Pet can be in.
 * Requirements: 3.1.6, 3.1.7
 */
public enum PetState implements Serializable {
    NORMAL,     // Default state
    SLEEPING,   // Pet is asleep (Sleep stat recovery)
    HUNGRY,     // Fullness is zero (Happiness declines faster, Health may decrease)
    ANGRY,      // Happiness is zero (Refuses most commands)
    DEAD;       // Health is zero (Game Over for this pet)

    // Adding serialVersionUID for Serializable interface
    private static final long serialVersionUID = 1L;
} 