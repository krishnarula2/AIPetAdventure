/**
 * Panel displaying the settings for the virtual pet game.
 * Allows the player to adjust various game preferences such as sound or difficulty level.
 * 
 * @author Group 14
 * @version 1.0
 */

package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.group14.virtualpet.Main;
import com.group14.virtualpet.util.AudioManager;

/**
 * Settings panel for the Virtual Pet game.
 * Allows users to customize their virtual pet experience.
 */
public class SettingsPanel extends JPanel implements ActionListener {

    private final Consumer<String> navigateCallback;
    
    // Sound settings
    private JCheckBox backgroundMusicCheckbox;
    
    // Buttons
    private final JButton backButton;

    /**
     * Creates a new SettingsPanel.
     * 
     * @param navigateCallback Callback for navigation between panels
     */
    public SettingsPanel(Consumer<String> navigateCallback) {
        this.navigateCallback = navigateCallback;
        
        // Set up the panel
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Add sound settings section
        contentPanel.add(createSoundSettingsPanel());
        
        // Add padding at the bottom to create space before the footer
        contentPanel.add(Box.createVerticalGlue());
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Create footer with buttons
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        backButton = createButton("Back to Menu", new Color(240, 80, 80));
        backButton.addActionListener(this);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        
        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the header panel with title and subtitle
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Customize your virtual pet experience", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    /**
     * Creates the sound settings panel
     */
    private JPanel createSoundSettingsPanel() {
        JPanel panel = createSectionPanel("Sound Settings");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Background music toggle
        JPanel musicPanel = new JPanel();
        musicPanel.setLayout(new BorderLayout(20, 0));
        musicPanel.setOpaque(false);
        musicPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        JPanel musicLabelPanel = new JPanel();
        musicLabelPanel.setLayout(new BoxLayout(musicLabelPanel, BoxLayout.Y_AXIS));
        musicLabelPanel.setOpaque(false);
        
        JLabel musicLabel = new JLabel("Background Music");
        musicLabel.setFont(new Font("Arial", Font.BOLD, 14));
        musicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel musicDescLabel = new JLabel("Play music while the game is running");
        musicDescLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        musicDescLabel.setForeground(Color.GRAY);
        musicDescLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        musicLabelPanel.add(musicLabel);
        musicLabelPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        musicLabelPanel.add(musicDescLabel);
        
        // Initialize checkbox with current audio manager state
        backgroundMusicCheckbox = createToggleSwitch();
        backgroundMusicCheckbox.setSelected(AudioManager.getInstance().isMusicEnabled());
        
        // Add change listener to toggle music when checkbox is changed
        backgroundMusicCheckbox.addActionListener(e -> {
            boolean isSelected = backgroundMusicCheckbox.isSelected();
            AudioManager.getInstance().setMusicEnabled(isSelected);
            // Auto-save settings immediately when changed
            saveSettings();
        });
        
        // Center the toggle switch vertically
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        togglePanel.setOpaque(false);
        togglePanel.add(backgroundMusicCheckbox);
        
        musicPanel.add(musicLabelPanel, BorderLayout.WEST);
        musicPanel.add(togglePanel, BorderLayout.EAST);
        
        panel.add(musicPanel);
        
        return panel;
    }
    
    /**
     * Creates a section panel with a title
     */
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Add title to the panel
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(),
            title
        );
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 16));
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            panel.getBorder(),
            titledBorder
        ));
        
        // Set a preferred size for the panel to ensure it has enough space
        panel.setPreferredSize(new Dimension(600, 150));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        return panel;
    }
    
    /**
     * Creates a toggle switch (checkbox styled as a switch)
     */
    private JCheckBox createToggleSwitch() {
        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(true);
        toggle.setOpaque(false);
        toggle.setFocusPainted(false);
        toggle.setIcon(createToggleIcon(false));
        toggle.setSelectedIcon(createToggleIcon(true));
        toggle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        toggle.setAlignmentY(Component.CENTER_ALIGNMENT);
        return toggle;
    }
    
    /**
     * Creates a toggle icon
     */
    private javax.swing.Icon createToggleIcon(boolean selected) {
        return new javax.swing.Icon() {
            @Override
            public void paintIcon(Component c, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
                
                // Draw the track with smoother appearance
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(selected ? new Color(80, 180, 80) : new Color(200, 200, 200));
                g2d.fillRoundRect(x, y + 3, 40, 14, 14, 14);
                
                // Draw the thumb with slight shadow effect
                if (selected) {
                    // Subtle shadow for ON state
                    g2d.setColor(new Color(30, 150, 30, 50));
                    g2d.fillOval(x + 25, y + 1, 22, 22);
                }
                
                g2d.setColor(selected ? new Color(40, 160, 40) : new Color(240, 240, 240));
                g2d.fillOval(selected ? x + 26 : x, y, 20, 20);
                
                // Add a subtle border to the thumb
                g2d.setColor(selected ? new Color(30, 130, 30) : new Color(180, 180, 180));
                g2d.drawOval(selected ? x + 26 : x, y, 20, 20);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 46;
            }
            
            @Override
            public int getIconHeight() {
                return 20;
            }
        };
    }
    
    /**
     * Creates a styled button
     */
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(new Color(
                        Math.max(bgColor.getRed() - 20, 0),
                        Math.max(bgColor.getGreen() - 20, 0),
                        Math.max(bgColor.getBlue() - 20, 0)
                    ));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(
                        Math.min(bgColor.getRed() + 20, 255),
                        Math.min(bgColor.getGreen() + 20, 255),
                        Math.min(bgColor.getBlue() + 20, 255)
                    ));
                } else {
                    g.setColor(bgColor);
                }
                
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(java.awt.Graphics g) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(bgColor.getRed() - 30, bgColor.getGreen() - 30, bgColor.getBlue() - 30));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        
        button.setPreferredSize(new Dimension(200, 50));
        button.setMinimumSize(new Dimension(200, 50));
        
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            // Play a button click sound effect if background music is enabled
            if (AudioManager.getInstance().isMusicEnabled()) {
                AudioManager.getInstance().playSoundEffect("mainButtonSound.mp3");
            }
            
            // Navigate back to main menu
            navigateCallback.accept(Main.MAIN_MENU_CARD);
        }
    }
    
    /**
     * Saves the current settings
     */
    private void saveSettings() {
        // Update audio settings in the AudioManager
        boolean musicEnabled = backgroundMusicCheckbox.isSelected();
        
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.setMusicEnabled(musicEnabled);
        
        System.out.println("Saving settings:");
        System.out.println("Background Music: " + musicEnabled);
    }
}
