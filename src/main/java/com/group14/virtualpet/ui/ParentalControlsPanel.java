package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;

import com.group14.virtualpet.Main;
import com.group14.virtualpet.MainFrame;
import com.group14.virtualpet.model.Pet;
import com.group14.virtualpet.state.GameState;
import com.group14.virtualpet.util.SaveLoadUtil;

/**
 * Panel for parental controls (password protected).
 * Requirement: 3.1.11
 */
public class ParentalControlsPanel extends JPanel implements ActionListener {

    private static final String CORRECT_PASSWORD = "admin"; // Hardcoded password (Req 3.1.11)
    private static final String CONTENT_CARD = "Content";
    private static final String PASSWORD_PROMPT_CARD = "PasswordPrompt";

    private Consumer<String> navigateCallback;
    private CardLayout cardLayout;
    private JPanel internalPanel; // Panel using CardLayout for password/content

    // Password Prompt Components
    private JPasswordField passwordField;
    private JButton submitPasswordButton;
    private JButton backFromPasswordButton;

    // Content Components
    private JButton backFromContentButton;
    // Revive Pet Components (Req 3.1.11.3)
    private JComboBox<String> saveFileDropdown;
    private JButton reviveButton;
    // Time Limit Components (Req 3.1.11.1)
    private JCheckBox enableTimeLimitCheckbox;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JButton applyTimeLimitButton;
    // Statistics Components (Req 3.1.11.2)
    private JLabel totalPlaytimeLabel;
    private JLabel averageSessionLabel;
    private JButton resetStatsButton;
    // Play as Parent Section
    private JButton playAsParentButton;

    public ParentalControlsPanel(Consumer<String> navigateCallback) {
        this.navigateCallback = navigateCallback;

        // Main panel uses BorderLayout
        setLayout(new BorderLayout());

        // Internal panel uses CardLayout to switch between password prompt and content
        cardLayout = new CardLayout();
        internalPanel = new JPanel(cardLayout);

        // --- Create Password Prompt Panel ---
        JPanel passwordPanel = createPasswordPromptPanel();
        internalPanel.add(passwordPanel, PASSWORD_PROMPT_CARD);

        // --- Create Content Panel ---
        JPanel contentPanel = createContentPanel();
        internalPanel.add(contentPanel, CONTENT_CARD);

        // Add the internal panel to the main panel
        add(internalPanel, BorderLayout.CENTER);

        // Show password prompt initially
        cardLayout.show(internalPanel, PASSWORD_PROMPT_CARD);
    }

    /** Creates the panel for password entry */
    private JPanel createPasswordPromptPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Padding

