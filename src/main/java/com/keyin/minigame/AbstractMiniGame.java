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
        gamePanel = new JPanel(new BorderLayout());

        artPanel = new GameArtPanel();
        gamePanel.add(artPanel, BorderLayout.CENTER);

        dialogBox = new DialogBox();
        gamePanel.add(dialogBox, BorderLayout.SOUTH);

        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        customizeUI();
    }

    protected abstract void customizeUI();
    protected abstract void handleKeyPress(KeyEvent e);
    protected abstract String getGameSpecificAsciiArt();

    public void startGame() {
        artPanel.setArt(getGameSpecificAsciiArt());

        dialogBox.showText(getIntroText());

        gamePanel.requestFocusInWindow();
    }

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

    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    public void setOnFailCallback(Runnable callback) {
        this.onFailCallback = callback;
    }

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

    public JPanel getGamePanel() {
        return gamePanel;
    }
    public Long getLocationId() {
        return locationId;
    }

    public Long getHeroId() {
        return heroId;
    }

    public DialogBox getDialogBox() {
        return dialogBox;
    }

    public GameArtPanel getArtPanel() {
        return artPanel;
    }
}