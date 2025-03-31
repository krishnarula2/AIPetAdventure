package com.group14.virtualpet.ui.gameplayPanelComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.model.PetState;

/**
 * Panel that contains command buttons for interacting with the pet.
 */
public class CommandPanel extends JPanel {
    
    private JButton feedButton;
    private JButton goToBedButton;
    private JButton giveGiftButton;
    private JButton vetButton;
    private JButton playButton;
    private JButton exerciseButton;
    private JButton saveButton;
    
    private final GameplayPanel gameplayPanel;
    
    public CommandPanel(GameplayPanel gameplayPanel) {
        this.gameplayPanel = gameplayPanel;
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setOpaque(false);
        
        createButtons();
    }
    
    private void createButtons() {
        feedButton = createStyledButton("Feed", new Color(40, 167, 69));
        goToBedButton = createStyledButton("Go to Bed", new Color(0, 123, 255));
        giveGiftButton = createStyledButton("Give Gift", new Color(255, 193, 7));
        vetButton = createStyledButton("Take to Vet", new Color(108, 117, 125));
        playButton = createStyledButton("Play", new Color(23, 162, 184));
        exerciseButton = createStyledButton("Exercise", new Color(111, 66, 193));
        saveButton = createStyledButton("Save Game", new Color(52, 58, 64));
        
        // Add buttons to panel
        add(feedButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(goToBedButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(giveGiftButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(vetButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(playButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(exerciseButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(saveButton);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(isColorBright(bgColor) ? GameplayPanel.DARK_COLOR : Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(gameplayPanel);
        
        return button;
    }
    
    /**
     * Enables or disables buttons based on the pet's state.
     * 
     * @param pet The current pet
     */
    public void updateCommandAvailability(Pet pet) {
        if (pet == null) {
            setAllCommandsEnabled(false);
            // Even if there's no pet, we still want to enable the save button
            saveButton.setEnabled(true);
            return;
        }
        
        PetState state = pet.getCurrentState();
        
        // Disable all buttons first
        setAllCommandsEnabled(false);
        
        // Save button is always enabled
        saveButton.setEnabled(true);
        
        // Enable buttons based on pet state using rule-based switch
        switch (state) {
            case NORMAL, HUNGRY -> {
                // All commands available
                feedButton.setEnabled(true);
                goToBedButton.setEnabled(true);
                giveGiftButton.setEnabled(true);
                vetButton.setEnabled(pet.isVetAvailable());
                playButton.setEnabled(pet.isPlayAvailable());
                exerciseButton.setEnabled(true);
            }
            case ANGRY -> {
                // Only Give Gift and Play available
                giveGiftButton.setEnabled(true);
                playButton.setEnabled(pet.isPlayAvailable());
            }
            case SLEEPING, DEAD -> {
                // No commands available
                // All buttons remain disabled
            }
        }
    }
    
    private void setAllCommandsEnabled(boolean enabled) {
        feedButton.setEnabled(enabled);
        goToBedButton.setEnabled(enabled);
        giveGiftButton.setEnabled(enabled);
        vetButton.setEnabled(enabled);
        playButton.setEnabled(enabled);
        exerciseButton.setEnabled(enabled);
        // Save button is handled separately in updateCommandAvailability
    }
    
    private boolean isColorBright(Color color) {
        double brightness = (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255;
        return brightness > 0.5;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(GameplayPanel.LIGHT_COLOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    // Getters for the buttons to be accessed by GameplayPanel
    
    public JButton getFeedButton() {
        return feedButton;
    }
    
    public JButton getGoToBedButton() {
        return goToBedButton;
    }
    
    public JButton getGiveGiftButton() {
        return giveGiftButton;
    }
    
    public JButton getVetButton() {
        return vetButton;
    }
    
    public JButton getPlayButton() {
        return playButton;
    }
    
    public JButton getExerciseButton() {
        return exerciseButton;
    }
    
    public JButton getSaveButton() {
        return saveButton;
    }
} 