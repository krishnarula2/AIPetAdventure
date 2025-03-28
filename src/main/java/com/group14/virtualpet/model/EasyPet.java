package com.group14.virtualpet.model;

/**
 * Concrete implementation of an Easy difficulty pet.
 */
public class EasyPet extends Pet {

    /**
     * Constructor for EasyPet.
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
    public EasyPet(String name,
               int maxHealth, double initialHealth,
               int maxSleep, double initialSleep, double sleepDecayRate,
               int maxFullness, double initialFullness, double fullnessDecayRate,
               int maxHappiness, double initialHappiness, double happinessDecayRate) {
        super(name,
              maxHealth, initialHealth,
              maxSleep, initialSleep, sleepDecayRate,
              maxFullness, initialFullness, fullnessDecayRate,
              maxHappiness, initialHappiness, happinessDecayRate);
        System.out.println("An Easy pet named " + name + " is ready!");
    }

    // TODO: Override methods for pet-specific behaviors if needed
    // For example, implement an abstract getSpeciesSound() method from Pet
    // @Override
    // public String getSpeciesSound() {
    //     return "Woof!";
    // }
} 