package com.group14.virtualpet.state;

import com.group14.virtualpet.model.Pet;

/**
 * Represents the sleeping state of the pet.
 * Most interactions are disabled.
 * Sleep increases over time until max, then transitions back to NormalState.
 */
public class SleepingState implements State {

    // TODO: Consider making states Singletons or using a factory
    private static final double SLEEP_RECOVERY_RATE = 0.5; // Placeholder: How much sleep recovers per update tick

    private void printSleepingMessage(Pet pet) {
        System.out.println(pet.getName() + " is sleeping soundly. Zzz...");
    }

    @Override
    public void feed(Pet pet) {
        printSleepingMessage(pet);
        // Cannot feed while sleeping
    }

    @Override
    public void giveGift(Pet pet) {
        printSleepingMessage(pet);
        // Cannot give gift while sleeping
    }

    @Override
    public void goToBed(Pet pet) {
        printSleepingMessage(pet);
        // Already sleeping
    }

    @Override
    public void takeToVet(Pet pet) {
        printSleepingMessage(pet);
        // Cannot take to vet while sleeping
    }

    @Override
    public void play(Pet pet) {
        printSleepingMessage(pet);
        // Cannot play while sleeping
    }

    @Override
    public void exercise(Pet pet) {
        printSleepingMessage(pet);
        // Cannot exercise while sleeping
    }

    @Override
    public void update(Pet pet) {
        // Increase sleep value
        pet.getSleep().increase(SLEEP_RECOVERY_RATE);
        // System.out.println(pet.getName() + " sleep recovers slightly."); // Optional debug log

        // Check if pet should wake up
        // Using >= handles potential overshoot if recovery rate is large
        if (pet.getSleep().getCurrentValue() >= pet.getSleep().getMaxValue()) {
            System.out.println(pet.getName() + " wakes up refreshed!");
            pet.setState(new NormalState()); // TODO: Use factory/singleton instance
        }
        // Note: General stat decay (fullness, happiness) still happens in Pet.update()
    }

    @Override
    public String getStatusMessage() {
        return "Sleeping";
    }
} 