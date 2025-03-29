package com.group14.virtualpet.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * Thread for playing MP3 audio in the background
 */
class MP3PlayerThread extends Thread {
    private Player player;
    private InputStream inputStream;
    private final boolean loop;
    private boolean stop;
    private final AudioManager audioManager;

    public MP3PlayerThread(InputStream inputStream, boolean loop, AudioManager audioManager) {
        this.inputStream = inputStream;
        this.loop = loop;
        this.stop = false;
        this.audioManager = audioManager;
    }

    @Override
    public void run() {
        try {
            do {
                // Reset the input stream for looping
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                player = new Player(bufferedInputStream);

                // Play until complete or stopped
                while (!stop && !player.isComplete()) {
                    player.play(1);
                }

                // If stop was requested, break the loop
                if (stop) {
                    break;
                }

                // Reset the input stream for the next loop iteration
                inputStream = audioManager.getAudioStream();
            } while (loop && inputStream != null);

        } catch (JavaLayerException e) {
            System.err.println("Error playing MP3: " + e.getMessage());
        } finally {
            if (player != null) {
                player.close();
            }
        }
    }

    public void stopPlaying() {
        this.stop = true;
        if (player != null) {
            player.close();
        }
    }

    public boolean isPlaying() {
        return player != null && !stop;
    }
}

/**
 * Manages audio playback for the Virtual Pet game.
 * Handles background music and sound effects.
 */
public class AudioManager {
    private static AudioManager instance;
    private MP3PlayerThread playerThread;
    private boolean musicEnabled = true;

    private static final String PREFS_NODE = "com.group14.virtualpet";
    private static final String PREF_MUSIC_ENABLED = "musicEnabled";

    private AudioManager() {
        // Load saved preferences
        loadPreferences();

        // Initialize background music
        initBackgroundMusic();
    }

    /**
     * Gets the singleton instance of AudioManager.
     * @return The AudioManager instance
     */
    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Initializes the background music.
     */
    private void initBackgroundMusic() {
        // Start playing if music is enabled
        if (musicEnabled) {
            startBackgroundMusic();
        }
    }

    /**
     * Gets a fresh audio input stream for the music file.
     * This is used when looping the audio.
     * 
     * @return A new input stream for the audio file
     */
    public InputStream getAudioStream() {
        InputStream audioSrc = getClass().getClassLoader().getResourceAsStream("sounds/2212gameAudio.mp3");
        if (audioSrc == null) {
            System.err.println("Could not find background music file. Make sure 2212gameAudio.mp3 exists in the sounds folder.");
            return null;
        }
        return audioSrc;
    }

    /**
     * Starts playing the background music.
     */
    public void startBackgroundMusic() {
        if (!musicEnabled) {
            return;
        }

        // Stop any existing playback
        stopBackgroundMusic();

        try {
            // Get a fresh audio stream
            InputStream audioStream = getAudioStream();
            if (audioStream == null) {
                return;
            }

            // Create and start a new player thread
            playerThread = new MP3PlayerThread(audioStream, true, this);
            playerThread.start();

            System.out.println("Background music started");
        } catch (Exception e) {
            System.err.println("Error starting background music: " + e.getMessage());
        }
    }

    /**
     * Stops the background music.
     */
    public void stopBackgroundMusic() {
        if (playerThread != null) {
            playerThread.stopPlaying();
            playerThread = null;
            System.out.println("Background music stopped");
        }
    }

    /**
     * Sets whether background music is enabled.
     * @param enabled True to enable music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;

        if (enabled) {
            startBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }

        // Save the preference
        savePreferences();
    }

    /**
     * Checks if background music is enabled.
     * @return True if music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Loads user preferences from storage.
     */
    private void loadPreferences() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        musicEnabled = prefs.getBoolean(PREF_MUSIC_ENABLED, true);
    }

    /**
     * Saves user preferences to storage.
     */
    private void savePreferences() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        prefs.putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            System.err.println("Error saving preferences: " + e.getMessage());
        }
    }

    /**
     * Cleans up resources when the application is closing.
     */
    public void cleanup() {
        stopBackgroundMusic();
    }
}
