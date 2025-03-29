package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.group14.virtualpet.Main; // For card name constants

/**
 * Panel displaying game instructions.
 * Requirement: 3.1.3
 */
public class InstructionPanel extends JPanel implements ActionListener {

    private Consumer<String> navigateCallback;
    private JButton backButton;

    public InstructionPanel(Consumer<String> navigateCallback) {
        this.navigateCallback = navigateCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel titleLabel = new JLabel("How to Play", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // --- Instructions Text ---
        JTextArea instructionText = new JTextArea();
        instructionText.setEditable(false);
        instructionText.setLineWrap(true);
        instructionText.setWrapStyleWord(true);
        instructionText.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionText.setText(getInstructionsContent()); // Populate with instructions

        JScrollPane scrollPane = new JScrollPane(instructionText);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button ---
        JPanel buttonPanel = new JPanel(); // Defaults to FlowLayout center
        backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(this);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Provides the text content for the instructions panel. */
    private String getInstructionsContent() {
    return "=== Welcome to Virtual Pet Adventure! ===\n\n" +
           "GOAL:\n" +
           "Keep your virtual pet happy, healthy, and thriving!\n\n" +

           "------------------\n" +
           "PET STATS:\n" +
           "- Health    : Reaches 0 = Game Over.\n" +
           "- Sleep     : Goes down over time. Let your pet rest.\n" +
           "- Fullness  : Goes down over time. Feed your pet.\n" +
           "- Happiness : Boost with gifts or playtime.\n\n" +

           ">> Stats turn RED below 25%. Keep an eye on them!\n\n" +

           "------------------\n" +
           "PET STATES:\n" +
           "- Normal   : Your pet is fine.\n" +
           "- Sleeping : Recovering sleep. Commands disabled.\n" +
           "- Hungry   : Fullness is 0. Health drops and mood worsens.\n" +
           "- Angry    : Happiness is 0. Ignores most commands.\n" +
           "- Dead     : Game Over. Load a save or start a new game.\n\n" +

           "------------------\n" +
           "COMMANDS:\n" +
           "- Feed       : Increases Fullness (needs food).\n" +
           "- Go to Bed  : Recovers Sleep.\n" +
           "- Give Gift  : Boosts Happiness (needs gift).\n" +
           "- Take to Vet: Increases Health (cooldown).\n" +
           "- Play       : Boosts Happiness (cooldown).\n" +
           "- Exercise   : Boosts Health, lowers Sleep & Fullness.\n\n" +
           "* Some commands are disabled depending on pet state.\n\n" +

           "------------------\n" +
           "INVENTORY:\n" +
           "Items (Food & Gifts) appear randomly over time.\n" +
           "Use them to care for your pet.\n\n" +

           "------------------\n" +
           "SAVING:\n" +
           "Click 'Save Game' to save progress.\n" +
           "Your pet's name will be used for the save file.\n\n" +

           "------------------\n" +
           "PET TYPES:\n" +
           "- RoboFriend : Easy. Great for beginners.\n" +
           "- MechaMate  : Medium difficulty.\n" +
           "- Tech Titan : Hard. Best for experienced players.\n\n" +

           "------------------\n" +
           "TIP:\n" +
           "Watch your pet and use commands to keep it happy!\n\n" +
           "=== Have fun caring for your digital companion! ===";
}

    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            if (navigateCallback != null) {
                navigateCallback.accept(Main.MAIN_MENU_CARD);
            }
        }
    }
} 