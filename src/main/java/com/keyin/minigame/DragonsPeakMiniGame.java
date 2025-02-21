package com.keyin.minigame;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class DragonsPeakMiniGame extends AbstractMiniGame {

    public DragonsPeakMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
        // TODO: Mike - Add your custom UI components here
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // TODO: Mike - Implement your game's key handling logic

        // Temporary implementation for testing
        if (!dialogBox.isTyping()) {
            dialogBox.showText("Dragon's Peak mini-game completed! (This is a placeholder)", () -> {
                completeGame();
            });
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        // TODO: Mike - Replace with your own ASCII art
        return
                "        /\\        \n" +
                        "       /  \\       \n" +
                        "      /    \\      \n" +
                        "     /      \\     \n" +
                        "    /        \\    \n" +
                        "   /          \\   \n" +
                        "  /            \\  \n" +
                        " /              \\ \n" +
                        "/________________\\\n" +
                        "       (())       \n" +
                        "      ((  ))      \n" +
                        "     ((    ))     \n";
    }

    @Override
    protected String getIntroText() {
        // TODO: Mike - Create your own introduction text
        return "The howling winds of Dragon's Peak surround you...\n" +
                "From this height, you can see across the entire realm.\n" +
                "This is a placeholder mini-game.\n" +
                "Press any key to complete it...";
    }
}
