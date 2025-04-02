/**
 * Main application class for the Virtual Pet game.
 * Launches the MainFrame and manages the overall game flow.
 * 
 * @author Group 14
 * @version 1.0
 */
package com.group14.virtualpet;

import javax.swing.SwingUtilities;

/**
 * Main application class responsible for initializing the game's GUI and controlling the flow of the game.
 */
public class Main {

    // Define card names as constants for different game screens
/**
 * Card for the main menu screen.
 */
public static final String MAIN_MENU_CARD = "MainMenu";

/**
 * Card for the gameplay screen.
 */
public static final String GAMEPLAY_CARD = "Gameplay";

/**
 * Card for the pet selection screen.
 */
public static final String PET_SELECTION_CARD = "PetSelection";

/**
 * Card for the instructions screen.
 */
public static final String INSTRUCTION_CARD = "Instructions";

/**
 * Card for the parental controls screen.
 */
public static final String PARENTAL_CONTROLS_CARD = "ParentalControls";

/**
 * Card for the settings screen.
 */
public static final String SETTINGS_CARD = "Settings";

    /**
     * Main method to launch the game.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    /**
     * Creates and shows the GUI for the application.
     * This method is executed on the event-dispatching thread for thread safety.
     */
    private static void createAndShowGUI() {
        // Initialize the MainFrame for the game
        new MainFrame();
        System.out.println("Application started. MainFrame created.");
    }
}
