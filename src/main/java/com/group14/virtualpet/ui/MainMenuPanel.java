package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.group14.virtualpet.Main;
import com.group14.virtualpet.MainFrame;
import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.util.SaveLoadUtil;

/**
 * Represents the main menu screen of the game.
 * Requirements: 3.1.2
 */
public class MainMenuPanel extends JPanel implements ActionListener {

    // Buttons
    private final JButton newGameButton;
    private final JButton loadGameButton;
    private final JButton instructionsButton;
    private final JButton parentalControlsButton;
    private final JButton exitButton;

    private final Consumer<String> navigateCallback; // Callback for navigation

    // TODO: Add JLabels for title, graphic placeholder, developer names, team #, term, university info

    public MainMenuPanel(Consumer<String> navigateCallback) {
        this.navigateCallback = navigateCallback;

        // Set layout - Using BorderLayout for basic structure
        setLayout(new BorderLayout(10, 10)); // Gaps between components
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
        
        // Use light blue diagonal pattern background
        setBackground(new java.awt.Color(230, 240, 250));
        
        // --- Top Panel (Title & Graphic) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 40, 20));
        
        // Create a white background panel with rounded corners for the title
        JPanel titleBackground = new JPanel();
        titleBackground.setLayout(new BoxLayout(titleBackground, BoxLayout.Y_AXIS));
        titleBackground.setBackground(java.awt.Color.WHITE);
        titleBackground.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JLabel titleLabel = new JLabel("Virtual Pet Adventure", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Your Digital Companion", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleBackground.add(titleLabel);
        titleBackground.add(Box.createRigidArea(new Dimension(0, 5)));
        titleBackground.add(subtitleLabel);
        
        topPanel.add(titleBackground, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel (Buttons) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        
        newGameButton = createGreenButton("Start New Game", "1");
        loadGameButton = createGreenButton("Load Game", "2");
        instructionsButton = createGreenButton("Tutorial", "3");
        parentalControlsButton = createGreenButton("Parental Controls", "4");
        JButton settingsButton = createGreenButton("Settings", "5");
        exitButton = createGreenButton("Exit", "ESC");

        // Add buttons with spacing
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(loadGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(instructionsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(parentalControlsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(settingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exitButton);

        // Centering the button panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(buttonPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // --- Bottom Panel (Developer Info) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));
        
        // Create a white background panel for the footer
        JPanel footerBackground = new JPanel();
        footerBackground.setLayout(new BoxLayout(footerBackground, BoxLayout.Y_AXIS));
        footerBackground.setBackground(java.awt.Color.WHITE);
        footerBackground.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JLabel devTeamLabel = new JLabel("Development Team", SwingConstants.CENTER);
        devTeamLabel.setFont(new Font("Arial", Font.BOLD, 18));
        devTeamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel teamNumberLabel = new JLabel("Team 14", SwingConstants.CENTER);
        teamNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        teamNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel developersLabel = new JLabel(
            "Pragalvha Sharma • Aaliyan Muhammad • Athul Charanthara • Krish Narula • Manan Joshi", 
            SwingConstants.CENTER
        );
        developersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        developersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        developersLabel.setForeground(new java.awt.Color(0, 150, 64)); // Green color for names
        
        JLabel courseInfoLabel = new JLabel("Created as part of CS2212", SwingConstants.CENTER);
        courseInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        courseInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel uniTermLabel = new JLabel("Western University • Fall 2024", SwingConstants.CENTER);
        uniTermLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        uniTermLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel versionLabel = new JLabel("Version 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        footerBackground.add(devTeamLabel);
        footerBackground.add(Box.createRigidArea(new Dimension(0, 5)));
        footerBackground.add(teamNumberLabel);
        footerBackground.add(Box.createRigidArea(new Dimension(0, 10)));
        footerBackground.add(developersLabel);
        footerBackground.add(Box.createRigidArea(new Dimension(0, 10)));
        footerBackground.add(courseInfoLabel);
        footerBackground.add(uniTermLabel);
        footerBackground.add(Box.createRigidArea(new Dimension(0, 5)));
        footerBackground.add(versionLabel);
        
        bottomPanel.add(footerBackground);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize keyboard controls after construction
        initializeKeyboardControls();
        
        // Add component listener to request focus when panel becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Request focus when the panel becomes visible
                SwingUtilities.invokeLater(() -> {
                    requestFocusInWindow();
                    System.out.println("MainMenuPanel shown - requesting focus");
                });
            }
        });
    }

    /**
     * Creates a green button with white text, matching the reference design.
     * @param text The button text
     * @param shortcut The keyboard shortcut
     * @return A styled JButton
     */
    private JButton createGreenButton(String text, String shortcut) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Style the button to match the green design in the reference image
        button.setBackground(new java.awt.Color(45, 175, 75)); // Bright green color
        button.setForeground(java.awt.Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Ensure the background is painted
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set size constraints
        button.setPreferredSize(new Dimension(250, 40));
        button.setMaximumSize(new Dimension(250, 40));
        
        // Add shortcut label to the right side
        JLabel shortcutLabel = new JLabel(shortcut);
        shortcutLabel.setForeground(java.awt.Color.WHITE);
        shortcutLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Use BorderLayout for the button to position the shortcut
        button.setLayout(new BorderLayout());
        button.add(shortcutLabel, BorderLayout.EAST);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // Store button text for action handling
        button.putClientProperty("buttonText", text);
        
        return button;
    }

    /**
     * Initialize keyboard controls for the panel
     */
    private void initializeKeyboardControls() {
        // Keyboard handling is now done in MainFrame
        // This method is kept for backward compatibility
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Request focus when the panel is added to the container hierarchy
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            System.out.println("MainMenuPanel addNotify - requesting focus");
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton button) {
            String buttonText = (String) button.getClientProperty("buttonText");
            
            if (buttonText == null) {
                // Fallback to older approach if client property not found
                buttonText = button.getText();
            }
            
            if (button == newGameButton || "Start New Game".equals(buttonText)) {
                System.out.println("New Game Clicked - Navigating to Pet Selection...");
                if (navigateCallback != null) {
                    navigateCallback.accept(Main.PET_SELECTION_CARD);
                }
            } else if (button == loadGameButton || "Load Game".equals(buttonText)) {
                System.out.println("Load Game Clicked");
                handleLoadGame();
            } else if (button == instructionsButton || "Tutorial".equals(buttonText)) {
                System.out.println("Tutorial Clicked - Navigating...");
                if (navigateCallback != null) {
                    navigateCallback.accept(Main.INSTRUCTION_CARD);
                }
            } else if (button == parentalControlsButton || "Parental Controls".equals(buttonText)) {
                System.out.println("Parental Controls Clicked - Navigating...");
                if (navigateCallback != null) {
                    navigateCallback.accept(Main.PARENTAL_CONTROLS_CARD);
                }
            } else if (button == exitButton || "Exit".equals(buttonText)) {
                System.out.println("Exit Clicked");
                int choice = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to exit?",
                        "Exit Game",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else if ("Settings".equals(buttonText)) {
                System.out.println("Settings Clicked - Navigating...");
                if (navigateCallback != null) {
                    navigateCallback.accept(Main.SETTINGS_CARD);
                }
            }
        }
    }

    /** Handles the Load Game button action. Req 3.1.5 */
    // Keyboard handling is now done in MainFrame

    /**
     * Handles the Load Game button action. Req 3.1.5
     * Made public so it can be called from MainFrame
     */
    public void handleLoadGame() {
        List<String> saveFiles = SaveLoadUtil.listSaveFiles();

        if (saveFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No saved games found.",
                    "Load Game",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convert List to array for JOptionPane
        String[] choices = saveFiles.toArray(new String[0]);

        String selectedSave = (String) JOptionPane.showInputDialog(
                this,
                "Choose a game to load:",
                "Load Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0] // Default selection
        );

        if (selectedSave != null) { // User made a selection
            System.out.println("Attempting to load game: " + selectedSave);
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);

            if (loadedState != null) {
                System.out.println("Game loaded successfully. Navigating to Gameplay...");
                // We need to pass the loaded state to the MainFrame/GameplayPanel
                // This usually requires a reference to the MainFrame or a more complex event system.
                // For now, we'll assume MainFrame has a method to handle this.
                if (navigateCallback instanceof MainFrame) { // Check if callback is the MainFrame (adjust if needed)
                    ((MainFrame) navigateCallback).loadAndSwitchToGameplay(loadedState);
                } else {
                     // Fallback or error if we can't directly call MainFrame
                     System.err.println("Cannot directly trigger game load. Navigation callback is not MainFrame.");
                     JOptionPane.showMessageDialog(this, "Error: Could not initiate game load process.", "Load Error", JOptionPane.ERROR_MESSAGE);
                }

                // Old simple navigation (replace with the above)
                // if (navigateCallback != null) {
                //     navigateCallback.accept(Main.GAMEPLAY_CARD);
                // }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to load saved game: " + selectedSave,
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 