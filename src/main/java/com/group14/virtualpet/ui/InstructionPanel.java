package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.group14.virtualpet.Main; // For card name constants
import com.group14.virtualpet.util.AudioManager;

/**
 * Panel displaying game instructions.
 * Requirement: 3.1.3
 */
public class InstructionPanel extends JPanel implements ActionListener {

    private Consumer<String> navigateCallback;
    private JButton backButton;
    
    // Color definitions
    private final Color BACKGROUND_COLOR = new Color(230, 240, 250); // Light blue background
    private final Color SECTION_COLOR = Color.WHITE; // White for section backgrounds
    private final Color HEADER_COLOR = new Color(45, 175, 75); // Green for headers
    private final Color TEXT_COLOR = new Color(50, 50, 50); // Dark gray for text

    public InstructionPanel(Consumer<String> navigateCallback) {
        this.navigateCallback = navigateCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        // --- Title Panel ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(SECTION_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JLabel titleLabel = new JLabel("Virtual Pet Tutorial", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Everything you need to know to care for your digital companion", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        add(titlePanel, BorderLayout.NORTH);

        // --- Instructions Content ---
        JPanel instructionsContainer = new JPanel();
        instructionsContainer.setLayout(new BoxLayout(instructionsContainer, BoxLayout.Y_AXIS));
        instructionsContainer.setBackground(BACKGROUND_COLOR);
        
        // Add each section
        instructionsContainer.add(createSection("GOAL", 
            "Keep your virtual pet happy, healthy, and thriving! Monitor your pet's stats and respond to its needs."));
        
        instructionsContainer.add(createStatsSection());
        
        instructionsContainer.add(createSection("PET STATES", 
            "• Normal - Your pet is fine and can perform all actions.\n" +
            "• Sleeping - Recovering sleep. Most commands are disabled.\n" +
            "• Hungry - Fullness is 0. Health drops and mood worsens.\n" +
            "• Angry - Happiness is 0. Ignores most commands.\n" +
            "• Dead - Game Over. Load a save or start a new game."));
        
        instructionsContainer.add(createSection("COMMANDS", 
            "• Feed - Increases Fullness (requires food from inventory).\n" +
            "• Go to Bed - Recovers Sleep over time.\n" +
            "• Give Gift - Boosts Happiness (requires gift from inventory).\n" +
            "• Take to Vet - Increases Health (has cooldown period).\n" +
            "• Play - Boosts Happiness (has cooldown period).\n" +
            "• Exercise - Boosts Health, but lowers Sleep & Fullness.\n\n" +
            "Note: Some commands are disabled depending on pet state."));
        
        // Add the new controls section
        instructionsContainer.add(createControlsSection());
        
        instructionsContainer.add(createSection("INVENTORY", 
            "Items (Food & Gifts) appear randomly over time.\n" +
            "Collect and use them to care for your pet."));
        
        instructionsContainer.add(createSection("SAVING", 
            "Click 'Save Game' to save your progress.\n" +
            "Your pet's name will be used for the save file."));
        
        instructionsContainer.add(createPetTypesSection());
        
        instructionsContainer.add(createSection("TIPS", 
            "• Watch your pet's stats carefully - they change over time.\n" +
            "• Balance all needs - don't focus on just one stat.\n" +
            "• Save regularly to avoid losing progress.\n" +
            "• Different pet types have different difficulty levels.\n" +
            "• Experiment with different commands to see their effects."));
        
        // Add a final encouraging message
        JPanel finalPanel = new JPanel();
        finalPanel.setBackground(SECTION_COLOR);
        finalPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        finalPanel.setLayout(new BoxLayout(finalPanel, BoxLayout.Y_AXIS));
        finalPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel finalMessage = new JLabel("Have fun caring for your digital companion!", SwingConstants.CENTER);
        finalMessage.setFont(new Font("Arial", Font.BOLD, 16));
        finalMessage.setForeground(HEADER_COLOR);
        finalMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        finalPanel.add(finalMessage);
        instructionsContainer.add(finalPanel);
        
        // Add some space at the bottom
        instructionsContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(instructionsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        backButton = createGreenButton("Back to Main Menu");
        backButton.addActionListener(this);
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the controls section that shows keyboard shortcuts
     */
    private JPanel createControlsSection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(SECTION_COLOR);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));
        sectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        // Section title
        JLabel titleLabel = new JLabel("KEYBOARD SHORTCUTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create a panel for the controls grid
        JPanel controlsGrid = new JPanel();
        controlsGrid.setLayout(new BoxLayout(controlsGrid, BoxLayout.Y_AXIS));
        controlsGrid.setBackground(SECTION_COLOR);
        controlsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add each control entry with spacing
        controlsGrid.add(createControlEntry("Feed Pet", "Shortcut to feed your pet", "F"));
        controlsGrid.add(Box.createRigidArea(new Dimension(0, 15)));
        controlsGrid.add(createControlEntry("Play with Pet", "Shortcut to play with your pet", "P"));
        controlsGrid.add(Box.createRigidArea(new Dimension(0, 15)));
        controlsGrid.add(createControlEntry("Sleep Mode", "Shortcut to make your pet sleep", "S"));
        controlsGrid.add(Box.createRigidArea(new Dimension(0, 15)));
        controlsGrid.add(createControlEntry("Give Gift", "Shortcut to give your pet a gift", "G"));
        controlsGrid.add(Box.createRigidArea(new Dimension(0, 15)));
        controlsGrid.add(createControlEntry("Exercise", "Shortcut to exercise your pet", "E"));
        
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionPanel.add(controlsGrid);
        
        return sectionPanel;
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
     * Creates a styled section panel with header and content
     */
    private JPanel createSection(String title, String content) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(SECTION_COLOR);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));
        sectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Section content
        JLabel contentLabel = new JLabel("<html><div style='width: 400px;'>" + content.replace("\n", "<br>") + "</div></html>");
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentLabel.setForeground(TEXT_COLOR);
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionPanel.add(contentLabel);
        
        return sectionPanel;
    }
    
    /**
     * Creates a special section for pet stats with colored indicators
     */
    private JPanel createStatsSection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(SECTION_COLOR);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));
        sectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        // Section title
        JLabel titleLabel = new JLabel("PET STATS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create a grid for the stats
        JPanel statsGrid = new JPanel(new GridLayout(4, 1, 0, 10));
        statsGrid.setBackground(SECTION_COLOR);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add each stat with colored indicator
        statsGrid.add(createStatRow("Health", "Reaches 0 = Game Over", new Color(220, 50, 50))); // Red
        statsGrid.add(createStatRow("Sleep", "Goes down over time. Let your pet rest", new Color(100, 100, 255))); // Blue
        statsGrid.add(createStatRow("Fullness", "Goes down over time. Feed your pet", new Color(255, 165, 0))); // Orange
        statsGrid.add(createStatRow("Happiness", "Boost with gifts or playtime", new Color(50, 205, 50))); // Green
        
        // Warning about low stats
        JLabel warningLabel = new JLabel("Stats turn RED below 25%. Keep an eye on them!");
        warningLabel.setFont(new Font("Arial", Font.BOLD, 14));
        warningLabel.setForeground(new Color(220, 50, 50)); // Red warning
        warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionPanel.add(statsGrid);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionPanel.add(warningLabel);
        
        return sectionPanel;
    }
    
    /**
     * Creates a row for a single stat with colored indicator
     */
    private JPanel createStatRow(String statName, String description, Color color) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBackground(SECTION_COLOR);
        
        // Colored indicator
        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(color);
        colorIndicator.setPreferredSize(new Dimension(20, 20));
        colorIndicator.setMaximumSize(new Dimension(20, 20));
        colorIndicator.setMinimumSize(new Dimension(20, 20));
        
        // Stat name
        JLabel nameLabel = new JLabel(statName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setPreferredSize(new Dimension(100, 20));
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        rowPanel.add(colorIndicator);
        rowPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rowPanel.add(nameLabel);
        rowPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rowPanel.add(descLabel);
        
        return rowPanel;
    }
    
    /**
     * Creates a special section for pet types with difficulty indicators
     */
    private JPanel createPetTypesSection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(SECTION_COLOR);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));
        sectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        
        // Section title
        JLabel titleLabel = new JLabel("PET TYPES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create a grid for the pet types
        JPanel typesGrid = new JPanel(new GridLayout(3, 1, 0, 10));
        typesGrid.setBackground(SECTION_COLOR);
        typesGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add each pet type with difficulty indicator
        typesGrid.add(createPetTypeRow("RoboFriend", "Easy. Great for beginners.", new Color(144, 238, 144))); // Light green
        typesGrid.add(createPetTypeRow("MechaMate", "Medium difficulty.", new Color(255, 165, 0))); // Orange
        typesGrid.add(createPetTypeRow("Tech Titan", "Hard. Best for experienced players.", new Color(255, 99, 71))); // Tomato red
        
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sectionPanel.add(typesGrid);
        
        return sectionPanel;
    }
    
    /**
     * Creates a row for a pet type with difficulty indicator
     */
    private JPanel createPetTypeRow(String petName, String description, Color color) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBackground(SECTION_COLOR);
        
        // Difficulty indicator
        JPanel difficultyIndicator = new JPanel();
        difficultyIndicator.setBackground(color);
        difficultyIndicator.setPreferredSize(new Dimension(20, 20));
        difficultyIndicator.setMaximumSize(new Dimension(20, 20));
        difficultyIndicator.setMinimumSize(new Dimension(20, 20));
        
        // Pet name
        JLabel nameLabel = new JLabel(petName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setPreferredSize(new Dimension(120, 20));
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        rowPanel.add(difficultyIndicator);
        rowPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rowPanel.add(nameLabel);
        rowPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rowPanel.add(descLabel);
        
        return rowPanel;
    }
    
    /**
     * Creates a green button with white text, matching the style in MainMenuPanel
     */
    private JButton createGreenButton(String text) {
        JButton button = new JButton(text);
        
        // Style the button to match the green design in the MainMenuPanel
        button.setBackground(new Color(45, 175, 75)); // Bright green color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Ensure the background is painted
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set size constraints
        button.setPreferredSize(new Dimension(250, 40));
        button.setMaximumSize(new Dimension(250, 40));
        
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            // Play sound effect on button press
            try {
                AudioManager.getInstance().playSoundEffect("mainbuttonSound.mp3");
            } catch (Exception ex) {
                // Silently handle if audio manager isn't available
            }
            
            if (navigateCallback != null) {
                navigateCallback.accept(Main.MAIN_MENU_CARD);
            }
        }
    }
} 