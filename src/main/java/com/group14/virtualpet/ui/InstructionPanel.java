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
        return "🐾 Welcome to Virtual Pet Adventure!\n\n" +
               "🎯 Goal:\n" +
               "Keep your virtual pet happy, healthy, and thriving!\n\n" +
    
               "📊 Pet Stats:\n" +
               "• ❤️ Health – If it reaches 0, it's game over!\n" +
               "• 💤 Sleep – Goes down over time. Let your pet rest to recover.\n" +
               "• 🍗 Fullness – Your pet gets hungry! Feed it with food items.\n" +
               "• 😊 Happiness – Pets need love! Boost it with gifts and playtime.\n\n" +
    
               "⚠️ Stat Warnings:\n" +
               "Stats turn red when they drop below 25%. Keep an eye on them!\n\n" +
    
               "😺 Pet States:\n" +
               "• ✅ Normal – Pet is doing well.\n" +
               "• 😴 Sleeping – Pet is asleep. Commands are disabled.\n" +
               "• 😫 Hungry – Fullness is 0. Health drops, and your pet becomes upset.\n" +
               "• 😠 Angry – Happiness is 0. Most commands are ignored.\n" +
               "• ☠️ Dead – Pet has died. You'll need to start a new game or load a save.\n\n" +
    
               "🕹️ Commands:\n" +
               "• 🍖 Feed – Increases Fullness using food items.\n" +
               "• 🛏️ Go to Bed – Recovers Sleep over time.\n" +
               "• 🎁 Give Gift – Increases Happiness using gift items.\n" +
               "• 🏥 Take to Vet – Restores Health. Has a cooldown.\n" +
               "• 🎮 Play – Boosts Happiness. Has a cooldown.\n" +
               "• 🏃 Exercise – Increases Health but uses up Sleep and Fullness.\n" +
               "Commands may be disabled based on your pet’s state.\n\n" +
    
               "📦 Inventory:\n" +
               "You’ll receive random items over time (Food & Gifts).\n" +
               "Use these to interact with your pet effectively.\n\n" +
    
               "💾 Saving:\n" +
               "Use the \"Save Game\" button to save your progress.\n" +
               "Your pet's name is used for the save file.\n\n" +
    
               "🤖 Pet Types:\n" +
               "• RoboFriend – Easiest, great for beginners.\n" +
               "• MechaMate – Medium difficulty, balanced needs.\n" +
               "• Tech Titan – Advanced challenge for experienced players.\n\n" +
    
               "🌟 Tip:\n" +
               "Watch your pet closely and respond to its needs to keep it happy!\n\n" +
               "Have fun caring for your digital companion!";
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