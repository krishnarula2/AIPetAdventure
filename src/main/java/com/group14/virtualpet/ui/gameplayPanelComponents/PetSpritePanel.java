package com.group14.virtualpet.ui.gameplayPanelComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.group14.virtualpet.model.PetState;

/**
 * Panel that displays the pet sprite with a colorful animated background.
 */
public class PetSpritePanel extends JPanel {
    
    private JLabel petSpriteLabel;
    private ColorfulBackgroundPanel backgroundPanel;
    
    public PetSpritePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Create the background panel
        backgroundPanel = new ColorfulBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        
        // Create pet sprite label
        petSpriteLabel = new JLabel();
        petSpriteLabel.setOpaque(false);
        petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        petSpriteLabel.setVerticalAlignment(JLabel.CENTER);
        
        // Add sprite label to background panel
        backgroundPanel.add(petSpriteLabel, BorderLayout.CENTER);
        
        // Add background panel to this panel
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    /**
     * Updates the pet sprite based on pet type and state.
     * 
     * @param petType The type of pet (e.g., "Dog", "Cat")
     * @param state The current state of the pet
     * @param spriteFlipFlop Animation toggle for sprite alternation
     */
    public void updateSprite(String petType, PetState state, boolean spriteFlipFlop) {
        if (petType == null || state == null) {
            petSpriteLabel.setIcon(null);
            petSpriteLabel.setText("[Sprite Error]");
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
            return;
        }
        
        // Load the appropriate sprite
        ImageIcon originalIcon = loadPetSprite(petType, state);
        
        if (originalIcon != null) {
            // Scaling Logic
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();
            int maxWidth = 300;
            int maxHeight = 300;
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            // Calculate scaling factor to fit within bounds while maintaining aspect ratio
            if (originalWidth > maxWidth) {
                double ratio = (double) maxWidth / originalWidth;
                newWidth = maxWidth;
                newHeight = (int) (originalHeight * ratio);
            }

            if (newHeight > maxHeight) {
                double ratio = (double) maxHeight / newHeight;
                newHeight = maxHeight;
                newWidth = (int) (newWidth * ratio);
            }

            // Scale the image
            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            petSpriteLabel.setIcon(scaledIcon);
            petSpriteLabel.setText(null);
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
            petSpriteLabel.setVerticalAlignment(JLabel.CENTER);
        } else {
            petSpriteLabel.setIcon(null);
            petSpriteLabel.setText("[Sprite Error]");
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        }
    }
    
    private ImageIcon loadPetSprite(String petType, PetState state) {
        if (petType == null || state == null) return null;
    
        // Convert PetState enum to a capitalized string
        String capitalizedState = state.name().substring(0, 1) 
                                  + state.name().substring(1).toLowerCase();
    
        // Build the filename based on state
        String fileName;
        if (state == PetState.NORMAL) {
            fileName = petType + ".png"; 
        } else {
            fileName = petType + "_" + capitalizedState + ".png";
        }
    
        // Build the full resource path
        String resourcePath = "/images/pets/" + petType + "/" + fileName;
    
        // Load the image
        URL imageURL = getClass().getResource(resourcePath);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            return null;
        }
    }
    
    /**
     * Inner class for the colorful animated background.
     */
    private class ColorfulBackgroundPanel extends JPanel {
        private final Color[] colors = {
            new Color(255, 192, 203), // Pink
            new Color(173, 216, 230), // Light Blue
            new Color(152, 251, 152), // Pale Green
            new Color(255, 255, 153)  // Light Yellow
        };
        
        private final float[] fractions = {0.0f, 0.33f, 0.66f, 1.0f};
        private final long startTime;
        private final Timer animationTimer;
        
        public ColorfulBackgroundPanel() {
            setOpaque(false);
            startTime = System.currentTimeMillis();
            
            // Create animation timer to repaint the panel periodically
            animationTimer = new Timer(50, e -> repaint());
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g.create();
            
            int width = getWidth();
            int height = getHeight();
            
            // Calculate time-based offset for animation
            long elapsed = System.currentTimeMillis() - startTime;
            float offset = (elapsed % 5000) / 5000.0f;
            
            // Create a circular radial gradient that shifts colors over time
            float centerX = width / 2.0f;
            float centerY = height / 2.0f;
            float radius = Math.max(width, height) * 0.7f;
            
            // Shift the colors based on time
            Color[] shiftedColors = new Color[colors.length];
            for (int i = 0; i < colors.length; i++) {
                int index = (i + (int)(offset * colors.length)) % colors.length;
                shiftedColors[i] = colors[index];
            }
            
            // Create radial gradient paint
            RadialGradientPaint paint = new RadialGradientPaint(
                centerX, centerY, radius,
                fractions, shiftedColors,
                MultipleGradientPaint.CycleMethod.REFLECT
            );
            
            g2d.setPaint(paint);
            
            // Draw a rounded rectangle with the gradient
            int arcSize = 30;
            g2d.fillRoundRect(0, 0, width, height, arcSize, arcSize);
            
            g2d.dispose();
        }
    }
} 