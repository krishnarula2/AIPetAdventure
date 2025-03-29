package com.group14.virtualpet;

import java.awt.CardLayout;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.ui.GameplayPanel;
import com.group14.virtualpet.ui.InstructionPanel;
import com.group14.virtualpet.ui.MainMenuPanel;
import com.group14.virtualpet.ui.ParentalControlsPanel;
import com.group14.virtualpet.ui.PetSelectionPanel;

/**
 * The main window frame for the Virtual Pet application.
 * Manages the different panels (screens) using CardLayout.
 * Requirement: 3.1.1 (Multiple Screens)
 */
public class MainFrame extends JFrame implements Consumer<String> {

    private CardLayout cardLayout;
    private JPanel mainPanel; // Panel that uses CardLayout

    // Panels (Screens)
    private MainMenuPanel mainMenuPanel;
    private GameplayPanel gameplayPanel;
    private PetSelectionPanel petSelectionPanel;
    private InstructionPanel instructionPanel;
    private ParentalControlsPanel parentalControlsPanel;
    // TODO: Add panels for Instructions, Parental Controls

    public MainFrame() {
        setTitle("Virtual Pet Game"); // Requirement 3.1.2
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setMinimumSize(new Dimension(600, 500)); // Set a reasonable minimum size

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Instantiate Panels ---
        // Pass 'this' as the navigateCallback (Consumer<String>)
        mainMenuPanel = new MainMenuPanel(this);
        gameplayPanel = new GameplayPanel(); // GameplayPanel doesn't need navigation callback directly currently
        petSelectionPanel = new PetSelectionPanel(this, this::startNewGame);
        instructionPanel = new InstructionPanel(this);
        parentalControlsPanel = new ParentalControlsPanel(this);

        // --- Add Panels to CardLayout ---
        mainPanel.add(mainMenuPanel, Main.MAIN_MENU_CARD);
        mainPanel.add(gameplayPanel, Main.GAMEPLAY_CARD);
        mainPanel.add(petSelectionPanel, Main.PET_SELECTION_CARD);
        mainPanel.add(instructionPanel, Main.INSTRUCTION_CARD);
        mainPanel.add(parentalControlsPanel, Main.PARENTAL_CONTROLS_CARD);
        // TODO: Add other panels

        // Add the main panel to the frame
        add(mainPanel);

        // Show the main menu first
        cardLayout.show(mainPanel, Main.MAIN_MENU_CARD);

        pack(); // Adjusts window size to preferred sizes of components
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    /**
     * Handles navigation requests from panels.
     * Implements the Consumer<String> interface.
     * @param cardName The string identifier of the panel to navigate to.
     */
    @Override
    public void accept(String cardName) {
        System.out.println("Navigating to: " + cardName); // Log navigation
        // Stop game timer if navigating away from gameplay panel
        if (!cardName.equals(Main.GAMEPLAY_CARD)) {
            gameplayPanel.stopGame();
        }
        // Reset parental controls panel when navigating to it
        if (cardName.equals(Main.PARENTAL_CONTROLS_CARD)) {
            parentalControlsPanel.resetPanel();
        }
        // TODO: Add reset logic for other panels if needed (e.g., PetSelectionPanel already has resetFields)
        cardLayout.show(mainPanel, cardName);
    }

    /**
     * Loads game data into the GameplayPanel and switches to it.
     * Called by MainMenuPanel after successfully loading a game.
     * @param loadedState The GameState object loaded from a file.
     */
    public void loadAndSwitchToGameplay(GameState loadedState) {
        if (loadedState != null) {
            // TODO: Implement a method in GameplayPanel to accept loaded data
            // For now, we'll assume startGame can handle a pre-existing pet
            // A dedicated loadGame(GameState state) method in GameplayPanel would be better.
            gameplayPanel.loadGameData(loadedState); // Assuming this method exists or will be added
            accept(Main.GAMEPLAY_CARD); // Navigate to gameplay
        } else {
            System.err.println("MainFrame: Cannot switch to gameplay, loaded state is null.");
            // Optionally show an error message
        }
    }

    /**
     * Starts a new game with the selected pet and switches to GameplayPanel.
     * Called by PetSelectionPanel.
     * @param selectedPet The newly created Pet object.
     */
    public void startNewGame(com.group14.virtualpet.model.Pet selectedPet) {
        if (selectedPet != null) {
             // Save the initial state immediately (Req 3.1.4)
            String filename = selectedPet.getName().trim().replaceAll("[^a-zA-Z0-9.-]", "_");
            GameState initialState = new GameState(selectedPet, new com.group14.virtualpet.model.Inventory(), 0); // New inventory, score 0
            com.group14.virtualpet.util.SaveLoadUtil.saveGame(initialState, filename);

            gameplayPanel.startGame(selectedPet); // Start game with the new pet
            accept(Main.GAMEPLAY_CARD); // Navigate to gameplay
        } else {
            System.err.println("MainFrame: Cannot start new game, selected pet is null.");
        }
    }

} 