package com.keyin.minigame;

import com.keyin.ui.GameInterfaceGUI;

/**
 
MiniGameService is responsible for creating and starting mini-games.
It uses MiniGameFactory to create the mini-game, then sets up success/failure callbacks
that call back into the GUI.*/
public class MiniGameService {

    public void startMiniGame(Long locationId, Long heroId, GameInterfaceGUI gui, Runnable onComplete) {
        // Create the mini-game from your factory
        AbstractMiniGame miniGame = MiniGameFactory.createMiniGame(locationId, heroId, gui);

        // On success, run onComplete
        miniGame.setOnCompleteCallback(onComplete);

        // On fail, show message and switch panels in the GUI
        miniGame.setOnFailCallback(() -> {
            gui.showMessage("You failed the challenge! Try again.", "Game Over");
            gui.showPanel("locationSelection");
        });

        // Start the mini-game
        miniGame.startGame();
    }
}