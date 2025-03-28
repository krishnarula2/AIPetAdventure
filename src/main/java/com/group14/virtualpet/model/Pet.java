package com.group14.virtualpet.model;

import com.group14.virtualpet.state.AngryState;
import com.group14.virtualpet.state.DeadState;
import com.group14.virtualpet.state.HungryState;
import com.group14.virtualpet.state.NormalState;
import com.group14.virtualpet.state.SleepingState;
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
     * Ensures transitions don't happen if already in a terminal state (Dead, Sleeping).
     */
    private void checkStateTransitions() {
        // Don't transition if already dead or sleeping (these states manage their own exit/non-exit)
        if (currentState instanceof DeadState || currentState instanceof SleepingState) {
            return;
        }

        // Highest precedence: Death
        if (health.isZero()) {
            if (!(currentState instanceof DeadState)) {
                 // TODO: Use factory/singleton instance
                setState(new DeadState());
            }
            return; // If dead, no other transitions matter
        }

        // Next precedence: Falling asleep from exhaustion
        if (sleep.isZero()) {
            if (!(currentState instanceof SleepingState)) {
                System.out.println(name + " collapses from exhaustion! Health suffers.");
                // Apply health penalty (Rule 3.1.6 b)
                health.decrease(5.0); // Placeholder penalty value
                // TODO: Use factory/singleton instance
                setState(new SleepingState());
            }
            return; // If falling asleep, no other transitions check needed this tick
        }

        // Lower precedence states: Angry and Hungry. They can potentially co-exist conceptually,
        // but the state machine handles one at a time. Angry might take precedence over Hungry?
        // Let's check Angry first.
        if (happiness.isZero()) {
             if (!(currentState instanceof AngryState)) {
                 System.out.println(name + " becomes angry due to zero happiness!");
                 // TODO: Use factory/singleton instance
                 setState(new AngryState());
                 // Note: AngryState handles its own recovery check
             }
             // Even if angry, we might *also* be hungry, let hungry check run if needed
             // Or decide if Angry blocks checking for Hungry state transition? Let's allow both for now.
        }

        // Check for Hunger
        if (fullness.isZero()) {
            if (!(currentState instanceof HungryState || currentState instanceof AngryState)) {
                 // Avoid transitioning to Hungry if already Angry (Angry takes precedence)
                 System.out.println(name + " becomes hungry due to zero fullness!");
                 // TODO: Use factory/singleton instance
                 setState(new HungryState());
                 // Note: HungryState handles penalties and recovery check
            }
        }

        // Implicit: If none of the above conditions are met, and the pet was in a state
        // like Angry or Hungry, those states' update/action methods handle returning to Normal.
        // No explicit transition back to NormalState is needed here, preventing potential loops.
    }

    // TODO: Add abstract methods for pet-specific behaviors if any (e.g., getSpeciesSpecificSound())
} 