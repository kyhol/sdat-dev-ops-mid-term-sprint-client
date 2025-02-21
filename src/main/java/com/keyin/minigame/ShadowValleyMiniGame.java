package com.keyin.minigame;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Placeholder for Shadow Valley Mini-Game (Location ID 6) - Brad
 */
public class ShadowValleyMiniGame extends AbstractMiniGame {

    public ShadowValleyMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
    }

    @Override
    protected void customizeUI() {
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // TODO: Brad - Implement your game's key handling logic

        // Temporary implementation for testing
        if (!dialogBox.isTyping()) {
            dialogBox.showText("Shadow Valley mini-game completed! (This is a placeholder)", () -> {
                completeGame();
            });
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        // TODO: Brad - Replace with your own ASCII art
        return
                "     .-.         \n" +
                        "    (   )        \n" +
                        "     `-'         \n" +
                        "     J L         \n" +
                        "     | |         \n" +
                        "    J   L        \n" +
                        "    |   |        \n" +
                        "    |   |        \n" +
                        "    |   |        \n" +
                        "    |   |        \n" +
                        "    J   L        \n" +
                        "    | | |        \n" +
                        "    | | |        \n" +
                        "    | | |        \n" +
                        "   (| | |)       \n";
    }

    @Override
    protected String getIntroText() {
        // TODO: Brad - Create your own introduction text
        return "Darkness envelops you as you enter Shadow Valley...\n" +
                "Shapes shift and move at the edge of your vision.\n" +
                "This is a placeholder mini-game.\n" +
                "Press any key to complete it...";
    }
}
