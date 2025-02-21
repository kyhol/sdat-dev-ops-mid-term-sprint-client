package com.keyin.minigame;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Placeholder for Crystal Cave Mini-Game (Location ID 3) - Michael
 */
public class CrystalCaveMiniGame extends AbstractMiniGame {

    public CrystalCaveMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // TODO: Brian - Add your custom UI components here
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // TODO: Brian - Implement your game's key handling logic

        // Temporary implementation for testing
        if (!dialogBox.isTyping()) {
            dialogBox.showText("Crystal Cave mini-game completed! (This is a placeholder)", () -> {
                completeGame();
            });
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        // TODO: Brian - Replace with your own ASCII art
        return
                "    /\\      /\\    \n" +
                        "   /  \\    /  \\   \n" +
                        "  /    \\  /    \\  \n" +
                        " /      \\/      \\ \n" +
                        "/                \\\n" +
                        "\\                /\n" +
                        " \\              / \n" +
                        "  \\            /  \n" +
                        "   \\          /   \n" +
                        "    \\________/    ";
    }

    @Override
    protected String getIntroText() {
        // TODO: Brian - Create your own introduction text
        return "Welcome to the Crystal Cave...\n" +
                "The crystalline structures shimmer with an otherworldly light.\n" +
                "This is a placeholder mini-game.\n" +
                "Press any key to complete it...";
    }
}


