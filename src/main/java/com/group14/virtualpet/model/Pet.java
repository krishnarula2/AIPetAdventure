package com.group14.virtualpet.model;

import java.io.Serializable; // Import Serializable

/**
 * Represents the virtual pet in the game.
 * Stores its vital statistics, name, and type.
 * Requirements: 3.1.4, 3.1.6
 */
public class Pet implements Serializable { // Implement Serializable
    private static final long serialVersionUID = 1L; // Add serialVersionUID

    // --- Constants ---
    // Default Max Values (Requirement 3.1.6)
    public static final int DEFAULT_MAX_HEALTH = 100;
    public static final int DEFAULT_MAX_SLEEP = 100;
    public static final int DEFAULT_MAX_FULLNESS = 100;
    public static final int DEFAULT_MAX_HAPPINESS = 100;

    // Default Decline Rates (Per Tick) (Requirement 3.1.6)
    private static final int DEFAULT_SLEEP_DECLINE = 1;
    private static final int DEFAULT_FULLNESS_DECLINE = 2;
    private static final int DEFAULT_HAPPINESS_DECLINE = 1;

    // State-Specific Effects (Requirement 3.1.6)
    private static final int SLEEPING_HEALTH_PENALTY = 10; // Penalty when forced to sleep
    private static final int SLEEPING_SLEEP_RECOVERY_RATE = 5; // How fast sleep recovers when sleeping
    private static final int HUNGRY_HAPPINESS_DECLINE_MULTIPLIER = 2; // Rate multiplier when hungry
    private static final int HUNGRY_HEALTH_DECLINE = 2; // Health lost per tick when hungry
    private static final int ANGRY_MIN_HAPPINESS_EXIT = DEFAULT_MAX_HAPPINESS / 2; // Happiness needed to exit ANGRY state (Req 3.1.6d)

    // Cooldown Durations (Requirement 3.1.7d, 3.1.7e)
    private static final long VET_COOLDOWN_MS = 60 * 1000 * 5; // 5 minutes (example)
    private static final long PLAY_COOLDOWN_MS = 60 * 1000 * 1; // 1 minute (example)

    // TODO: Make max values, decline rates, and effects potentially dependent on petType

    // --- Fields ---
    private String petType; // e.g., "friendly_robot", "balanced_robot", "challenging_robot"
    private String name;
    private int health;
    private int sleep;
    private int fullness;
    private int happiness;
    private PetState currentState;
    private int maxHealth; // Specific max values for this instance
    private int maxSleep;
    private int maxFullness;
    private int maxHappiness;

    // Cooldown Tracking
    private long lastVetTime = 0; // Timestamp of last vet visit
    private long lastPlayTime = 0; // Timestamp of last play session

    /**
     * Constructor for creating a new pet.
     * @param name The name given to the pet.
     * @param petType The type of the pet.
     */
    public Pet(String name, String petType) {
        this.name = name;
        this.petType = petType;
        setInitialMaxValuesForPetType(); // Set max values based on type
        
        // Starting values at 75% of max
        this.health = (int) (maxHealth * 0.75);
        this.sleep = (int) (maxSleep * 0.75);
        this.fullness = (int) (maxFullness * 0.75);
        this.happiness = (int) (maxHappiness * 0.75);

        this.currentState = PetState.NORMAL;

        // TODO: Adjust starting stats and max values based on petType characteristics (Req 3.1.4) - Partially done
        // TODO: Adjust decline rates based on petType (requires adding fields for rates)
    }

