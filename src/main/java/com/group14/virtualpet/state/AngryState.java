package com.group14.virtualpet.state;

import com.group14.virtualpet.model.Pet;

/**
 * Represents the angry state of the pet, entered when happiness reaches zero.
 * The pet refuses most commands until happiness recovers to at least 50%.
 * Only Give Gift and Play commands are accepted.
 */
public class AngryState implements State {

    // TODO: Consider making states Singletons or using a factory

    private void printRefusalMessage(Pet pet) {
        System.out.println(pet.getName() + " is angry and refuses to listen!");
    }

    @Override
    public void feed(Pet pet) {
        printRefusalMessage(pet);
        // Refuses command
    }

    @Override
    public void giveGift(Pet pet) {
        // Accepted command: Increases happiness
        System.out.println(pet.getName() + " grudgingly accepts the gift.");
        pet.getHappiness().increase(20); // Placeholder value, maybe more effective?
        checkRecovery(pet);
        // TODO: Integrate with specific GiftItem effects
    }

    @Override
    public void goToBed(Pet pet) {
        printRefusalMessage(pet);
        // Refuses command
    }

    @Override
    public void takeToVet(Pet pet) {
        printRefusalMessage(pet);
        // Refuses command
    }

    @Override
    public void play(Pet pet) {
        // Accepted command: Increases happiness
        System.out.println(pet.getName() + " plays reluctantly, but seems a little less angry.");
        pet.getHappiness().increase(15); // Placeholder value
        // Playing might affect other stats slightly even when angry
        pet.getFullness().decrease(2); // Placeholder
        pet.getSleep().decrease(2);    // Placeholder
        checkRecovery(pet);
        // TODO: Implement cooldown logic
    }

    @Override
    public void exercise(Pet pet) {
        printRefusalMessage(pet);
        // Refuses command
    }

    @Override
    public void update(Pet pet) {
        // Check if happiness has recovered sufficiently to exit the angry state.
        // This check is also done after accepted commands, but the update loop ensures
        // recovery happens even if the player does nothing (assuming base decay doesn't keep it at 0).
        checkRecovery(pet);

        // Note: General stat decay (sleep, fullness, happiness) still happens in Pet.update()
        // Note: Health/Sleep checks for Dead/Sleeping states happen in Pet.checkStateTransitions()
    }

    /**
     * Checks if the pet's happiness is high enough to exit the Angry state.
     * @param pet The pet to check.
     */
    private void checkRecovery(Pet pet) {
        double recoveryThreshold = pet.getHappiness().getMaxValue() * 0.50;
        if (pet.getHappiness().getCurrentValue() >= recoveryThreshold) {
            System.out.println(pet.getName() + " has calmed down and is no longer angry!");
            pet.setState(new NormalState()); // TODO: Use factory/singleton instance
        }
    }

    @Override
    public String getStatusMessage() {
        return "Angry";
    }
} 