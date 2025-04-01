/**
 * Main frame for the Virtual Pet game, managing the different panels and game flow.
 * 
 * @author Group 14
 * @version 1.0
 */
package com.group14.virtualpet;

import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.ui.GameplayPanel;
import com.group14.virtualpet.ui.InstructionPanel;
import com.group14.virtualpet.ui.MainMenuPanel;
import com.group14.virtualpet.ui.ParentalControlsPanel;
import com.group14.virtualpet.ui.PetSelectionPanel;
import com.group14.virtualpet.ui.SettingsPanel;
import com.group14.virtualpet.util.AudioManager;

/**
 * The main window frame for the Virtual Pet application.
 * Manages the different panels (screens) using CardLayout.
 * Requirement: 3.1.1 (Multiple Screens)
 */
public class MainFrame extends JFrame implements Consumer<String>, KeyListener {

    private final CardLayout cardLayout;
    private final JPanel mainPanel; // Panel that uses CardLayout
    private String currentCard = Main.MAIN_MENU_CARD; // Track current card

    // Panels (Screens)
    private final MainMenuPanel mainMenuPanel;
    private final GameplayPanel gameplayPanel;
    private final PetSelectionPanel petSelectionPanel;
    private final InstructionPanel instructionPanel;
    private final ParentalControlsPanel parentalControlsPanel;
    private final SettingsPanel settingsPanel;

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
        
        // Set the return to main menu callback for GameplayPanel
        gameplayPanel.setReturnToMainMenuCallback(() -> accept(Main.MAIN_MENU_CARD));
        
        petSelectionPanel = new PetSelectionPanel(this, this::startNewGame);
        instructionPanel = new InstructionPanel(this);
        parentalControlsPanel = new ParentalControlsPanel(this);
        settingsPanel = new SettingsPanel(this);

        // --- Add Panels to CardLayout ---
        mainPanel.add(mainMenuPanel, Main.MAIN_MENU_CARD);
        mainPanel.add(gameplayPanel, Main.GAMEPLAY_CARD);
        mainPanel.add(petSelectionPanel, Main.PET_SELECTION_CARD);
        mainPanel.add(instructionPanel, Main.INSTRUCTION_CARD);
        mainPanel.add(parentalControlsPanel, Main.PARENTAL_CONTROLS_CARD);
        mainPanel.add(settingsPanel, Main.SETTINGS_CARD);

        // Add the main panel to the frame
        add(mainPanel);

        // Show the main menu first
        cardLayout.show(mainPanel, Main.MAIN_MENU_CARD);

        pack(); // Adjusts window size to preferred sizes of components
        setLocationRelativeTo(null); // Center the window
        // Initialize keyboard handling after construction
        SwingUtilities.invokeLater(this::initializeKeyboardHandling);
        
        // Initialize audio manager and explicitly start background music
        AudioManager.getInstance().startBackgroundMusic();
        
        // Add window listener to clean up resources when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Clean up resources
                AudioManager.getInstance().cleanup();
            }
        });
        
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
        
        // Update current card tracking
        currentCard = cardName;
        
        // If navigating to main menu, request focus for keyboard shortcuts
        if (cardName.equals(Main.MAIN_MENU_CARD)) {
            SwingUtilities.invokeLater(() -> {
                // Request focus in multiple ways to ensure it works
                requestFocus();
                requestFocusInWindow();
                setFocusable(true);
                System.out.println("Focus requested for MainFrame");
                
                // Re-add the key listener to ensure it's active
                for (KeyListener kl : getKeyListeners()) {
                    removeKeyListener(kl);
                }
                addKeyListener(this);
            });
        }
    }

    /**
     * Loads game data into the GameplayPanel and switches to it.
     * Called by MainMenuPanel after successfully loading a game.
     * @param loadedState The GameState object loaded from a file.
     */
    /**
     * Initialize keyboard handling after construction to avoid leaking 'this' in constructor
     */
    private void initializeKeyboardHandling() {
        // Add key listener to the frame
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        
        // Add a focus listener to detect when focus is lost
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                System.out.println("MainFrame lost focus");
                if (Main.MAIN_MENU_CARD.equals(currentCard)) {
                    SwingUtilities.invokeLater(() -> {
                        requestFocusInWindow();
                        System.out.println("Focus requested after focus loss");
                    });
                }
            }
        });
        
        System.out.println("Keyboard shortcuts initialized");
    }
    
    /**
     * Handle key press events
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // Debug logging for keyboard events
        System.out.println("Key pressed: " + KeyEvent.getKeyText(e.getKeyCode()) + 
            " (code: " + e.getKeyCode() + "), current card: " + currentCard);
        
        // Only process key pressed events when the main menu is showing
        if (Main.MAIN_MENU_CARD.equals(currentCard)) {
            // Play sound effect for relevant keys
            boolean playSound = switch (e.getKeyCode()) {
                case KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5 -> true;
                default -> false;
            };
            if (playSound) {
                AudioManager.getInstance().playSoundEffect("mainbuttonSound.mp3");
            }
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_1 -> accept(Main.PET_SELECTION_CARD);
                case KeyEvent.VK_2 -> mainMenuPanel.handleLoadGame();
                case KeyEvent.VK_3 -> accept(Main.INSTRUCTION_CARD);
                case KeyEvent.VK_4 -> accept(Main.PARENTAL_CONTROLS_CARD);
                case KeyEvent.VK_5 -> {
                    System.out.println("Navigating to Settings from key 5");
                    accept(Main.SETTINGS_CARD);
                }
                case KeyEvent.VK_ESCAPE -> {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to exit?",
                            "Exit Game",
                            JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        }
    }
    
    /**
     * Required by KeyListener interface
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    /**
     * Required by KeyListener interface
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
    
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