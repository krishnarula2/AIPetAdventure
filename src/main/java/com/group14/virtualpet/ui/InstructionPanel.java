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
        // TODO: Refine instructions text to be comprehensive and age-appropriate (Req 3.1.3)
        return "Welcome to Virtual Pet!\n\n" +
                "Goal: Keep your pet happy and healthy!\n\n" +
                "Each pet has four vital statistics:\n" +
                "• Health: Decreases when your pet is in a negative state\n" +
                "• Sleep: Decreases over time, replenish with rest\n" +
                "• Fullness: Decreases over time, replenish by feeding\n" +
                "• Happiness: Decreases over time, replenish with gifts\n\n" +
                "Stats:\n" +
                "- Health: How healthy your pet is. If it reaches 0, the game is over!\n" +
                "- Sleep: How rested your pet is. If it reaches 0, your pet will fall asleep.\n" +
                "- Fullness: How hungry your pet is. If it reaches 0, your pet gets hungry and starts losing health.\n" +
                "- Happiness: How happy your pet is. If it reaches 0, your pet gets angry and won\'t listen to most commands.\n" +
                "Stats decrease over time. Keep them high using commands!\n\n" +
                "States:\n" +
                "- Normal: Your pet is fine.\n" +
                "- Sleeping: Pet is recovering sleep. Cannot issue commands.\n" +
                "- Hungry: Pet needs food! Happiness drops faster, health decreases.\n" +
                "- Angry: Pet needs cheering up! Only happy commands work.\n" +
                "- Dead: Game Over. You\'ll need to start a new game or load one.\n" +
                "Watch for warnings when stats get low (below 25%)!\n\n" +
                "Commands:\n" +
                "- Feed: Give your pet food from your inventory to increase Fullness.\n" +
                "- Go to Bed: Puts your pet to sleep to recover Sleep.\n" +
                "- Give Gift: Give a gift from your inventory to increase Happiness.\n" +
                "- Take to Vet: Increases Health. Has a cooldown.\n" +
                "- Play: Increases Happiness. Has a cooldown.\n" +
                "- Exercise: Decreases Sleep and Fullness slightly, but increases Health.\n" +
                "Some commands are unavailable depending on the pet\'s state (e.g., when sleeping or angry).\n\n" +
                "Inventory:\n" +
                "You need items (Food, Gifts) to use Feed and Give Gift commands. You\'ll randomly find new items over time.\n\n" +
                "Saving:\n" +
                "Use the \'Save Game\' button during gameplay to save your progress. Your pet\'s name is used for the save file.\n\n" +
                "Pet Types:\n" +
                "Different pet types (friendly_robot, balanced_robot, challenging_robot) have slightly different needs and stats.\n\n" +
                "Have fun taking care of your virtual pet!";
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