/**
 * Class representing a virtual pet in the game.
 * The pet has properties such as its name, health, and hunger level.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet.model;

import java.io.Serializable;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_MAX_HEALTH = 100;
    public static final int DEFAULT_MAX_SLEEP = 100;
    public static final int DEFAULT_MAX_FULLNESS = 100;
    public static final int DEFAULT_MAX_HAPPINESS = 100;

    private static final int DEFAULT_SLEEP_DECLINE = 1;
    private static final int DEFAULT_FULLNESS_DECLINE = 2;
    private static final int DEFAULT_HAPPINESS_DECLINE = 1;

    private static final int SLEEPING_HEALTH_PENALTY = 10;
    private static final int SLEEPING_SLEEP_RECOVERY_RATE = 5;
    private static final int HUNGRY_HAPPINESS_DECLINE_MULTIPLIER = 2;
    private static final int HUNGRY_HEALTH_DECLINE = 2;
    private static final int ANGRY_MIN_HAPPINESS_EXIT = DEFAULT_MAX_HAPPINESS / 2;

    private String petType;
    private String name;
    private int health;
    private int sleep;
    private int fullness;
    private int happiness;
    private PetState currentState;
    private int maxHealth;
    private int maxSleep;
    private int maxFullness;
    private int maxHappiness;

    private long lastVetTime = 0;
    private long lastPlayTime = 0;

    public Pet(String name, String petType) {
        this.name = name;
        this.petType = petType;
        setInitialMaxValuesForPetType();
        
        this.health = (int) (maxHealth * 0.75);
        this.sleep = (int) (maxSleep * 0.75);
        this.fullness = (int) (maxFullness * 0.75);
        this.happiness = (int) (maxHappiness * 0.75);

        this.currentState = PetState.NORMAL;
    }

    private void setInitialMaxValuesForPetType() {
        switch (petType) {
            case "friendly_robot":
                this.maxHealth = DEFAULT_MAX_HEALTH;
                this.maxSleep = DEFAULT_MAX_SLEEP;
                this.maxFullness = DEFAULT_MAX_FULLNESS + 20;
                this.maxHappiness = DEFAULT_MAX_HAPPINESS;
                break;
            case "balanced_robot":
                this.maxHealth = DEFAULT_MAX_HEALTH;
                this.maxSleep = DEFAULT_MAX_SLEEP + 20;
                this.maxFullness = DEFAULT_MAX_FULLNESS;
                this.maxHappiness = DEFAULT_MAX_HAPPINESS - 10;
                break;
            case "challenging_robot":
                this.maxHealth = DEFAULT_MAX_HEALTH - 10;
                this.maxSleep = DEFAULT_MAX_SLEEP;
                this.maxFullness = DEFAULT_MAX_FULLNESS;
                this.maxHappiness = DEFAULT_MAX_HAPPINESS + 20;
                break;
            default:
                break;
        }
    }

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

    public void liveOneTick() {
        if (currentState == PetState.DEAD) {
            return;
        }

        int currentSleepDecline = DEFAULT_SLEEP_DECLINE;
        int currentFullnessDecline = DEFAULT_FULLNESS_DECLINE;
        int currentHappinessDecline = DEFAULT_HAPPINESS_DECLINE;

        if ("friendly_robot".equals(petType)) {
            currentFullnessDecline = 3;
        } else if ("balanced_robot".equals(petType)) {
            currentHappinessDecline = 0;
        }

        if (currentState == PetState.SLEEPING) {
            increaseSleep(SLEEPING_SLEEP_RECOVERY_RATE);
            decreaseFullness(currentFullnessDecline);
            decreaseHappiness(currentHappinessDecline);
        } else {
            decreaseSleep(currentSleepDecline);
            decreaseFullness(currentFullnessDecline);

            int happinessDecline = currentHappinessDecline;
            if (currentState == PetState.HUNGRY) {
                happinessDecline *= HUNGRY_HAPPINESS_DECLINE_MULTIPLIER;
                decreaseHealth(HUNGRY_HEALTH_DECLINE);
            }
            decreaseHappiness(happinessDecline);
        }

        updateState();
    }

    private void updateState() {
        PetState previousState = this.currentState;
        if (health <= 0) {
            currentState = PetState.DEAD;
        } else if (currentState == PetState.SLEEPING) {
            if (sleep >= maxSleep) {
                wakeUp();
            }
        } else if (sleep <= 0) {
             forceSleep();
        } else if (fullness <= 0) {
            currentState = PetState.HUNGRY;
        } else if (happiness <= 0) {
            currentState = PetState.ANGRY;        
        } else {
            if (currentState == PetState.ANGRY && happiness >= ANGRY_MIN_HAPPINESS_EXIT) {
                currentState = PetState.NORMAL;
            } else if (currentState == PetState.HUNGRY && fullness > 0) {
                currentState = PetState.NORMAL;
            } else if (currentState != PetState.SLEEPING) {
                 currentState = PetState.NORMAL;
            }
        }

        if (previousState != currentState) {
            System.out.println("State changed from " + previousState + " to " + currentState);
        }
    }

    public void forceSleep() {
        if (currentState != PetState.SLEEPING && currentState != PetState.DEAD) {
            System.out.println(name + " passed out from exhaustion!");
            decreaseHealth(SLEEPING_HEALTH_PENALTY);
            this.currentState = PetState.SLEEPING;
            updateState();
        }
    }

    public void wakeUp() {
        if (currentState == PetState.SLEEPING) {
             System.out.println(name + " woke up!");
             this.currentState = PetState.NORMAL;
             updateState();
        }
    }

    public void goToBed() {
         if (currentState != PetState.SLEEPING && currentState != PetState.DEAD) {
            System.out.println(name + " is going to bed.");
            this.currentState = PetState.SLEEPING;
            updateState();
        }
    }

    public void revive() {
        System.out.println("Reviving " + this.name + "!");
        this.health = this.maxHealth;
        this.sleep = this.maxSleep;
        this.fullness = this.maxFullness;
        this.happiness = this.maxHappiness;
        this.currentState = PetState.NORMAL;
    }

    public void increaseHealth(int amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
    }

    public void decreaseHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
        if (this.health == 0) updateState();
    }

    public void increaseSleep(int amount) {
        this.sleep = Math.min(this.maxSleep, this.sleep + amount);
        if (currentState == PetState.SLEEPING && this.sleep >= this.maxSleep) {
             updateState();
        }
    }

    public void decreaseSleep(int amount) {
        this.sleep = Math.max(0, this.sleep - amount);
        if (this.sleep == 0 && currentState != PetState.SLEEPING) {
            updateState();
        }
    }

    public void increaseFullness(int amount) {
        boolean wasHungry = (this.fullness <= 0);
        this.fullness = Math.min(this.maxFullness, this.fullness + amount);
        if (wasHungry && this.fullness > 0) {
            updateState();
        }
    }

    public void decreaseFullness(int amount) {
        this.fullness = Math.max(0, this.fullness - amount);
        if (this.fullness == 0) updateState();
    }

    public void increaseHappiness(int amount) {
        boolean wasAngry = (this.happiness <= 0);
        this.happiness = Math.min(this.maxHappiness, this.happiness + amount);
        if (wasAngry && this.happiness >= ANGRY_MIN_HAPPINESS_EXIT) {
             updateState();
        }
    }

    public void decreaseHappiness(int amount) {
        this.happiness = Math.max(0, this.happiness - amount);
        if (this.happiness == 0) updateState();
    }

    public boolean isPlayAvailable() {
        return System.currentTimeMillis() >= lastPlayTime + PLAY_COOLDOWN_MS;
    }

    public boolean isVetAvailable() {
        return System.currentTimeMillis() >= lastVetTime + VET_COOLDOWN_MS;
    }

    public boolean play() {
        if (!isPlayAvailable()) {
            System.out.println(name + " doesn't want to play right now (cooldown)...");
            return false;
        }
        System.out.println("Playing with " + name + "...");
        increaseHappiness(15);
        lastPlayTime = System.currentTimeMillis();
        return true;
    }

    public boolean takeToVet() {
        if (!isVetAvailable()) {
            System.out.println(name + " doesn't need the vet right now (cooldown)...");
            return false;
        }
        System.out.println("Taking " + name + " to the vet...");
        increaseHealth(30);
        lastVetTime = System.currentTimeMillis();
        return true;
    }

    public void exercise() {
        System.out.println("Exercising with " + name + "...");
        increaseHealth(5);
        decreaseSleep(10);
        decreaseFullness(15);
    }

    public void feed(FoodItem food) {
        if (food == null) return;
        System.out.println("Feeding " + name + " with " + food.getName() + "...");
        increaseFullness(food.getFullnessValue());
    }

    public void receiveGift(GiftItem gift) {
        if (gift == null) return;
        System.out.println(name + " receives a " + gift.getName() + "!");
        increaseHappiness(gift.getHappinessValue());
    }

    @Override
    public String toString() {
        return String.format("Pet{name='%s', type='%s', state=%s, H=%d/%d, S=%d/%d, F=%d/%d, Hap=%d/%d}",
                name, petType, currentState,
                health, maxHealth, sleep, maxSleep, fullness, maxFullness, happiness, maxHappiness);
    }
    
    // Cooldown constants
    private static final long VET_COOLDOWN_MS = 60 * 1000 * 5;
    private static final long PLAY_COOLDOWN_MS = 60 * 1000 * 1;
}
