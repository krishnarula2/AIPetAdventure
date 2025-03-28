package com.group14.virtualpet.model;

import com.group14.virtualpet.state.NormalState;
import com.group14.virtualpet.state.State;
// TODO: Add imports for VitalStatistic, etc. when needed

/**
 * Base abstract class for all pets.
 * Holds the pet's name, vital statistics, and current state.
 * Delegates state-dependent actions to the currentState object.
 */
public abstract class Pet {

    private String name;
    private VitalStatistic health;
    private VitalStatistic sleep;
    private VitalStatistic fullness;
    private VitalStatistic happiness;
    private State currentState;

    // TODO: Consider PetType specific defaults or passing a config object
    /**
     * Constructor for Pet.
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
    public Pet(String name,
               int maxHealth, double initialHealth,
               int maxSleep, double initialSleep, double sleepDecayRate,
               int maxFullness, double initialFullness, double fullnessDecayRate,
               int maxHappiness, double initialHappiness, double happinessDecayRate) {
        this.name = name;
        // Health typically doesn't decay naturally, so decay rate is 0
        this.health = new VitalStatistic(maxHealth, initialHealth, 0);
        this.sleep = new VitalStatistic(maxSleep, initialSleep, sleepDecayRate);
        this.fullness = new VitalStatistic(maxFullness, initialFullness, fullnessDecayRate);
        this.happiness = new VitalStatistic(maxHappiness, initialHappiness, happinessDecayRate);

        // TODO: Refine initial state setting, maybe use a Factory or pass state
        this.currentState = new NormalState(); // Default to NormalState initially
    }

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
        System.out.println(name + " changed state to: " + newState.getClass().getSimpleName()); // Basic logging
    }

    // --- Vital Statistic Getters ---

    public VitalStatistic getHealth() {
        return health;
    }

    public VitalStatistic getSleep() {
        return sleep;
    }

    public VitalStatistic getFullness() {
        return fullness;
    }

    public VitalStatistic getHappiness() {
        return happiness;
    }

    // --- State-Delegated Methods --- //

    /** Delegates the feed action to the current state. */
    public void feed() {
        currentState.feed(this);
    }

    /** Delegates the play action to the current state. */
    public void play() {
        currentState.play(this);
    }

    /** Delegates the giveGift action to the current state. */
    public void giveGift() {
        currentState.giveGift(this);
    }

    /** Delegates the goToBed action to the current state. */
    public void goToBed() {
        currentState.goToBed(this);
    }

    /** Delegates the takeToVet action to the current state. */
    public void takeToVet() {
        currentState.takeToVet(this);
    }

    /** Delegates the exercise action to the current state. */
    public void exercise() {
        currentState.exercise(this);
    }

    /**
     * Central update method called periodically by the game loop.
     * Delegates decay to stats and state-specific updates to the current state.
     * Also checks for state transitions based on stat values.
     */
    public void update() {
        // Natural decay of stats
        sleep.decay();
        fullness.decay();
        happiness.decay();
        // Health doesn't decay naturally but might be affected by states

        // Delegate state-specific updates/checks
        currentState.update(this);

        // Check for state transitions AFTER state update logic
        checkStateTransitions();
    }

    /**
     * Checks vital statistics and triggers state changes if necessary.
     * Order matters here - e.g., Dead state takes precedence.
     */
    private void checkStateTransitions() {
        // TODO: Refine this logic, especially handling multiple low stats or state interactions
        if (health.isZero()) {
            // setState(new DeadState()); // Need DeadState instance
        } else if (sleep.isZero()) {
            // Apply health penalty
            // health.decrease(SOME_PENALTY_VALUE);
            // setState(new SleepingState()); // Need SleepingState instance
        } else if (fullness.isZero()) {
            // Don't necessarily change state immediately, HungryState logic handles effects
            // But ensure if not already hungry, maybe transition?
            // This logic might belong IN the NormalState.update() or here. Requires thought.
        } else if (happiness.isZero()) {
            // setState(new AngryState()); // Need AngryState instance
        }
        // If none of the above, and not already normal, potentially return to normal?
        // else if (!(currentState instanceof NormalState)) {
        //     // Add conditions for returning to normal (e.g., stats above thresholds)
        //     // setState(new NormalState());
        // }
    }

    // TODO: Add abstract methods for pet-specific behaviors if any (e.g., getSpeciesSpecificSound())
} 