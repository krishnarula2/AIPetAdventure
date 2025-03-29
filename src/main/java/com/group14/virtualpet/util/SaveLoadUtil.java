package com.group14.virtualpet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.group14.virtualpet.state.GameState;

/**
 * Utility class for saving and loading game state using Java Serialization.
 * Requirement: 3.1.5, 3.2.14
 */
public class SaveLoadUtil {

    private static final String SAVE_DIRECTORY = "saves";
    private static final String SAVE_EXTENSION = ".sav";

    /**
     * Saves the given GameState object to a file.
     * The file will be saved in the SAVE_DIRECTORY with the specified filename and SAVE_EXTENSION.
     *
     * @param state    The GameState object to save.
     * @param filename The base name for the save file (without extension).
     * @return true if saving was successful, false otherwise.
     */
    public static boolean saveGame(GameState state, String filename) {
        File saveDir = new File(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                System.err.println("Error: Could not create save directory: " + SAVE_DIRECTORY);
                return false;
            }
        }

        File saveFile = new File(saveDir, filename + SAVE_EXTENSION);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            oos.writeObject(state);
            System.out.println("Game saved successfully to: " + saveFile.getPath());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game state to " + saveFile.getPath() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads a GameState object from a file.
     *
     * @param filename The base name of the save file to load (without extension).
     * @return The loaded GameState object, or null if loading fails or the file doesn't exist.
     */
    public static GameState loadGame(String filename) {
        File saveFile = new File(SAVE_DIRECTORY, filename + SAVE_EXTENSION);

        if (!saveFile.exists()) {
            System.err.println("Error: Save file not found: " + saveFile.getPath());
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameState loadedState = (GameState) ois.readObject();
            System.out.println("Game loaded successfully from: " + saveFile.getPath());
            return loadedState;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Error loading game state from " + saveFile.getPath() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lists the names of available save files in the save directory.
     *
     * @return A List of save file names (without the .sav extension).
     */
    public static List<String> listSaveFiles() {
        File saveDir = new File(SAVE_DIRECTORY);
        List<String> saveFiles = new ArrayList<>();

        if (saveDir.exists() && saveDir.isDirectory()) {
            File[] files = saveDir.listFiles((dir, name) -> name.toLowerCase().endsWith(SAVE_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    // Remove extension
                    String name = file.getName();
                    int extensionIndex = name.toLowerCase().lastIndexOf(SAVE_EXTENSION);
                    saveFiles.add(name.substring(0, extensionIndex));
                }
            }
        }
        return saveFiles;
    }

    // Private constructor to prevent instantiation
    private SaveLoadUtil() {}
} 