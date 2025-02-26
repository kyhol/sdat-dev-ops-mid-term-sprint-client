package com.keyin.minigame;

import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Factory class to create appropriate mini-game based on location ID
 */
public class MiniGameFactory {

    /**
     * Creates and returns the appropriate mini-game based on location ID
     */
    public static AbstractMiniGame createMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        switch(locationId.intValue()) {
            case 2:
                return new MysticForestMiniGame(locationId, heroId, parentFrame); // Brian's game
            case 3:
                return new CrystalCaveMiniGame(locationId, heroId, parentFrame); // Michael's game
            case 4:
                return new AncientTempleMiniGame(locationId, heroId, parentFrame); // Kyle's game
            case 5:
                return new DragonsPeakMiniGame(locationId, heroId, parentFrame); // Adam's game
            case 6:
                return new ShadowValleyMiniGame(locationId, heroId, parentFrame); // Brad's game
            default:
                JOptionPane.showMessageDialog(parentFrame,
                        "No specific mini-game found for location ID: " + locationId,
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return new GenericMiniGame(locationId, heroId, parentFrame);
        }
    }

    /**
     * A generic mini-game to use as fallback
     */
    private static class GenericMiniGame extends AbstractMiniGame {
        private boolean gameCompleted = false;

        public GenericMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
            super(locationId, heroId, parentFrame);
        }

        @Override
        protected void customizeUI() {
        }

        @Override
        protected void handleKeyPress(KeyEvent e) {
            if (!dialogBox.isTyping() && !gameCompleted) {
                gameCompleted = true;
                dialogBox.showText("You completed the generic challenge! Congratulations!", () -> {
                    completeGame();
                });
            }
        }

        @Override
        protected String getGameSpecificAsciiArt() {
            return
                    "    /\\     \n" +
                            "   /  \\    \n" +
                            "  /    \\   \n" +
                            " /      \\  \n" +
                            "/________\\ \n" +
                            "   |  |    \n" +
                            "   |__|    ";
        }

        @Override
        protected String getIntroText() {
            return "You're at " + getLocationName() + ".\n" +
                    "This is a generic challenge.\n" +
                    "Press any key to complete it...";
        }
    }
}