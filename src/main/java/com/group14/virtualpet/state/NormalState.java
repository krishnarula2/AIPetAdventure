package com.group14.virtualpet.state;

import com.group14.virtualpet.model.Pet;

/**
 * Represents the normal, default state of the pet.
 * All commands are typically available in this state.
 */
public class NormalState implements State {

    // TODO: Consider making states Singletons or using a factory

    @Override
    public void feed(Pet pet) {
        // Example: Increase fullness, maybe slightly decrease happiness if already full?
        System.out.println(pet.getName() + " eats the food.");
        pet.getFullness().increase(20); // Placeholder value
        // TODO: Integrate with specific FoodItem effects
    }

    @Override
    public void giveGift(Pet pet) {
        // Example: Increase happiness
        System.out.println(pet.getName() + " loves the gift!");
        pet.getHappiness().increase(15); // Placeholder value
        // TODO: Integrate with specific GiftItem effects
    }

    @Override
    public void goToBed(Pet pet) {
        // Transition to SleepingState
        System.out.println(pet.getName() + " goes to sleep.");
        pet.setState(new SleepingState()); // TODO: Use factory/singleton instance
    }

    @Override
    public void takeToVet(Pet pet) {
        // Example: Increase health significantly
        System.out.println(pet.getName() + " visits the vet and feels much better!");
        pet.getHealth().increase(50); // Placeholder value
        // TODO: Implement cooldown logic (likely outside the state class)
    }

    @Override
    public void play(Pet pet) {
        // Example: Increase happiness, maybe decrease fullness/sleep slightly
        System.out.println(pet.getName() + " enjoys playing!");
        pet.getHappiness().increase(10); // Placeholder value
        pet.getFullness().decrease(5); // Placeholder value
        pet.getSleep().decrease(5);    // Placeholder value
        // TODO: Implement cooldown logic (likely outside the state class)
    }

    @Override
    public void exercise(Pet pet) {
        // Example: Increase health, decrease sleepiness and hunger
        System.out.println(pet.getName() + " gets some exercise!");
        pet.getHealth().increase(5);      // Placeholder value
        pet.getSleep().decrease(10);   // Placeholder value
        pet.getFullness().decrease(10); // Placeholder value
    }

    @Override
    public void update(Pet pet) {
        // In Normal state, primary updates (decay) happen in Pet.update().
        // State transitions are checked in Pet.checkStateTransitions().
        // This method could be used for subtle Normal-state-specific behaviors if needed.
        // System.out.println(pet.getName() + " is feeling normal.");
    }

    @Override
    public String getStatusMessage() {
        return "Normal";
    }
} 