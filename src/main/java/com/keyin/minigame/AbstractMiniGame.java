package com.keyin.minigame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import com.keyin.ui.DialogBox;
import com.keyin.ui.GameArtPanel;

/**
 * Abstract base class that all mini-games will extend
 */
public abstract class AbstractMiniGame {
    protected Long locationId;
    protected Long heroId;
    protected JPanel gamePanel;
    protected DialogBox dialogBox;
    protected GameArtPanel artPanel;
    protected JFrame parentFrame;
    protected Runnable onCompleteCallback;
    protected Runnable onFailCallback;

    public AbstractMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        this.locationId = locationId;
        this.heroId = heroId;
        this.parentFrame = parentFrame;
        initializeUI();
    }

    private void initializeUI() {
        // Create the main panel with BorderLayout
        gamePanel = new JPanel(new BorderLayout());

        // Create and add the art panel
        artPanel = new GameArtPanel();
        gamePanel.add(artPanel, BorderLayout.CENTER);

        // Create and add the dialog box
        dialogBox = new DialogBox();
        gamePanel.add(dialogBox, BorderLayout.SOUTH);

        // Add key listener to handle user input
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Let the specific mini-game customize its UI
        customizeUI();
    }

    // Methods that specific mini-games must implement
    protected abstract void customizeUI();
    protected abstract void handleKeyPress(KeyEvent e);
    protected abstract String getGameSpecificAsciiArt();

    // Method to start the game
    public void startGame() {
        // Set the ASCII art specific to this game
        artPanel.setArt(getGameSpecificAsciiArt());

        // Show the introduction text
        dialogBox.showText(getIntroText());

        // Make sure the panel has focus to receive key events
        gamePanel.requestFocusInWindow();
    }

    // Methods that can be overridden by specific mini-games if needed
    protected String getIntroText() {
        return "Welcome to the mini-game at " + getLocationName() + "!\nPress any key to continue...";
    }

    protected String getLocationName() {
        switch(locationId.intValue()) {
            case 2: return "Mystic Forest";
            case 3: return "Crystal Cave";
            case 4: return "Ancient Temple";
            case 5: return "Dragon's Peak";
            case 6: return "Shadow Valley";
            default: return "Unknown Location";
        }
    }

    // Methods to set callbacks
    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    public void setOnFailCallback(Runnable callback) {
        this.onFailCallback = callback;
    }

    // Helper methods that mini-games can use
    protected void completeGame() {
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
    }

    protected void failGame() {
        if (onFailCallback != null) {
            onFailCallback.run();
        }
    }

    // Return the panel to be added to the card layout
    public JPanel getGamePanel() {
        return gamePanel;
    }
}