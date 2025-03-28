package com.group14.virtualpet.model;

/**
 * Represents the different types of pets available in the game.
 * Each type can have unique characteristics (stats, decay rates, etc.).
 */
public enum PetType {
    // Define characteristics for each difficulty level
    EASY("Easy", // Was Dog
         100, 100.0, // Health (Max, Initial)
         100, 90.0, 0.4, // Sleep (Slower decay)
         100, 80.0, 0.5, // Fullness (Slower decay)
         100, 80.0, 0.3  // Happiness (Slower decay)
    ),
    MEDIUM("Medium", // Was Cat
          100, 90.0,  // Health
          100, 80.0, 0.6, // Sleep
          100, 75.0, 0.7, // Fullness
          100, 70.0, 0.6  // Happiness
    ),
    HARD("Hard", // Was Robot - Adjusted for difficulty
         100, 85.0,  // Health (Lower start)
         100, 70.0, 0.9,  // Sleep (Faster decay)
         100, 65.0, 1.0,  // Fullness (Faster decay)
         100, 60.0, 0.8   // Happiness (Faster decay)
    );
    // TODO: Add more types if desired

    private final String displayName;
    // Fields for characteristics
    private final int maxHealth;
    private final double initialHealth;
    private final int maxSleep;
    private final double initialSleep;
    private final double sleepDecayRate;
    private final int maxFullness;
    private final double initialFullness;
    private final double fullnessDecayRate;
    private final int maxHappiness;
    private final double initialHappiness;
    private final double happinessDecayRate;

    // Update constructor to accept all characteristics
    PetType(String displayName,
            int maxHealth, double initialHealth,
            int maxSleep, double initialSleep, double sleepDecayRate,
            int maxFullness, double initialFullness, double fullnessDecayRate,
            int maxHappiness, double initialHappiness, double happinessDecayRate) {
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.initialHealth = initialHealth;
        this.maxSleep = maxSleep;
        this.initialSleep = initialSleep;
        this.sleepDecayRate = sleepDecayRate;
        this.maxFullness = maxFullness;
        this.initialFullness = initialFullness;
        this.fullnessDecayRate = fullnessDecayRate;
        this.maxHappiness = maxHappiness;
        this.initialHappiness = initialHappiness;
        this.happinessDecayRate = happinessDecayRate;
    }

    // Getters for characteristics
    public String getDisplayName() { return displayName; }
    public int getMaxHealth() { return maxHealth; }
    public double getInitialHealth() { return initialHealth; }
    public int getMaxSleep() { return maxSleep; }
    public double getInitialSleep() { return initialSleep; }
    public double getSleepDecayRate() { return sleepDecayRate; }
    public int getMaxFullness() { return maxFullness; }
    public double getInitialFullness() { return initialFullness; }
    public double getFullnessDecayRate() { return fullnessDecayRate; }
    public int getMaxHappiness() { return maxHappiness; }
    public double getInitialHappiness() { return initialHappiness; }
    public double getHappinessDecayRate() { return happinessDecayRate; }

    // Example: Placeholder for characteristics
    // public int getDefaultMaxHealth() { return 100; }
    // public double getDefaultHappinessDecay() { return 0.5; }
} 