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
 * PetSpritePanel displays the pet sprite over a colorful animated background.
 * It updates the displayed sprite based on the pet's type and state, and supports
 * a temporary movement animation (indicated by a movement mode flag).
 */
public class PetSpritePanel extends JPanel {
    
    // Label to display the pet sprite image.
    private JLabel petSpriteLabel;
    
    // Panel that shows a dynamic, animated background.
    private ColorfulBackgroundPanel backgroundPanel;
    
    /**
     * Constructor: Initializes the panel layout, background, and sprite label.
     */
    public PetSpritePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Initialize the colorful background panel.
        backgroundPanel = new ColorfulBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        
        // Create and configure the pet sprite label.
        petSpriteLabel = new JLabel();
        petSpriteLabel.setOpaque(false);
        petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        petSpriteLabel.setVerticalAlignment(JLabel.CENTER);
        
        // Add the sprite label to the background panel.
        backgroundPanel.add(petSpriteLabel, BorderLayout.CENTER);
        
        // Add the background panel to this PetSpritePanel.
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    /**
     * Updates the pet sprite image.
     * 
     * @param petType       The type of pet (e.g., "friendly_robot").
     * @param state         The current state of the pet (e.g., NORMAL, ANGRY, etc.).
     * @param spriteFlipFlop A boolean used to alternate the sprite if needed.
     * @param movementMode  If true, display the movement version of the sprite.
     */
    public void updateSprite(String petType, PetState state, boolean spriteFlipFlop, boolean movementMode) {
        // If petType or state is missing, display an error message.
        if (petType == null || state == null) {
            petSpriteLabel.setIcon(null);
            petSpriteLabel.setText("[Sprite Error]");
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
            return;
        }
        
        // Load the appropriate sprite image using the provided parameters.
        ImageIcon originalIcon = loadPetSprite(petType, state, movementMode);
        
        // If the image was successfully loaded, scale it to fit within 300x300 pixels.
        if (originalIcon != null) {
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();
            int maxWidth = 300;
            int maxHeight = 300;
            int newWidth = originalWidth;
            int newHeight = originalHeight;
            // Scale based on width if needed.
            if (originalWidth > maxWidth) {
                double ratio = (double) maxWidth / originalWidth;
                newWidth = maxWidth;
                newHeight = (int) (originalHeight * ratio);
            }
            // Scale based on height if needed.
            if (newHeight > maxHeight) {
                double ratio = (double) maxHeight / newHeight;
                newHeight = maxHeight;
                newWidth = (int) (newWidth * ratio);
            }
            // Create a scaled image.
            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            petSpriteLabel.setIcon(new ImageIcon(scaledImage));
            petSpriteLabel.setText(null);
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
            petSpriteLabel.setVerticalAlignment(JLabel.CENTER);
        } else {
            // If the image failed to load, show an error message.
            petSpriteLabel.setIcon(null);
            petSpriteLabel.setText("[Sprite Error]");
            petSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        }
    }
    
    /**
     * Loads the pet sprite image based on the pet type, state, and movement mode.
     * 
     * For the NORMAL state:
     *   - Default: "<petType>.png"
     *   - Movement: "<petType>_movement.png"
     * For other states (e.g., ANGRY, DEAD, HUNGRY, SLEEPING):
     *   - Default: "<petType>_<State>.jpeg" (State is capitalized, e.g., "Angry")
     *   - Movement: "<petType>_<State>_movement.jpeg"
     * 
     * @param petType      The pet type (folder name).
     * @param state        The current pet state.
     * @param movementMode If true, the movement image is loaded.
     * @return An ImageIcon for the sprite, or null if the resource is not found.
     */
    private ImageIcon loadPetSprite(String petType, PetState state, boolean movementMode) {
        if (petType == null || state == null) return null;
        String fileName;
        // For NORMAL state, determine the filename based on movement mode.
        if (state == PetState.NORMAL) {
            fileName = petType + (movementMode ? "_movement.png" : ".png");
        } else {
            // Convert state name to a format with first letter uppercase.
            String capitalizedState = state.name().substring(0, 1) + state.name().substring(1).toLowerCase();
            fileName = petType + "_" + capitalizedState + (movementMode ? "_movement.jpeg" : ".jpeg");
        }
        // Build the full path: /images/pets/<petType>/<fileName>
        String resourcePath = "/images/pets/" + petType + "/" + fileName;
        URL imageURL = getClass().getResource(resourcePath);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            System.err.println("Warning: Could not load sprite file: " + resourcePath);
            return null;
        }
    }
    
    /**
     * Inner class that implements a colorful animated background.
     * The background is painted as a rounded rectangle with a radial gradient
     * that cycles through a set of predefined colors.
     */
    private class ColorfulBackgroundPanel extends JPanel {
        // Array of colors to be used in the gradient.
        private final Color[] colors = {
            new Color(255, 192, 203), // Pink
            new Color(173, 216, 230), // Light Blue
            new Color(152, 251, 152), // Pale Green
            new Color(255, 255, 153)  // Light Yellow
        };
        
        // Fractional positions for the gradient stops.
        private final float[] fractions = {0.0f, 0.33f, 0.66f, 1.0f};
        
        // The start time is used to calculate the offset for the animation.
        private final long startTime;
        
        // Timer to repaint the background for animation.
        private final Timer animationTimer;
        
        /**
         * Constructor: Initializes the start time and animation timer.
         */
        public ColorfulBackgroundPanel() {
            setOpaque(false);
            startTime = System.currentTimeMillis();
            
            // Create a timer that repaints the panel every 50ms.
            animationTimer = new Timer(50, e -> repaint());
            animationTimer.start();
        }
        
        /**
         * Overrides the paintComponent method to draw an animated radial gradient.
         * The gradient shifts over time to create a dynamic background effect.
         * 
         * @param g The Graphics object for drawing.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g.create();
            int width = getWidth();
            int height = getHeight();
            
            // Calculate an offset based on the elapsed time (cycles every 5000ms).
            long elapsed = System.currentTimeMillis() - startTime;
            float offset = (elapsed % 5000) / 5000.0f;
            
            // Determine the center and radius for the radial gradient.
            float centerX = width / 2.0f;
            float centerY = height / 2.0f;
            float radius = Math.max(width, height) * 0.7f;
            
            // Create a shifted array of colors based on the offset.
            Color[] shiftedColors = new Color[colors.length];
            for (int i = 0; i < colors.length; i++) {
                int index = (i + (int)(offset * colors.length)) % colors.length;
                shiftedColors[i] = colors[index];
            }
            
            // Create a radial gradient paint using the shifted colors.
            RadialGradientPaint paint = new RadialGradientPaint(
                centerX, centerY, radius,
                fractions, shiftedColors,
                MultipleGradientPaint.CycleMethod.REFLECT
            );
            
            g2d.setPaint(paint);
            // Draw a rounded rectangle that fills the entire panel.
            int arcSize = 30;
            g2d.fillRoundRect(0, 0, width, height, arcSize, arcSize);
            
            g2d.dispose();
        }
    }
}