        JLabel promptLabel = new JLabel("Enter Parent Password:", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(promptLabel, BorderLayout.NORTH);

        // Create a container panel for the password field to control its size
        JPanel passwordContainer = new JPanel();
        passwordField = new JPasswordField(20);
        // Set preferred size to make the field an appropriate width and height
        passwordField.setPreferredSize(new java.awt.Dimension(200, 30));
        passwordContainer.add(passwordField);
        panel.add(passwordContainer, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(); // Flow layout
        submitPasswordButton = new JButton("Verify");
        submitPasswordButton.setBackground(java.awt.Color.GREEN);
        backFromPasswordButton = new JButton("Back to Main Menu");
        submitPasswordButton.addActionListener(this);
        backFromPasswordButton.addActionListener(this);
        buttonPanel.add(backFromPasswordButton);
        buttonPanel.add(submitPasswordButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /** Creates the panel displaying the actual controls */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Parental Controls", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // --- Controls Area Panel (using BoxLayout for vertical sections) ---
        JPanel controlsArea = new JPanel();
        controlsArea.setLayout(new BoxLayout(controlsArea, BoxLayout.Y_AXIS));

        // --- Revive Pet Section (Req 3.1.11.3) ---
        JPanel revivePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        revivePanel.setBorder(BorderFactory.createTitledBorder("Revive Pet"));
        revivePanel.add(new JLabel("Select Save File:"));
        saveFileDropdown = new JComboBox<>();
        // Dropdown will be populated when panel is shown
        revivePanel.add(saveFileDropdown);
        reviveButton = new JButton("Revive Selected Pet");
        reviveButton.setBackground(java.awt.Color.GREEN);
        reviveButton.setToolTipText("Revive the pet and reset its stats.");
        reviveButton.addActionListener(this);
        revivePanel.add(reviveButton);
        controlsArea.add(revivePanel);

        // --- Time Limitations Section (Req 3.1.11.1) ---
        JPanel timeLimitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeLimitsPanel.setBorder(BorderFactory.createTitledBorder("Time Limitations"));
        enableTimeLimitCheckbox = new JCheckBox("Enable Playtime Restrictions");
        timeLimitsPanel.add(enableTimeLimitCheckbox);

        // Start Time Spinner
        timeLimitsPanel.add(new JLabel("Start Time:"));
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startEditor);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            startTimeSpinner.setValue(sdf.parse("08:00"));
        } catch (Exception ex) { }
        timeLimitsPanel.add(startTimeSpinner);
        JLabel startInfoLabel = new JLabel("ℹ");
        startInfoLabel.setToolTipText("Set the allowed play start time (24-hour format).");
        timeLimitsPanel.add(startInfoLabel);

        // End Time Spinner
        timeLimitsPanel.add(new JLabel("End Time:"));
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endEditor);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            endTimeSpinner.setValue(sdf.parse("22:00"));
        } catch (Exception ex) { }
        timeLimitsPanel.add(endTimeSpinner);
        JLabel endInfoLabel = new JLabel("ℹ");
        endInfoLabel.setToolTipText("Set the allowed play end time (24-hour format).");
        timeLimitsPanel.add(endInfoLabel);

        // Apply Button
        applyTimeLimitButton = new JButton("Apply Time Limit");
        applyTimeLimitButton.setBackground(java.awt.Color.GREEN);
        applyTimeLimitButton.addActionListener(this);
        timeLimitsPanel.add(applyTimeLimitButton);
        controlsArea.add(timeLimitsPanel);

        // --- Play Time Statistics Section (Req 3.1.11.2) ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Play Time Statistics"));
        totalPlaytimeLabel = new JLabel("Total Playtime: (Select Save)");
        totalPlaytimeLabel.setForeground(java.awt.Color.GREEN);
        statsPanel.add(totalPlaytimeLabel);
        averageSessionLabel = new JLabel("Average Session: (Select Save)");
        averageSessionLabel.setForeground(java.awt.Color.GREEN);
        statsPanel.add(averageSessionLabel);
        resetStatsButton = new JButton("Reset Playtime Stats");
        resetStatsButton.setBackground(java.awt.Color.GREEN);
        resetStatsButton.addActionListener(this);
        statsPanel.add(resetStatsButton);
        controlsArea.add(statsPanel);

        // --- Play as Parent Section ---
        JPanel playAsParentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playAsParentButton = new JButton("Play as Parent");
        playAsParentButton.setBackground(java.awt.Color.BLUE);
        // Changed text color to black for better visibility
        playAsParentButton.setForeground(java.awt.Color.BLACK);
        playAsParentButton.setToolTipText("Start playing without restrictions.");
        playAsParentButton.addActionListener(this);
        playAsParentPanel.add(playAsParentButton);
        controlsArea.add(playAsParentPanel);

        panel.add(controlsArea, BorderLayout.CENTER);

        // --- Bottom Button Panel ---
        JPanel buttonPanel = new JPanel();
        backFromContentButton = new JButton("Back to Main Menu");
        backFromContentButton.addActionListener(this);
        buttonPanel.add(backFromContentButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /** Call this method when navigating TO this panel to reset it */
    public void resetPanel() {
        passwordField.setText(""); // Clear password field
        cardLayout.show(internalPanel, PASSWORD_PROMPT_CARD); // Show password prompt
    }

    /** Populates the save file dropdown menu. Req 3.1.11.3 */
    private void populateSaveFiles() {
        saveFileDropdown.removeAllItems(); // Clear previous items
        List<String> saveFiles = SaveLoadUtil.listSaveFiles();
        if (saveFiles.isEmpty()) {
            saveFileDropdown.addItem("No saves found");
            saveFileDropdown.setEnabled(false);
            reviveButton.setEnabled(false);
            applyTimeLimitButton.setEnabled(false);
            resetStatsButton.setEnabled(false);
            updateStatsDisplay(null); // Clear stats display
        } else {
            saveFiles.forEach(saveFileDropdown::addItem);
            saveFileDropdown.setEnabled(true);
            reviveButton.setEnabled(true);
            applyTimeLimitButton.setEnabled(true);
            resetStatsButton.setEnabled(true);
            // Add listener to update UI when selection changes
            saveFileDropdown.addActionListener(this);
            updateUIForSelectedSave(); // Initial update for the default selection
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == submitPasswordButton) {
            // Check password
            String enteredPassword = new String(passwordField.getPassword());
            if (CORRECT_PASSWORD.equals(enteredPassword)) {
                // Correct password, show content and populate saves
                populateSaveFiles();
                cardLayout.show(internalPanel, CONTENT_CARD);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } else if (source == backFromPasswordButton || source == backFromContentButton) {
            if (navigateCallback != null) {
                navigateCallback.accept(Main.MAIN_MENU_CARD);
            }
        } else if (source == reviveButton) {
            handleRevivePet();
        } else if (source == applyTimeLimitButton) {
            handleApplyTimeLimit();
        } else if (source == resetStatsButton) {
            handleResetStats();
        } else if (source == playAsParentButton) {
            handlePlayAsParent();
        } else if (source == saveFileDropdown) {
            updateUIForSelectedSave();
        }
    }

    /** Handles the Revive Pet button action. Req 3.1.11.3 */
    private void handleRevivePet() {
        Object selectedItem = saveFileDropdown.getSelectedItem();
        if (selectedItem == null || !saveFileDropdown.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Please select a valid save file.", "Revive Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedSave = selectedItem.toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to revive the pet in '" + selectedSave + "'?\n" +
                "This will reset its stats and save the changes.",
                "Confirm Revive",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
            if (loadedState != null && loadedState.getPet() != null) {
                Pet petToRevive = loadedState.getPet();
                petToRevive.revive();
                boolean saveSuccess = SaveLoadUtil.saveGame(loadedState, selectedSave);
                if (saveSuccess) {
                    JOptionPane.showMessageDialog(this, "Pet in '" + selectedSave + "' has been revived successfully!", "Revive Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save the revived pet state.", "Revive Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load or find pet data in '" + selectedSave + "'.", "Revive Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Handles the Apply Time Limit button action. Req 3.1.11.1 */
    private void handleApplyTimeLimit() {
        Object selectedItem = saveFileDropdown.getSelectedItem();
        if (selectedItem == null || "No saves found".equals(selectedItem.toString())) {
            JOptionPane.showMessageDialog(this, "Please select a save file to apply the time limit to.", "Time Limit Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedSave = selectedItem.toString();
        boolean enabled = enableTimeLimitCheckbox.isSelected();
        Date startTime = (Date) startTimeSpinner.getValue();
        Date endTime = (Date) endTimeSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String confirmMessage = "Apply time limit settings to '" + selectedSave + "'?\n" +
                "Enabled: " + enabled + "\n" +
                (enabled ? "Start Time: " + sdf.format(startTime) + "\nEnd Time: " + sdf.format(endTime) : "");
        int confirm = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Confirm Time Limit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
            if (loadedState != null) {
                loadedState.setTimeLimitEnabled(enabled);
                loadedState.setPlaytimeStart(startTime);
                loadedState.setPlaytimeEnd(endTime);
                boolean saveSuccess = SaveLoadUtil.saveGame(loadedState, selectedSave);
                if (saveSuccess) {
                    JOptionPane.showMessageDialog(this, "Time limit settings applied successfully to '" + selectedSave + "'!", "Time Limit Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save the time limit settings.", "Time Limit Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load '" + selectedSave + "' to apply time limit.", "Time Limit Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Updates the Time Limit controls and Statistics display based on the selected save file */
    private void updateUIForSelectedSave() {
        Object selectedItem = saveFileDropdown.getSelectedItem();
        if (selectedItem == null || "No saves found".equals(selectedItem.toString())) {
            enableTimeLimitCheckbox.setSelected(false);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                startTimeSpinner.setValue(sdf.parse("08:00"));
                endTimeSpinner.setValue(sdf.parse("22:00"));
            } catch (Exception ex) { }
            updateStatsDisplay(null);
            return;
        }
        String selectedSave = selectedItem.toString();
        GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
        if (loadedState != null) {
            enableTimeLimitCheckbox.setSelected(loadedState.isTimeLimitEnabled());
            if (loadedState.getPlaytimeStart() != null) {
                startTimeSpinner.setValue(loadedState.getPlaytimeStart());
            }
            if (loadedState.getPlaytimeEnd() != null) {
                endTimeSpinner.setValue(loadedState.getPlaytimeEnd());
            }
            updateStatsDisplay(loadedState);
        } else {
            System.err.println("Could not load save '" + selectedSave + "' to update UI.");
            enableTimeLimitCheckbox.setSelected(false);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                startTimeSpinner.setValue(sdf.parse("08:00"));
                endTimeSpinner.setValue(sdf.parse("22:00"));
            } catch (Exception ex) { }
            updateStatsDisplay(null);
        }
    }

    /** Updates the statistics labels based on the loaded GameState */
    private void updateStatsDisplay(GameState state) {
        if (state != null) {
            long totalMillis = state.getTotalPlaytimeMillis();
            String formattedTime = formatDuration(totalMillis);
            totalPlaytimeLabel.setText("Total Playtime: " + formattedTime);
            long avgMillis = state.getAverageSessionMillis(); // Assumes GameState provides this method
            String avgFormatted = formatDuration(avgMillis);
            averageSessionLabel.setText("Average Session: " + avgFormatted);
        } else {
            totalPlaytimeLabel.setText("Total Playtime: (Select Save)");
            averageSessionLabel.setText("Average Session: (Select Save)");
        }
    }

    /** Helper method to format milliseconds into H:M:S string */
    private String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }

    /** Handles the Reset Statistics button action. Req 3.1.11.2 */
    private void handleResetStats() {
        Object selectedItem = saveFileDropdown.getSelectedItem();
        if (selectedItem == null || "No saves found".equals(selectedItem.toString())) {
            JOptionPane.showMessageDialog(this, "Please select a save file to reset statistics for.", "Reset Stats Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedSave = selectedItem.toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset ALL playtime statistics for '" + selectedSave + "'?\nThis cannot be undone.",
                "Confirm Reset Statistics",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
            if (loadedState != null) {
                loadedState.resetPlaytimeStats();
                boolean saveSuccess = SaveLoadUtil.saveGame(loadedState, selectedSave);
                if (saveSuccess) {
                    JOptionPane.showMessageDialog(this, "Playtime statistics reset successfully for '" + selectedSave + "'!", "Reset Success", JOptionPane.INFORMATION_MESSAGE);
                    updateStatsDisplay(loadedState);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save the reset statistics.", "Reset Stats Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load '" + selectedSave + "' to reset statistics.", "Reset Stats Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Handles the Play as Parent button action. */
    private void handlePlayAsParent() {
        GameState gameState = null;
        Object selectedItem = saveFileDropdown.getSelectedItem();

        // 1. If a valid save is selected, load it and disable time limits.
        if (selectedItem != null && !"No saves found".equals(selectedItem.toString())) {
            gameState = SaveLoadUtil.loadGame(selectedItem.toString());
            if (gameState != null) {
                gameState.setTimeLimitEnabled(false);
                SaveLoadUtil.saveGame(gameState, selectedItem.toString());
            }
        }

        // 2. If no valid save loaded, create a new default GameState.
        if (gameState == null) {
            // Adjust this to match your actual Pet/Inventory constructors.
            gameState = new GameState(
                new com.group14.virtualpet.model.Pet("ParentPet", "DefaultType"),
                new com.group14.virtualpet.model.Inventory(),
                0
            );
            SaveLoadUtil.saveGame(gameState, "parent_default_save");
        }

        // 3. Show a confirmation message.
        JOptionPane.showMessageDialog(this,
            "Starting game as Parent (no restrictions applied).",
            "Play as Parent",
            JOptionPane.INFORMATION_MESSAGE
        );

        // 4. Pass this GameState to the gameplay panel and switch screens.
        //    Example if your MainFrame has a method like loadAndSwitchToGameplay(GameState).
        if (navigateCallback instanceof MainFrame) {
            ((MainFrame) navigateCallback).loadAndSwitchToGameplay(gameState);
        } else {
            // Fallback: just navigate to the gameplay card (but you still need to set the GameState).
            if (navigateCallback != null) {
                navigateCallback.accept(Main.GAMEPLAY_CARD);
            }
        }
    }
}
