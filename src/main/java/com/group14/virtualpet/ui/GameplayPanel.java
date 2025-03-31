package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.state.GameState;

/**
 * This is the main GameplayPanel class that serves as a wrapper for the 
 * refactored GameplayPanel components.
 * 
 * The actual implementation has been moved to the gameplayPanelComponents package.
 */
public class GameplayPanel extends JPanel {
    
    private final com.group14.virtualpet.ui.gameplayPanelComponents.GameplayPanel gameplayPanel;
    
    public GameplayPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(800, 600));
        
        // Create the refactored gameplay panel
        gameplayPanel = new com.group14.virtualpet.ui.gameplayPanelComponents.GameplayPanel();
        
        // Add it to this panel
        add(gameplayPanel, BorderLayout.CENTER);
    }
    
    /**
     * Starts a new game session using the given pet.
     */
    public void startGame(Pet pet) {
        gameplayPanel.startGame(pet);
    }

    /**
     * Loads a saved game state.
     */
    public void loadGameData(GameState state) {
        gameplayPanel.loadGameData(state);
    }

    /**
     * Stops the game loop and sprite timer.
     */
    public void stopGame() {
        gameplayPanel.stopGame();
    }
}
