package com.group14.virtualpet.ui.gameplayPanelComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

import com.group14.virtualpet.model.Pet;

public class PetInfoPanel extends JPanel {
    
    private JLabel petNameLabel;
    private JLabel scoreLabel;
    private JProgressBar healthBar;
    private JProgressBar sleepBar;
    private JProgressBar fullnessBar;
    private JProgressBar happinessBar;
    private JLabel stateLabel;
    
    private static final double LOW_STAT_THRESHOLD = 0.25;
    
    public PetInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(300, 0));
        setBackground(GameplayPanel.LIGHT_COLOR);
        setOpaque(false);
        
        // Pet name with stylish font
        petNameLabel = new JLabel("Pet Name: ?");
        petNameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        petNameLabel.setForeground(GameplayPanel.DARK_COLOR);
        petNameLabel.setAlignmentX(LEFT_ALIGNMENT);
        petNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Score with stylish font
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(GameplayPanel.PRIMARY_COLOR);
        scoreLabel.setAlignmentX(LEFT_ALIGNMENT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        // Create status panel with titled border
        JPanel statusPanel = new RoundedPanel(10, Color.WHITE, null);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Pet Status");
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        titledBorder.setTitleColor(GameplayPanel.DARK_COLOR);
        
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statusPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Create each stat bar with proper spacing
        healthBar = createStatProgressBar("Health");
        healthBar.setAlignmentX(LEFT_ALIGNMENT);
        
        sleepBar = createStatProgressBar("Sleep");
        sleepBar.setAlignmentX(LEFT_ALIGNMENT);
        
        fullnessBar = createStatProgressBar("Fullness");
        fullnessBar.setAlignmentX(LEFT_ALIGNMENT);
        
        happinessBar = createStatProgressBar("Happiness");
        happinessBar.setAlignmentX(LEFT_ALIGNMENT);
        
        stateLabel = new JLabel("State: ?");
        stateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        stateLabel.setForeground(GameplayPanel.DARK_COLOR);
        stateLabel.setAlignmentX(LEFT_ALIGNMENT);
        stateLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        // Add components to status panel
        statusPanel.add(healthBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(sleepBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(fullnessBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(happinessBar);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statusPanel.add(stateLabel);
        
        // Add components to info panel
        add(petNameLabel);
        add(scoreLabel);
        add(statusPanel);
    }
    
    public void updatePetInfo(Pet pet, int score) {
        if (pet == null) return;
        
        petNameLabel.setText("Pet Name: " + pet.getName());
        scoreLabel.setText("Score: " + score);
        stateLabel.setText("State: " + pet.getCurrentState());
        
        updateStatBar(healthBar, pet.getHealth(), pet.getMaxHealth());
        updateStatBar(sleepBar, pet.getSleep(), pet.getMaxSleep());
        updateStatBar(fullnessBar, pet.getFullness(), pet.getMaxFullness());
        updateStatBar(happinessBar, pet.getHappiness(), pet.getMaxHappiness());
    }
    
    private JProgressBar createStatProgressBar(String label) {
        JProgressBar progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    // Draw the progress
                    int width = (int) (getWidth() * ((double) getValue() / getMaximum()));
                    
                    // Determine color based on value
                    if ((double) getValue() / getMaximum() < LOW_STAT_THRESHOLD) {
                        g2.setColor(GameplayPanel.DANGER_COLOR);
                    } else {
                        g2.setColor(GameplayPanel.SUCCESS_COLOR);
                    }
                    
                    g2.fillRoundRect(0, 0, width, getHeight(), 10, 10);
                    
                    // Draw the string
                    if (isStringPainted()) {
                        g2.setColor(Color.WHITE);
                        String text = getString();
                        int textWidth = g2.getFontMetrics().stringWidth(text);
                        int textHeight = g2.getFontMetrics().getHeight();
                        g2.drawString(text, (getWidth() - textWidth) / 2,
                            (getHeight() + textHeight) / 2 - 2);
                    }
                    
                    g2.dispose();
                }
            }
        };
        
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Create a TitledBorder instead of a regular Border
        TitledBorder titledBorder = BorderFactory.createTitledBorder(label);
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        titledBorder.setTitleColor(GameplayPanel.DARK_COLOR);
        
        progressBar.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        progressBar.setPreferredSize(new Dimension(250, 50));
        
        return progressBar;
    }
    
    private void updateStatBar(JProgressBar bar, int value, int max) {
        bar.setMaximum(max > 0 ? max : 100);
        bar.setValue(Math.max(0, value));
        bar.setString(String.format("%d / %d", bar.getValue(), bar.getMaximum()));
        
        // The color is now handled in the custom paintComponent method
        bar.repaint();
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
} 