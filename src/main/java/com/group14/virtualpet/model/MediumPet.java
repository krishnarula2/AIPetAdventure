package com.group14.virtualpet.model;

/**
 * Concrete implementation of a Medium difficulty pet.
 */
public class MediumPet extends Pet {

    /**
     * Constructor for MediumPet.
     * @param name The name of the pet.
     * @param maxHealth Max health value.
     * @param initialHealth Initial health value.
     * @param maxSleep Max sleep value.
     * @param initialSleep Initial sleep value.
     * @param sleepDecayRate Decay rate for sleep.
     * @param maxFullness Max fullness value.
     * @param initialFullness Initial fullness value.
     * @param fullnessDecayRate Decay rate for fullness.
     * @param maxHappiness Max happiness value.
     * @param initialHappiness Initial happiness value.
     * @param happinessDecayRate Decay rate for happiness.
     */
    public MediumPet(String name,
               int maxHealth, double initialHealth,
               int maxSleep, double initialSleep, double sleepDecayRate,
               int maxFullness, double initialFullness, double fullnessDecayRate,
               int maxHappiness, double initialHappiness, double happinessDecayRate) {
        super(name,
              maxHealth, initialHealth,
              maxSleep, initialSleep, sleepDecayRate,
              maxFullness, initialFullness, fullnessDecayRate,
              maxHappiness, initialHappiness, happinessDecayRate);
        System.out.println("A Medium pet named " + name + " has arrived.");
    }

    // TODO: Override methods for pet-specific behaviors if needed
    // @Override
    // public String getSpeciesSound() {
    //     return "Meow!";
    // }
} 