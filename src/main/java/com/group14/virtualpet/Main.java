package com.group14.virtualpet;

import javax.swing.SwingUtilities;

/**
 * Main application class for the Virtual Pet game.
 * Launches the MainFrame.
 */
public class Main {

    // Define card names as constants
    public static final String MAIN_MENU_CARD = "MainMenu";
    public static final String GAMEPLAY_CARD = "Gameplay";
    public static final String PET_SELECTION_CARD = "PetSelection";
    public static final String INSTRUCTION_CARD = "Instructions"; // Add card name for instructions
    public static final String PARENTAL_CONTROLS_CARD = "ParentalControls"; // Add card name for parental controls
    // TODO: Add card name for PARENTAL_CONTROLS_CARD

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Use MainFrame to manage the window and panels
        new MainFrame();
        System.out.println("Application started. MainFrame created.");
    }

    // Removed static panel fields and management methods (switchPanel, startGameWithSelectedPet)
    // They are now handled within MainFrame
} 