    /**
     * Set the maximum values for each statistic based on the pet type
     */
    private void setInitialMaxValuesForPetType() {
        switch (petType) {
            case "friendly_robot": // Example: Higher fullness decline, average everything else
                this.maxHealth = DEFAULT_MAX_HEALTH;
                this.maxSleep = DEFAULT_MAX_SLEEP;
                this.maxFullness = DEFAULT_MAX_FULLNESS + 20; // More food capacity
                this.maxHappiness = DEFAULT_MAX_HAPPINESS;
                break;
            case "balanced_robot": // Example: Lower happiness decline, higher sleep need
                this.maxHealth = DEFAULT_MAX_HEALTH;
                this.maxSleep = DEFAULT_MAX_SLEEP + 20; // More sleep needed
                this.maxFullness = DEFAULT_MAX_FULLNESS;
                this.maxHappiness = DEFAULT_MAX_HAPPINESS - 10; // Harder to keep happy
                break;
            case "challenging_robot": // Example: Lower health but increased happiness
                this.maxHealth = DEFAULT_MAX_HEALTH - 10; // Less sturdy
                this.maxSleep = DEFAULT_MAX_SLEEP;
                this.maxFullness = DEFAULT_MAX_FULLNESS;
                this.maxHappiness = DEFAULT_MAX_HAPPINESS + 20; // Higher potential happiness
                break;
            // TODO: Add more pet types
            default:
                // Default values as above
                break;
        }
    }

    // --- Getters ---
    public String getName() { return name; }
    public String getPetType() { return petType; }
    public int getHealth() { return health; }
    public int getSleep() { return sleep; }
    public int getFullness() { return fullness; }
    public int getHappiness() { return happiness; }
    public PetState getCurrentState() { return currentState; }
    public int getMaxHealth() { return maxHealth; }
    public int getMaxSleep() { return maxSleep; }
    public int getMaxFullness() { return maxFullness; }
    public int getMaxHappiness() { return maxHappiness; }

    // --- Simulation Tick ---

    /**
     * Simulates the passage of one time unit (tick).
     * Applies stat declines and updates the pet's state.
     * Requirement 3.1.6
     */
    public void liveOneTick() {
        if (currentState == PetState.DEAD) {
            return; // No changes if dead
        }

        // Get decline rates (TODO: Make these instance fields set by constructor based on petType)
        int currentSleepDecline = DEFAULT_SLEEP_DECLINE;
        int currentFullnessDecline = DEFAULT_FULLNESS_DECLINE;
        int currentHappinessDecline = DEFAULT_HAPPINESS_DECLINE;

        // Example: Modify decline rates based on type
        if ("friendly_robot".equals(petType)) {
            currentFullnessDecline = 3; // friendly_robots get hungry faster
        } else if ("balanced_robot".equals(petType)) {
            currentHappinessDecline = 0; // balanced_robots stay happy (or decline slower)
        }

        // Apply state-based effects and declines
        if (currentState == PetState.SLEEPING) {
            increaseSleep(SLEEPING_SLEEP_RECOVERY_RATE); // Recover sleep
            decreaseFullness(currentFullnessDecline);   // Use current decline rate
            decreaseHappiness(currentHappinessDecline);
        } else {
            // Not sleeping: Normal declines apply
            decreaseSleep(currentSleepDecline);
            decreaseFullness(currentFullnessDecline);

            int happinessDecline = currentHappinessDecline;
            if (currentState == PetState.HUNGRY) {
                happinessDecline *= HUNGRY_HAPPINESS_DECLINE_MULTIPLIER; // Faster happiness decline
                decreaseHealth(HUNGRY_HEALTH_DECLINE); // Health decreases when hungry
            }
            decreaseHappiness(happinessDecline);
        }

        // Update the state based on the new stat values
        updateState();

        // Temporary logging
        // System.out.println("Tick: " + this.toString());
    }

    // --- State Management ---

