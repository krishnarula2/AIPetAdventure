package com.group14.virtualpet.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer; // Added for formatting time

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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.group14.virtualpet.Main; // For card name constants
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
    private JSpinner timeLimitSpinner; // In minutes
    private JButton applyTimeLimitButton;
    // Statistics Components (Req 3.1.11.2)
    private JLabel totalPlaytimeLabel;
    private JButton resetStatsButton;

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

        // --- Create Content Panel (Initially empty placeholders) ---
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

        JLabel promptLabel = new JLabel("Enter Parental Controls Password:", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(promptLabel, BorderLayout.NORTH);

        passwordField = new JPasswordField(20);
        panel.add(passwordField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(); // Flow layout
        submitPasswordButton = new JButton("Submit");
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
        reviveButton.addActionListener(this);
        revivePanel.add(reviveButton);
        controlsArea.add(revivePanel);

        // --- Time Limits Section (Req 3.1.11.1) ---
        JPanel timeLimitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeLimitsPanel.setBorder(BorderFactory.createTitledBorder("Time Limits"));
        enableTimeLimitCheckbox = new JCheckBox("Enable Max Playtime per Session");
        timeLimitsPanel.add(enableTimeLimitCheckbox);
        timeLimitsPanel.add(new JLabel("Limit (minutes):"));
        // Spinner for minutes (1 to 180, default 30)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(30, 1, 180, 1);
        timeLimitSpinner = new JSpinner(spinnerModel);
        timeLimitsPanel.add(timeLimitSpinner);
        applyTimeLimitButton = new JButton("Apply Time Limit");
        applyTimeLimitButton.addActionListener(this);
        timeLimitsPanel.add(applyTimeLimitButton);
        controlsArea.add(timeLimitsPanel);

        // --- Statistics Section (Req 3.1.11.2) ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Usage Statistics"));
        totalPlaytimeLabel = new JLabel("Total Playtime: (Select Save)");
        statsPanel.add(totalPlaytimeLabel);
        resetStatsButton = new JButton("Reset Playtime Stats");
        resetStatsButton.addActionListener(this);
        statsPanel.add(resetStatsButton);
        // TODO: Add more stats like average session time, last played etc. if desired
        controlsArea.add(statsPanel);

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
            applyTimeLimitButton.setEnabled(false); // Disable apply buttons if no saves
            resetStatsButton.setEnabled(false);
            updateStatsDisplay(null); // Clear stats display
        } else {
            saveFiles.forEach(saveFileDropdown::addItem);
            saveFileDropdown.setEnabled(true);
            reviveButton.setEnabled(true);
            applyTimeLimitButton.setEnabled(true);
            resetStatsButton.setEnabled(true);
            // Add listener to update UI when selection changes
            saveFileDropdown.addActionListener(this); // Trigger actionPerformed on selection
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
                populateSaveFiles(); // Populate dropdown when showing content
                cardLayout.show(internalPanel, CONTENT_CARD);
            } else {
                // Incorrect password
                JOptionPane.showMessageDialog(this, "Incorrect password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // Clear field
            }
        } else if (source == backFromPasswordButton || source == backFromContentButton) {
            // Navigate back to main menu
            if (navigateCallback != null) {
                navigateCallback.accept(Main.MAIN_MENU_CARD);
            }
        } else if (source == reviveButton) {
            handleRevivePet(); // Handle revive action
        } else if (source == applyTimeLimitButton) {
            handleApplyTimeLimit(); // Handle apply time limit action
        } else if (source == resetStatsButton) {
            handleResetStats(); // Handle reset statistics
        } else if (source == saveFileDropdown) {
            // Update Time Limit controls and Stats display when selection changes
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
            System.out.println("Attempting to revive pet in: " + selectedSave);
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
            if (loadedState != null && loadedState.getPet() != null) {
                Pet petToRevive = loadedState.getPet();
                petToRevive.revive(); // Call the revive method on the pet
                System.out.println("Pet " + petToRevive.getName() + " revived.");

                // Re-save the modified state
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
            JOptionPane.showMessageDialog(this, "Please select a save file to apply the limit to.", "Time Limit Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedSave = selectedItem.toString();
        boolean enabled = enableTimeLimitCheckbox.isSelected();
        int limitMinutes = (int) timeLimitSpinner.getValue();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apply time limit settings to '" + selectedSave + "'?\n" +
                "Enabled: " + enabled + (enabled ? "\\nLimit: " + limitMinutes + " minutes" : ""),
                "Confirm Time Limit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Applying time limit to: " + selectedSave + ", Enabled: " + enabled + ", Limit: " + limitMinutes + " min");

            // Load the game state
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
            if (loadedState != null) {
                // Modify the game state using the new setters
                loadedState.setTimeLimitEnabled(enabled);
                loadedState.setMaxPlaytimeMinutes(limitMinutes);
                // We don't need to reset session time here; it's handled on game start/load.
                System.out.println("GameState time limit settings updated.");

                // Re-save the modified state
                boolean saveSuccess = SaveLoadUtil.saveGame(loadedState, selectedSave);
                if (saveSuccess) {
                    JOptionPane.showMessageDialog(this, "Time limit settings applied successfully to '" + selectedSave + "'!", "Time Limit Success", JOptionPane.INFORMATION_MESSAGE);
                    // Optionally, could update the UI if we loaded the state's settings into the controls
                    // when the dropdown selection changes.
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save the time limit settings.", "Time Limit Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load '" + selectedSave + "' to apply time limit.", "Time Limit Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Updates the Time Limit controls and Stats Display based on the selected save file */
    private void updateUIForSelectedSave() {
        Object selectedItem = saveFileDropdown.getSelectedItem();
        if (selectedItem == null || "No saves found".equals(selectedItem.toString())) {
            // Disable controls if no valid save selected
            enableTimeLimitCheckbox.setSelected(false);
            timeLimitSpinner.setValue(30); // Reset to default
            updateStatsDisplay(null);
            return;
        }
        String selectedSave = selectedItem.toString();
        GameState loadedState = SaveLoadUtil.loadGame(selectedSave);

        if (loadedState != null) {
            // Update Time Limit controls
            enableTimeLimitCheckbox.setSelected(loadedState.isTimeLimitEnabled());
            timeLimitSpinner.setValue(loadedState.getMaxPlaytimeMinutes());

            // Update Statistics display
            updateStatsDisplay(loadedState);
        } else {
            // Handle case where save file couldn't be loaded (show error? clear UI?)
            System.err.println("Could not load save '+selectedSave+' to update UI.");
            enableTimeLimitCheckbox.setSelected(false);
            timeLimitSpinner.setValue(30);
            updateStatsDisplay(null);
        }
    }

    /** Updates the statistics label based on the loaded GameState */
    private void updateStatsDisplay(GameState state) {
        if (state != null) {
            long totalMillis = state.getTotalPlaytimeMillis();
            String formattedTime = formatDuration(totalMillis);
            totalPlaytimeLabel.setText("Total Playtime: " + formattedTime);
        } else {
            totalPlaytimeLabel.setText("Total Playtime: (Select Save)");
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
                "Are you sure you want to reset ALL playtime statistics for '" + selectedSave + "'?\n" +
                "This cannot be undone.",
                "Confirm Reset Statistics",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            GameState loadedState = SaveLoadUtil.loadGame(selectedSave);
            if (loadedState != null) {
                loadedState.resetPlaytimeStats(); // Call the reset method

                boolean saveSuccess = SaveLoadUtil.saveGame(loadedState, selectedSave);
                if (saveSuccess) {
                    JOptionPane.showMessageDialog(this, "Playtime statistics reset successfully for '" + selectedSave + "'!", "Reset Success", JOptionPane.INFORMATION_MESSAGE);
                    updateStatsDisplay(loadedState); // Update the display
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save the reset statistics.", "Reset Stats Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load '" + selectedSave + "' to reset statistics.", "Reset Stats Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 