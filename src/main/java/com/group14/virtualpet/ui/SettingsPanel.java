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
    
    // Controls are created dynamically in createControlEntry method
    
    // Buttons
    private final JButton backButton;
    private final JButton applyButton;

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
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Add sound settings section
        contentPanel.add(createSoundSettingsPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add controls section
        contentPanel.add(createControlsPanel());
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Create footer with buttons
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        backButton = createButton("Back to Menu", new Color(240, 80, 80));
        backButton.addActionListener(this);
        
        applyButton = createButton("Apply Changes", new Color(80, 180, 80));
        applyButton.addActionListener(this);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.add(applyButton);
        
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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Customize your virtual pet experience", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
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
        JPanel musicPanel = new JPanel(new BorderLayout());
        musicPanel.setOpaque(false);
        
        JPanel musicLabelPanel = new JPanel(new BorderLayout());
        musicLabelPanel.setOpaque(false);
        
        JLabel musicLabel = new JLabel("Background Music");
        musicLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel musicDescLabel = new JLabel("Play music while the game is running");
        musicDescLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        musicDescLabel.setForeground(Color.GRAY);
        
        musicLabelPanel.add(musicLabel, BorderLayout.NORTH);
        musicLabelPanel.add(musicDescLabel, BorderLayout.CENTER);
        
        // Initialize checkbox with current audio manager state
        backgroundMusicCheckbox = createToggleSwitch();
        backgroundMusicCheckbox.setSelected(AudioManager.getInstance().isMusicEnabled());
        
        // Add change listener to toggle music when checkbox is changed
        backgroundMusicCheckbox.addActionListener(e -> {
            boolean isSelected = backgroundMusicCheckbox.isSelected();
            AudioManager.getInstance().setMusicEnabled(isSelected);
        });
        
        musicPanel.add(musicLabelPanel, BorderLayout.WEST);
        musicPanel.add(backgroundMusicCheckbox, BorderLayout.EAST);
        
        panel.add(musicPanel);
        
        return panel;
    }
    
    /**
     * Creates the controls panel
     */
    private JPanel createControlsPanel() {
        JPanel panel = createSectionPanel("Controls");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Create control entries
        panel.add(createControlEntry("Feed Pet", "Shortcut to feed your pet", "F"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createControlEntry("Play with Pet", "Shortcut to play with your pet", "P"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createControlEntry("Sleep Mode", "Shortcut to make your pet sleep", "S"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createControlEntry("Give Gift", "Shortcut to give your pet a gift", "G"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createControlEntry("Exercise", "Shortcut to exercise your pet", "E"));
        
        return panel;
    }
    
    /**
     * Creates a control entry with label, description, and key binding
     */
    private JPanel createControlEntry(String label, String description, String key) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        labelPanel.add(nameLabel, BorderLayout.NORTH);
        labelPanel.add(descLabel, BorderLayout.CENTER);
        
        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        keyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        keyLabel.setPreferredSize(new Dimension(30, 30));
        keyLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        keyLabel.setBackground(new Color(245, 245, 245));
        keyLabel.setOpaque(true);
        
        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(keyLabel, BorderLayout.EAST);
        
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
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Add title to the panel
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(),
            title
        );
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 16));
        panel.setBorder(BorderFactory.createCompoundBorder(
            panel.getBorder(),
            titledBorder
        ));
        
        return panel;
    }
    
    /**
     * Creates a toggle switch (checkbox styled as a switch)
     */
    private JCheckBox createToggleSwitch() {
        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(true);
        toggle.setOpaque(false);
        toggle.setIcon(createToggleIcon(false));
        toggle.setSelectedIcon(createToggleIcon(true));
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
                
                // Draw the track
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(selected ? new Color(80, 180, 80) : Color.LIGHT_GRAY);
                g2d.fillRoundRect(x, y + 3, 40, 14, 14, 14);
                
                // Draw the thumb
                g2d.setColor(selected ? new Color(40, 160, 40) : Color.GRAY);
                g2d.fillOval(selected ? x + 26 : x, y, 20, 20);
                
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
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        // Add a subtle shadow effect with a darker bottom border
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Make buttons larger
        button.setPreferredSize(new Dimension(180, 50));
        button.setMinimumSize(new Dimension(180, 50));
        
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            navigateCallback.accept(Main.MAIN_MENU_CARD);
        } else if (e.getSource() == applyButton) {
            // Save settings
            saveSettings();
            // Show confirmation
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Settings have been saved successfully!",
                "Settings Saved",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
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
