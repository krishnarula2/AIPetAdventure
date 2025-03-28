package com.group14.virtualpet.model;

/**
 * Class to handle a single vital statistic (e.g., health, sleep, fullness, happiness).
 * Manages its current value, maximum value, decay rate, and provides methods for modification and checking thresholds.
 */
public class VitalStatistic {

    private final int maxValue;
    private double currentValue;
    private final double decayRate; // Amount to decrease per update tick
    private final double warningThreshold; // Typically 25% of maxValue

    /**
     * Constructor for VitalStatistic.
     * @param maxValue The maximum value this statistic can reach.
     * @param initialValue The starting value for this statistic.
     * @param decayRate The rate at which this statistic decreases over time (per update tick). Set to 0 if it doesn't decay naturally.
     */
    public VitalStatistic(int maxValue, double initialValue, double decayRate) {
        if (maxValue <= 0) {
            throw new IllegalArgumentException("Max value must be positive.");
        }
        this.maxValue = maxValue;
        this.currentValue = Math.min(Math.max(initialValue, 0), maxValue); // Clamp initial value
        this.decayRate = decayRate >= 0 ? decayRate : 0; // Ensure decay rate is not negative
        this.warningThreshold = maxValue * 0.25;
    }

    /** Increases the current value by the specified amount, capped at maxValue. */
    public void increase(double amount) {
        if (amount < 0) return; // Or throw exception
        this.currentValue = Math.min(this.currentValue + amount, this.maxValue);
    }

    /** Decreases the current value by the specified amount, capped at 0. */
    public void decrease(double amount) {
        if (amount < 0) return; // Or throw exception
        this.currentValue = Math.max(this.currentValue - amount, 0.0);
    }

    /** Applies the natural decay over one time unit. */
    public void decay() {
        decrease(this.decayRate);
    }

    /** Sets the current value directly to the maximum value. */
    public void setToMax() {
        this.currentValue = this.maxValue;
    }

    /** Returns the current value. */
    public double getCurrentValue() {
        return currentValue;
    }

    /** Returns the maximum value. */
    public int getMaxValue() {
        return maxValue;
    }
    
    /** Returns the decay rate per tick */
    public double getDecayRate() {
        return decayRate;
    }

    /** Checks if the current value is at or below the warning threshold (25% of max). */
    public boolean isBelowWarningThreshold() {
        return this.currentValue <= this.warningThreshold;
    }

    /** Checks if the current value is effectively zero (at or below 0). */
    public boolean isZero() {
        return this.currentValue <= 0.0;
    }
} 