    /**
     * Updates the pet's current state based on its vital statistics.
     * Handles state transitions and entry/exit effects.
     * Requirement 3.1.6
     */
    private void updateState() {
        PetState previousState = this.currentState;

        // Determine the new state based on precedence: DEAD > SLEEPING > ANGRY > HUNGRY > NORMAL
        if (health <= 0) {
            currentState = PetState.DEAD;
        } else if (currentState == PetState.SLEEPING) {
            // Check for waking up
            if (sleep >= maxSleep) {
                wakeUp(); // Changes state to NORMAL (or potentially HUNGRY/ANGRY)
            }
            // While sleeping, other conditions (like hunger/anger) don't change the primary SLEEPING state
        } else if (sleep <= 0) {
             // Note: Pet falls asleep *in the next tick* if not commanded to sleep.
             // The spec implies an immediate health penalty and state change if sleep hits 0.
             forceSleep(); // Applies penalty and sets state
        } else if (happiness <= 0) {
            currentState = PetState.ANGRY;
        } else if (fullness <= 0) {
            currentState = PetState.HUNGRY;
        } else {
            // Conditions met to potentially return to NORMAL
            if (currentState == PetState.ANGRY && happiness >= ANGRY_MIN_HAPPINESS_EXIT) {
                currentState = PetState.NORMAL;
            } else if (currentState == PetState.HUNGRY && fullness > 0) {
                currentState = PetState.NORMAL;
            } else if (currentState != PetState.SLEEPING) { // Ensure we don't override sleep
                 currentState = PetState.NORMAL;
            }
        }

        // Log state changes
        if (previousState != currentState) {
            System.out.println("State changed from " + previousState + " to " + currentState);
            // TODO: Trigger UI updates or animations based on state change
        }
    }

    /** Forces the pet to sleep, applying a health penalty. Req 3.1.6b */
    public void forceSleep() {
        if (currentState != PetState.SLEEPING && currentState != PetState.DEAD) {
            System.out.println(name + " passed out from exhaustion!");
            decreaseHealth(SLEEPING_HEALTH_PENALTY);
            this.currentState = PetState.SLEEPING;
            // Re-check state immediately in case health penalty killed the pet
            updateState();
        }
    }

     /** Wakes the pet up from sleep. */
    public void wakeUp() {
        if (currentState == PetState.SLEEPING) {
             System.out.println(name + " woke up!");
             // Reset state - updateState will determine the correct new state (Normal, Hungry, Angry)
             this.currentState = PetState.NORMAL; // Tentative state before check
             updateState(); // Re-evaluate state based on other stats
        }
    }

    /** Puts the pet to sleep via command. Req 3.1.7a */
    public void goToBed() {
         if (currentState != PetState.SLEEPING && currentState != PetState.DEAD) {
            System.out.println(name + " is going to bed.");
            this.currentState = PetState.SLEEPING;
            updateState(); // Update state immediately
        }
    }

    /**
     * Revives the pet, typically used by parental controls.
     * Resets all stats to maximum and sets state to NORMAL.
     * Requirement 3.1.11.3
     */
    public void revive() {
        System.out.println("Reviving " + this.name + "!");
        this.health = this.maxHealth;
        this.sleep = this.maxSleep;
        this.fullness = this.maxFullness;
        this.happiness = this.maxHappiness;
        this.currentState = PetState.NORMAL;
        // Cooldowns are NOT reset by revive, intentionally.
        // If we want to reset cooldowns, add that logic here.
    }

    // --- Stat Modifiers ---
    // These methods handle increasing/decreasing stats and clamping values

    public void increaseHealth(int amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
        // updateState(); // Update state immediately after significant changes
    }

