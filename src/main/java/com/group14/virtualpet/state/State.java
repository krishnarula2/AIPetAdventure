package com.group14.virtualpet.state;

import com.group14.virtualpet.model.Pet;

/**
 * Interface representing a pet's state (e.g., Normal, Sleeping).
 * Defines common state behaviors that concrete states must implement.
 */
public interface State {

    /**
     * Handles the action of feeding the pet.
     * @param pet The pet being fed.
     */
    void feed(Pet pet);

    /**
     * Handles the action of giving a gift to the pet.
     * @param pet The pet receiving the gift.
     */
    void giveGift(Pet pet);

    /**
     * Handles the command to put the pet to bed.
     * @param pet The pet going to bed.
     */
    void goToBed(Pet pet);

    /**
     * Handles the command to take the pet to the vet.
     * @param pet The pet being taken to the vet.
     */
    void takeToVet(Pet pet);

    /**
     * Handles the action of playing with the pet.
     * @param pet The pet being played with.
     */
    void play(Pet pet);

    /**
     * Handles the action of exercising the pet.
     * @param pet The pet exercising.
     */
    void exercise(Pet pet);

    /**
     * Handles time-based updates for the pet in this state (e.g., stat decay).
     * @param pet The pet to update.
     */
    void update(Pet pet);

    /**
     * Gets a user-friendly message describing the current state.
     * @return A string representing the state.
     */
    String getStatusMessage();
} 