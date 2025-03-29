package com.group14.virtualpet.ui;

import java.awt.BorderLayout; // For card names
import java.awt.Dimension; // Import Dimension class
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL; // For loading resources
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon; // For images
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
    private JButton startGameButton;
    private JButton backButton;

    // Placeholders for pet selection
    // TODO: Replace with actual pet type representations (images, info, selection mechanism)
    private JRadioButton petType1Radio;
    private JRadioButton petType2Radio;
    private JRadioButton petType3Radio;
    private ButtonGroup petTypeGroup;
    private JLabel petInfoLabel; // To display info about the selected pet type
    private JLabel petImageLabel; // Placeholder for pet image (Req 3.1.4)

    public PetSelectionPanel(Consumer<String> navigateCallback, Consumer<Pet> startGameCallback) {
        this.navigateCallback = navigateCallback;
        this.startGameCallback = startGameCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel titleLabel = new JLabel("Choose Your Pet", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // --- Center: Pet Selection & Naming ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Pet Type Selection (using Radio Buttons as placeholder)
        JPanel petTypePanel = new JPanel(new FlowLayout());
        petTypePanel.setBorder(BorderFactory.createTitledBorder("Select Pet Type"));
        // Use actual pet types (Req 3.1.4)
        petType1Radio = new JRadioButton("Dog");
        petType2Radio = new JRadioButton("Cat");
        petType3Radio = new JRadioButton("Robot");
        petTypeGroup = new ButtonGroup();
        petTypeGroup.add(petType1Radio);
        petTypeGroup.add(petType2Radio);
        petTypeGroup.add(petType3Radio);
        petTypePanel.add(petType1Radio);
        petTypePanel.add(petType2Radio);
        petTypePanel.add(petType3Radio);
        petType1Radio.setSelected(true); // Default selection
        // TODO: Add ActionListeners to radio buttons to update petInfoLabel
        centerPanel.add(petTypePanel, gbc);

        // Pet Image based on selection (Req 3.1.4 & 3.1.10)
        petImageLabel = new JLabel(); // Remove default text
        petImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // petImageLabel.setBorder(BorderFactory.createEtchedBorder()); // Keep or remove border?
        petImageLabel.setPreferredSize(new Dimension(150, 150)); // Keep size consistent
        centerPanel.add(petImageLabel, gbc); // Add image label

        // Placeholder for Pet Info/Description based on selection (Req 3.1.4)
        petInfoLabel = new JLabel("Info about selected pet type...");
        petInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(petInfoLabel, gbc);

        // Pet Name Input
        JPanel namePanel = new JPanel(new FlowLayout());
        namePanel.add(new JLabel("Pet Name:"));
        petNameField = new JTextField(20);
        namePanel.add(petNameField);
        centerPanel.add(namePanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        startGameButton = new JButton("Start Game");
        backButton = new JButton("Back to Main Menu");
        startGameButton.addActionListener(this);
        backButton.addActionListener(this);
        buttonPanel.add(backButton);
        buttonPanel.add(startGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // TODO: Add ActionListeners to radio buttons to update info label
        ActionListener updateInfoListener = e -> updatePetInfo();
        petType1Radio.addActionListener(updateInfoListener);
        petType2Radio.addActionListener(updateInfoListener);
        petType3Radio.addActionListener(updateInfoListener);
        updatePetInfo(); // Initial update
    }

    /** Loads an ImageIcon for the specified pet type (normal state) */
    private ImageIcon loadPetImage(String petType) {
        if (petType == null) return null;
        // Construct path assuming images are in src/main/resources/images/<PetType>/normal.png
        String imagePath = "/images/" + petType + "/normal.png";
        URL imageURL = getClass().getResource(imagePath);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            System.err.println("Warning: Could not load image for " + petType + " at path: " + imagePath);
            return null; // Or return a default placeholder ImageIcon
        }
    }

    /** Updates the placeholder info and image labels based on radio selection */
    private void updatePetInfo() {
        String info = "Select a pet type.";
        // String imageText = "[Image Placeholder]"; // Replaced by ImageIcon
        String selectedType = getSelectedPetType();
        ImageIcon petIcon = null;

        if (selectedType != null) {
            petIcon = loadPetImage(selectedType);
             // Add actual descriptions based on Requirement 3.1.4 and Pet class
            switch (selectedType) {
                case "Dog":
                    info = "<html>Info: Dogs are loyal companions! They tend to get hungry a bit faster.</html>";
                    // imageText = "[Dog Image]";
                    break;
                case "Cat":
                    info = "<html>Info: Cats are independent creatures who value their sleep and happiness.</html>";
                    // imageText = "[Cat Image]";
                    break;
                case "Robot":
                    info = "<html>Info: Robots are durable and don't need food or sleep, but can be harder to keep happy.</html>";
                    // imageText = "[Robot Image]";
                    break;
            }
        }
        petInfoLabel.setText(info);
        // petImageLabel.setText(imageText); // Update image placeholder text

        // Update image label
        if (petIcon != null) {
            petImageLabel.setIcon(petIcon);
            petImageLabel.setText(null); // Remove text if icon is loaded
        } else {
            petImageLabel.setIcon(null);
            petImageLabel.setText("[" + (selectedType != null ? selectedType : "Image") + " not found]"); // Show error text
            petImageLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Reset font for text
        }
    }

    /** Helper to get the string representation of the selected pet type */
    private String getSelectedPetType() {
        if (petType1Radio.isSelected()) return "Dog";
        if (petType2Radio.isSelected()) return "Cat";
        if (petType3Radio.isSelected()) return "Robot";
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == startGameButton) {
            String petName = petNameField.getText().trim();
            String selectedType = getSelectedPetType();

            if (selectedType == null) {
                JOptionPane.showMessageDialog(this, "Please select a pet type.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (petName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name for your pet.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (petName.length() > 20) { // Example validation
                 JOptionPane.showMessageDialog(this, "Pet name is too long (max 20 characters).", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            System.out.println("Creating pet: Name=" + petName + ", Type=" + selectedType);
            // Create the actual Pet object using the correct type string
            Pet newPet = new Pet(petName, selectedType);
            // Note: Pet constructor now handles setting type-specific max values (Req 3.1.4)

            // Call the callback provided by Main to start the game
            if (startGameCallback != null) {
                startGameCallback.accept(newPet);
            }

        } else if (source == backButton) {
            if (navigateCallback != null) {
                navigateCallback.accept(Main.MAIN_MENU_CARD);
            }
        }
    }

     // TODO: Add a method like resetFields() if needed, called from Main.switchPanel
     public void resetFields() {
         petNameField.setText("");
         petType1Radio.setSelected(true); // Default to Dog
         updatePetInfo();
     }
} 