    public void decreaseHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
        if (this.health == 0) updateState(); // Check for death immediately
    }

    public void increaseSleep(int amount) {
        this.sleep = Math.min(this.maxSleep, this.sleep + amount);
        if (currentState == PetState.SLEEPING && this.sleep >= this.maxSleep) {
             updateState(); // Check if should wake up
        }
    }

    public void decreaseSleep(int amount) {
        this.sleep = Math.max(0, this.sleep - amount);
        if (this.sleep == 0 && currentState != PetState.SLEEPING) {
            updateState(); // Check if should pass out
        }
    }

    public void increaseFullness(int amount) {
        boolean wasHungry = (this.fullness <= 0);
        this.fullness = Math.min(this.maxFullness, this.fullness + amount);
        if (wasHungry && this.fullness > 0) {
            updateState(); // Check if no longer hungry
        }
    }

    public void decreaseFullness(int amount) {
        this.fullness = Math.max(0, this.fullness - amount);
        if (this.fullness == 0) updateState(); // Check if became hungry
    }

    public void increaseHappiness(int amount) {
        boolean wasAngry = (this.happiness <= 0);
        this.happiness = Math.min(this.maxHappiness, this.happiness + amount);
        // Req 3.1.6d: Exit angry state when happiness is >= max/2
        if (wasAngry && this.happiness >= ANGRY_MIN_HAPPINESS_EXIT) {
             updateState(); // Check if no longer angry
        }
    }

    public void decreaseHappiness(int amount) {
        this.happiness = Math.max(0, this.happiness - amount);
        if (this.happiness == 0) updateState(); // Check if became angry
    }

    // --- Command Methods (Requirement 3.1.7) ---

    /**
     * Checks if the 'Play' command is available (off cooldown).
     * @return true if play is available, false otherwise.
     */
    public boolean isPlayAvailable() {
        return System.currentTimeMillis() >= lastPlayTime + PLAY_COOLDOWN_MS;
    }

    /**
     * Checks if the 'Take to Vet' command is available (off cooldown).
     * @return true if vet is available, false otherwise.
     */
    public boolean isVetAvailable() {
        return System.currentTimeMillis() >= lastVetTime + VET_COOLDOWN_MS;
    }

    /**
     * Player plays with the pet. Increases Happiness.
     * @return true if the action was performed, false if it was on cooldown.
     */
    public boolean play() {
        if (!isPlayAvailable()) {
            System.out.println(name + " doesn't want to play right now (cooldown)...");
            return false;
        }
        System.out.println("Playing with " + name + "...");
        increaseHappiness(15); // Example amount
        lastPlayTime = System.currentTimeMillis(); // Update last play time
        return true;
    }

    /**
     * Player takes the pet to the vet. Increases Health.
     * @return true if the action was performed, false if it was on cooldown.
     */
    public boolean takeToVet() {
        if (!isVetAvailable()) {
            System.out.println(name + " doesn't need the vet right now (cooldown)...");
            return false;
        }
        System.out.println("Taking " + name + " to the vet...");
        increaseHealth(30); // Example amount
        lastVetTime = System.currentTimeMillis(); // Update last vet time
        // TODO: Add score decrease? (Req 3.1.9)
        return true;
    }

    /** Player exercises the pet. Affects Health, Sleep, Fullness. */
    public void exercise() {
        System.out.println("Exercising with " + name + "...");
        increaseHealth(5);    // Example amount
        decreaseSleep(10);    // Gets tired
        decreaseFullness(15); // Gets hungry
    }

    /** Player feeds the pet a specific food item. Increases Fullness. */
    public void feed(FoodItem food) {
        if (food == null) return;
        System.out.println("Feeding " + name + " with " + food.getName() + "...");
        increaseFullness(food.getFullnessValue());
        // TODO: Add score increase (Req 3.1.9)
    }

    /** Player gives the pet a specific gift item. Increases Happiness. */
    public void receiveGift(GiftItem gift) {
        if (gift == null) return;
        System.out.println(name + " receives a " + gift.getName() + "!");
        increaseHappiness(gift.getHappinessValue());
        // TODO: Add score increase (Req 3.1.9)
    }

    // Note: goToBed() was added previously
    // TODO: Implement feed(FoodItem item) - Requires Inventory (Req 3.1.7b, 3.1.8)
    // TODO: Implement giveGift(GiftItem item) - Requires Inventory (Req 3.1.7c, 3.1.8)

    @Override
    public String toString() {
        return String.format("Pet{name='%s', type='%s', state=%s, H=%d/%d, S=%d/%d, F=%d/%d, Hap=%d/%d}",
                name, petType, currentState,
                health, maxHealth, sleep, maxSleep, fullness, maxFullness, happiness, maxHappiness);
    }

} 