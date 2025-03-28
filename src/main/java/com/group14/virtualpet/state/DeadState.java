package com.group14.virtualpet.state;

import com.group14.virtualpet.model.Pet;

/**
 * Represents the dead state of the pet, entered when health reaches zero.
 * No commands are possible, and no stats change.
 * The game is effectively over for this pet.
 */
public class DeadState implements State {

    // TODO: Consider making states Singletons or using a factory

    private void printDeadMessage(Pet pet) {
        System.out.println(pet.getName() + " is dead and cannot respond. Game over.");
    }

    @Override
    public void feed(Pet pet) {
        printDeadMessage(pet);
    }

    @Override
    public void giveGift(Pet pet) {
        printDeadMessage(pet);
    }

    @Override
    public void goToBed(Pet pet) {
        printDeadMessage(pet);
    }

    @Override
    public void takeToVet(Pet pet) {
        printDeadMessage(pet);
    }

    @Override
    public void play(Pet pet) {
        printDeadMessage(pet);
    }

    @Override
    public void exercise(Pet pet) {
        printDeadMessage(pet);
    }

    @Override
    public void update(Pet pet) {
        // Nothing happens when dead. Stats no longer decay.
    }

    @Override
    public String getStatusMessage() {
        return "Dead";
    }
} 