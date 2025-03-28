package com.group14.virtualpet.model;

import com.group14.virtualpet.state.State;
// TODO: Add imports for VitalStatistic, etc. when needed

/**
 * Base abstract class for all pets.
 * Holds the pet's name, vital statistics, and current state.
 * Delegates state-dependent actions to the currentState object.
 */
public abstract class Pet {

    private String name;
    // TODO: Add fields for VitalStatistics (health, happiness, etc.)
    private State currentState;

    // TODO: Add constructor to initialize pet with name, initial stats, and initial state

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setState(State newState) {
        this.currentState = newState;
        // Optional: Add logic here if state transitions trigger immediate effects
    }

    // --- State-Delegated Methods --- //
    // Example: Delegate feed action to the current state
    // public void feed() {
    //     currentState.feed(this);
    // }
    // TODO: Add similar delegation methods for play, giveGift, goToBed, takeToVet, exercise, update

    // TODO: Add abstract methods for pet-specific behaviors if any
} 