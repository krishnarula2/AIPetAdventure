package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.group14.virtualpet.Main;
import com.group14.virtualpet.model.Pet;

/**
 * Panel for selecting a pet type and naming the new pet.
 * Requirements: 3.1.4
 */
public class PetSelectionPanel extends JPanel implements ActionListener {

    private Consumer<String> navigateCallback;
    private Consumer<Pet> startGameCallback;

    private JTextField petNameField;
    private JButton backButton;
    private JButton confirmButton;

    // Pet selection panels
    private JPanel petCardsPanel;
    private JRadioButton roboFriendRadio;
    private JRadioButton mechaMateRadio;
    private JRadioButton techTitanRadio;
    private ButtonGroup petTypeGroup;
    
    private String selectedPetType = "friendly_robot"; // Default selection
    
    // Color definitions
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Light blue
    private final Color EASY_COLOR = new Color(144, 238, 144); // Light green
    private final Color MEDIUM_COLOR = new Color(255, 165, 0); // Orange
    private final Color HARD_COLOR = new Color(255, 99, 71); // Tomato red

    public PetSelectionPanel(Consumer<String> navigateCallback, Consumer<Pet> startGameCallback) {
        this.navigateCallback = navigateCallback;
        this.startGameCallback = startGameCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        // --- Title ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Choose Your Pet", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel subtitleLabel = new JLabel("Select a virtual companion to begin your journey", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // --- Center: Pet Selection Cards ---
        petCardsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        petCardsPanel.setBackground(BACKGROUND_COLOR);
        
        petTypeGroup = new ButtonGroup();
        
        // Create pet cards
        JPanel roboFriendCard = createPetCard(
                "friendly_robot", 
                "Easy", 
                "A friendly robot companion that's easy to care for and very forgiving. Perfect for beginners.", 
                EASY_COLOR, 
                2, 3, 4);
                
        JPanel mechaMateCard = createPetCard(
                "balanced_robot", 
                "Medium", 
                "A balanced robot pet with moderate needs. Great for most players!", 
                MEDIUM_COLOR, 
                3, 4, 3);
                
        JPanel techTitanCard = createPetCard(
                "challenging_robot", 
                "Hard", 
                "An advanced robot pet that requires careful attention and skill. For experienced players only!", 
                HARD_COLOR, 
                5, 4, 2);
        
        petCardsPanel.add(roboFriendCard);
        petCardsPanel.add(mechaMateCard);
        petCardsPanel.add(techTitanCard);
        add(petCardsPanel, BorderLayout.CENTER);

        // --- Bottom: Name Your Pet ---
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 20));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        
        // Name section
        JPanel namePanel = new JPanel(new BorderLayout(0, 10));
        namePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel nameHeader = new JLabel("Name Your Pet", SwingConstants.CENTER);
        nameHeader.setFont(new Font("Arial", Font.BOLD, 18));
        namePanel.add(nameHeader, BorderLayout.NORTH);
        
        petNameField = new JTextField("Enter pet name...");
        petNameField.setHorizontalAlignment(JTextField.CENTER);
        namePanel.add(petNameField, BorderLayout.CENTER);
        
        bottomPanel.add(namePanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        backButton = new JButton("Back");
        backButton.addActionListener(this);
        
        confirmButton = new JButton("Confirm Selection");
        confirmButton.addActionListener(this);
        
        buttonPanel.add(backButton);
        buttonPanel.add(confirmButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPetCard(String petType, String difficulty, String description, Color themeColor, int hungerValue, int sleepValue, int happyValue) {
        JPanel card = new JPanel();
        card.setBorder(BorderFactory.createLineBorder(themeColor, 2));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        // Center elements horizontally
        card.setAlignmentX(CENTER_ALIGNMENT);
        
        // Pet image
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(Color.WHITE);
        JLabel imageLabel = new JLabel();
        
        // Load pet image if available
        ImageIcon petIcon = loadPetImage(petType);
        if (petIcon != null) {
            imageLabel.setIcon(petIcon);
        } else {
            imageLabel.setText("[" + petType + " Image]");
        }
        
        imagePanel.add(imageLabel);
        card.add(imagePanel);
        
        // Pet name
        String displayName = getPetDisplayName(petType);
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(nameLabel);
        
        // Difficulty
        JLabel diffLabel = new JLabel("Difficulty: " + difficulty);
        diffLabel.setForeground(themeColor);
        diffLabel.setFont(new Font("Arial", Font.BOLD, 12));
        diffLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(diffLabel);
        
        // Add spacing
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Stat bars
        JPanel statPanel1 = createSquareStatBar("Hunger Rate:", hungerValue, 5, themeColor);
        JPanel statPanel2 = createSquareStatBar("Energy Need:", sleepValue, 5, themeColor);
        JPanel statPanel3 = createSquareStatBar("Mood:", happyValue, 5, themeColor);
        
        card.add(statPanel1);
        card.add(statPanel2);
        card.add(statPanel3);
        
        // Add spacing
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Pet description
        JLabel descLabel = new JLabel("<html><div style='width:170px; text-align:center'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(descLabel);
        
        // Pet selection radio button
        JRadioButton selectButton = new JRadioButton();
        selectButton.setBackground(Color.WHITE);
        selectButton.setAlignmentX(CENTER_ALIGNMENT);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(selectButton);
        card.add(radioPanel);
        
        petTypeGroup.add(selectButton);
        
        // Store references to the radio buttons
        if (petType.equals("friendly_robot")) {
            roboFriendRadio = selectButton;
            roboFriendRadio.setSelected(true); // Default selection
        } else if (petType.equals("balanced_robot")) {
            mechaMateRadio = selectButton;
        } else if (petType.equals("challenging_robot")) {
            techTitanRadio = selectButton;
        }
        
        selectButton.addActionListener(e -> selectedPetType = petType);
        
        return card;
    }
    
    private JPanel createSquareStatBar(String labelText, int value, int maxValue, Color themeColor) {
        JPanel statPanel = new JPanel(new BorderLayout(5, 0));
        statPanel.setBackground(Color.WHITE);
        
        // Label on left side
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        statPanel.add(label, BorderLayout.WEST);
        
        // Stat blocks in center
        JPanel blocksPanel = new JPanel(new GridLayout(1, maxValue, 2, 0));
        blocksPanel.setBackground(Color.WHITE);
        
        for (int i = 0; i < maxValue; i++) {
            JPanel block = new JPanel();
            // Make blocks square with increased size
            block.setPreferredSize(new Dimension(30, 30));
            block.setMinimumSize(new Dimension(30, 30));
            
            if (i < value) {
                block.setBackground(themeColor);
            } else {
                block.setBackground(Color.LIGHT_GRAY);
            }
            
            blocksPanel.add(block);
        }
        
        statPanel.add(blocksPanel, BorderLayout.CENTER);
        
        // Add some padding around the stat panel
        statPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        return statPanel;
    }
    
    /** Loads an ImageIcon for the specified pet type */
    private ImageIcon loadPetImage(String petType) {
        if (petType == null) return null;
        
        String imagePath;
        // Use specific paths for each robot type
        switch (petType) {
            case "friendly_robot":
                imagePath = "/images/pets/friendly_robot/friendly_robot.png";
                break;
            case "balanced_robot":
                imagePath = "/images/pets/balanced_robot/balanced_robot.png";
                break;
            case "challenging_robot":
                imagePath = "/images/pets/challenging_robot/challenging_robot.png";
                break;
            default:
                imagePath = "/images/pets/" + petType + ".png";
                break;
        }
        
        URL imageURL = getClass().getResource(imagePath);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            System.err.println("Warning: Could not load image for " + petType + " at path: " + imagePath);
            return null;
        }
    }

    /** Helper to get the string representation of the selected pet type */
    private String getSelectedPetType() {
        return selectedPetType;
    }

    /** Get a user-friendly display name for the pet type */
    private String getPetDisplayName(String petType) {
        switch (petType) {
            case "friendly_robot": return "RoboFriend";
            case "balanced_robot": return "MechaMate";
            case "challenging_robot": return "TechTitan";
            default: return petType;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == confirmButton) {
            String petName = petNameField.getText().trim();
            if (petName.equals("Enter pet name...")) {
                petName = "";
            }
            
            String selectedType = getSelectedPetType();

            if (selectedType == null) {
                JOptionPane.showMessageDialog(this, "Please select a pet type.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (petName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name for your pet.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (petName.length() > 20) {
                 JOptionPane.showMessageDialog(this, "Pet name is too long (max 20 characters).", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            System.out.println("Creating pet: Name=" + petName + ", Type=" + selectedType);
            Pet newPet = new Pet(petName, selectedType);

            if (startGameCallback != null) {
                startGameCallback.accept(newPet);
            }

        } else if (source == backButton) {
            if (navigateCallback != null) {
                navigateCallback.accept(Main.MAIN_MENU_CARD);
            }
        }
    }

    public void resetFields() {
        petNameField.setText("Enter pet name...");
        if (roboFriendRadio != null) {
            roboFriendRadio.setSelected(true);
            selectedPetType = "friendly_robot";
        }
    }
} 