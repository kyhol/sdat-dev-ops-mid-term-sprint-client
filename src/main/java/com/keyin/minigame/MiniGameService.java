package com.keyin.minigame;

import com.keyin.ui.GameInterfaceGUI;


public class MiniGameService {

    public void startMiniGame(Long locationId, Long heroId, GameInterfaceGUI gui, Runnable onComplete) {
        AbstractMiniGame miniGame = MiniGameFactory.createMiniGame(locationId, heroId, gui);

        miniGame.setOnCompleteCallback(onComplete);

        miniGame.setOnFailCallback(() -> {
            gui.showMessage("You failed the challenge! Try again.", "Game Over");
            gui.showPanel("locationSelection");
        });

        miniGame.startGame();
    }
}