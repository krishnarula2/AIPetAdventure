package com.group14.virtualpet.factory;

import com.group14.virtualpet.model.EasyPet;
import com.group14.virtualpet.model.HardPet;
import com.group14.virtualpet.model.MediumPet;
import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.model.PetType;

/**
 * Factory class responsible for creating instances of different Pet types.
 * Encapsulates the logic for choosing which concrete Pet subclass to instantiate.
 */
public class PetFactory {

    /**
     * Creates a new Pet instance based on the specified type and name.
     * Retrieves default characteristics from the PetType enum.
     *
     * @param type The type of pet to create (DOG, CAT, ROBOT, etc.).
     * @param name The name to give the new pet.
     * @return A new Pet object of the specified concrete type.
     * @throws IllegalArgumentException if the PetType is unknown or null.
     */
    public static Pet createPet(PetType type, String name) {
        if (type == null) {
            throw new IllegalArgumentException("PetType cannot be null.");
        }
        if (name == null || name.trim().isEmpty()) {
             throw new IllegalArgumentException("Pet name cannot be null or empty.");
        }

        switch (type) {
            case EASY:
                return new EasyPet(name, type.getMaxHealth(), type.getInitialHealth(),
                             type.getMaxSleep(), type.getInitialSleep(), type.getSleepDecayRate(),
                             type.getMaxFullness(), type.getInitialFullness(), type.getFullnessDecayRate(),
                             type.getMaxHappiness(), type.getInitialHappiness(), type.getHappinessDecayRate());
            case MEDIUM:
                return new MediumPet(name, type.getMaxHealth(), type.getInitialHealth(),
                             type.getMaxSleep(), type.getInitialSleep(), type.getSleepDecayRate(),
                             type.getMaxFullness(), type.getInitialFullness(), type.getFullnessDecayRate(),
                             type.getMaxHappiness(), type.getInitialHappiness(), type.getHappinessDecayRate());
            case HARD:
                return new HardPet(name, type.getMaxHealth(), type.getInitialHealth(),
                               type.getMaxSleep(), type.getInitialSleep(), type.getSleepDecayRate(),
                               type.getMaxFullness(), type.getInitialFullness(), type.getFullnessDecayRate(),
                               type.getMaxHappiness(), type.getInitialHappiness(), type.getHappinessDecayRate());
            // Add cases for other pet types here
            default:
                // This should ideally not happen if all enum values are handled
                throw new IllegalArgumentException("Unknown PetType: " + type);
        }
    }

    // Private constructor to prevent instantiation of the factory class itself
    private PetFactory() {}
} 