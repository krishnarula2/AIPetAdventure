package com.group14.virtualpet.state;

import com.group14.virtualpet.model.Pet;

/**
 * Represents the hungry state of the pet, entered when fullness reaches zero.
 * Happiness decay is increased, and health decreases over time.
 * All commands remain available.
 * Transitions back to NormalState when fullness is above zero.
 */
public class HungryState implements State {

    // TODO: Consider making states Singletons or using a factory
    private static final double HUNGER_HEALTH_DAMAGE = 0.2; // Placeholder: How much health decreases per update tick due to hunger
    private static final double HUNGER_HAPPINESS_MULTIPLIER = 1.5; // Placeholder: Factor to multiply base happiness decay by

    @Override
    public void feed(Pet pet) {
        // Feeding is crucial in this state
        System.out.println(pet.getName() + " gobbles the food hungrily!");
        pet.getFullness().increase(25); // Placeholder value, maybe more effective when hungry?
        // Feeding should immediately check if the pet is no longer hungry
        if (!pet.getFullness().isZero()) {
             pet.setState(new NormalState()); // Exit hungry state
        }
        // TODO: Integrate with specific FoodItem effects
    }

    @Override
    public void giveGift(Pet pet) {
        // Still possible, but maybe less effective?
        System.out.println(pet.getName() + " accepts the gift, but is still very hungry.");
        pet.getHappiness().increase(10); // Placeholder value
        // TODO: Integrate with specific GiftItem effects
    }

    @Override
    public void goToBed(Pet pet) {
        // Can still go to bed while hungry
        System.out.println(pet.getName() + " goes to sleep, dreaming of food.");
        pet.setState(new SleepingState()); // TODO: Use factory/singleton instance
    }

    @Override
    public void takeToVet(Pet pet) {
        // Still possible
        System.out.println(pet.getName() + " visits the vet. The vet advises feeding the pet!");
        pet.getHealth().increase(30); // Placeholder value, vet might be less effective if underlying cause isn't treated?
        // TODO: Implement cooldown logic
    }

    @Override
    public void play(Pet pet) {
        // Can still play, but might not be as happy
        System.out.println(pet.getName() + " plays half-heartedly, stomach rumbling.");
        pet.getHappiness().increase(5); // Placeholder value, less effective?
        pet.getFullness().decrease(5); // Playing makes you hungrier!
        pet.getSleep().decrease(5);
        // TODO: Implement cooldown logic
    }

    @Override
    public void exercise(Pet pet) {
        // Can still exercise, but makes pet hungrier
        System.out.println(pet.getName() + " exercises weakly, needing energy.");
        pet.getHealth().increase(2);      // Less effective?
        pet.getSleep().decrease(10);
        pet.getFullness().decrease(15); // Exercise makes you hungrier!
    }

    @Override
    public void update(Pet pet) {
        // Apply hunger penalties
        System.out.println(pet.getName() + "'s stomach rumbles loudly! Health and happiness dropping faster!");
        pet.getHealth().decrease(HUNGER_HEALTH_DAMAGE);

        // Apply extra happiness decay
        double baseHappinessDecay = pet.getHappiness().getDecayRate();
        // Apply multiplier, ensuring we decrease by at least the base amount even if multiplier is < 1 somehow
        double extraDecay = Math.max(0, baseHappinessDecay * (HUNGER_HAPPINESS_MULTIPLIER - 1.0));
        pet.getHappiness().decrease(extraDecay);

        // Check if no longer hungry (e.g., if fed during this tick)
        // Although feed() handles immediate exit, this is a safety check
        if (!pet.getFullness().isZero()) {
            System.out.println(pet.getName() + " is no longer hungry.");
            pet.setState(new NormalState()); // TODO: Use factory/singleton instance
        }
        // Note: Base decay (sleep, fullness, base happiness) still happens in Pet.update()
        // Note: Health check for DeadState happens in Pet.checkStateTransitions()
    }

    @Override
    public String getStatusMessage() {
        return "Hungry";
    }
} 