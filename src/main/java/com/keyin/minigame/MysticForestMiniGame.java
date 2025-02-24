package com.keyin.minigame;

import com.keyin.ui.DialogBox;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class MysticForestMiniGame extends AbstractMiniGame {
    private static final int TILE_SIZE = 40;
    private static final int BACKGROUND_WIDTH = 1600;
    private static final int BACKGROUND_HEIGHT = 1200;

    // Player starts at (4, 0)
    private int playerX = 4, playerY = 0;
    private boolean hasKey = false;
    private boolean caveEntered = false;
    private boolean gameCompleted = false;

    private final int keyX = 18, keyY = 4;
    private final int caveX = 15, caveY = 14;

    // Goal positions for completing the game
    private final Set<Point> goalPositions = Set.of(
            new Point(0, 14),
            new Point(0, 15)
    );

    private JLabel playerLabel;
    private JLabel backgroundLabel;
    private JPanel buttonPanel;
    private JPanel bottomPanel;
    private JLabel playerTrackerLabel;

    public MysticForestMiniGame(Long locationId, Long heroId, JFrame parentFrame) {
        super(locationId, heroId, parentFrame);
        // Show intro text on the Swing event thread
        SwingUtilities.invokeLater(() -> {
            dialogBox.showText(getIntroText());
            dialogBox.setVisible(true);
        });
    }

    @Override
    protected void customizeUI() {
        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(Color.BLACK);

        // ---- TOP TITLE ----
        JLabel locationLabel = new JLabel("MYSTIC FOREST", SwingConstants.CENTER);
        locationLabel.setFont(new Font("Serif", Font.BOLD, 32));
        locationLabel.setForeground(Color.WHITE);
        locationLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        gamePanel.add(locationLabel, BorderLayout.NORTH);

        // ---- CENTER: BACKGROUND + PLAYER (LayeredPane) ----
        JLayeredPane centerPane = new JLayeredPane();
        centerPane.setPreferredSize(new Dimension(1200, 800));

        // Background
        URL bgURL = getClass().getClassLoader().getResource("image/forest_map.jpg");
        if (bgURL != null) {
            ImageIcon bgIcon = new ImageIcon(bgURL);
            // Scale to fit if you want
            Image scaled = bgIcon.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
            bgIcon = new ImageIcon(scaled);

            backgroundLabel = new JLabel(bgIcon);
            backgroundLabel.setBounds(0, 0, 1200, 800);
            centerPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        } else {
            backgroundLabel = new JLabel("Background not found");
            backgroundLabel.setOpaque(true);
            backgroundLabel.setBackground(Color.GREEN);
            backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
            backgroundLabel.setBounds(0, 0, 1200, 800);
            centerPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        }

        // Player
        URL playerURL = getClass().getClassLoader().getResource("image/wizard_cat.png");
        if (playerURL != null) {
            ImageIcon playerIcon = new ImageIcon(playerURL);
            playerLabel = new JLabel(playerIcon);
        } else {
            playerLabel = new JLabel("Player");
            playerLabel.setOpaque(true);
            playerLabel.setBackground(Color.BLUE);
            playerLabel.setForeground(Color.WHITE);
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        playerLabel.setBounds(playerX * TILE_SIZE, playerY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        centerPane.add(playerLabel, JLayeredPane.PALETTE_LAYER);

        gamePanel.add(centerPane, BorderLayout.CENTER);

        // ---- BOTTOM: DIALOG + MOVEMENT BUTTONS ----
        // We'll place them side by side or center vs east
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.DARK_GRAY);

        // Dialog box in the center (or west)
        if (dialogBox == null) {
            dialogBox = new DialogBox();
        }
        bottomPanel.add(dialogBox, BorderLayout.CENTER);

        // Movement buttons in a panel on the right
        buttonPanel = createMovementButtonPanel();
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Optional: show player's position above the dialog
        playerTrackerLabel = new JLabel("Player Position: (4, 0)", SwingConstants.CENTER);
        playerTrackerLabel.setForeground(Color.WHITE);
        bottomPanel.add(playerTrackerLabel, BorderLayout.NORTH);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        gamePanel.revalidate();
        gamePanel.repaint();
    }

    /**
     * Create the movement buttons in a vertical or grid layout.
     * Then place the panel on the RIGHT side of the bottom panel.
     */
    private JPanel createMovementButtonPanel() {
        // For a vertical stack, use GridLayout(4,1) or BoxLayout
        // For a 2x2, or something else, choose accordingly
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBackground(Color.BLACK);

        JButton upBtn = new JButton("Up");
        JButton downBtn = new JButton("Down");
        JButton leftBtn = new JButton("Left");
        JButton rightBtn = new JButton("Right");

        upBtn.addActionListener(e -> movePlayer(0, -1));
        downBtn.addActionListener(e -> movePlayer(0, 1));
        leftBtn.addActionListener(e -> movePlayer(-1, 0));
        rightBtn.addActionListener(e -> movePlayer(1, 0));

        panel.add(upBtn);
        panel.add(downBtn);
        panel.add(leftBtn);
        panel.add(rightBtn);

        return panel;
    }

    @Override
    protected void handleKeyPress(KeyEvent e) {
        // If you want arrow keys to move the player, handle them here
    }

    private void movePlayer(int dx, int dy) {
        if (gameCompleted) return;

        int newX = playerX + dx;
        int newY = playerY + dy;

        // Bounds checking
        if (newX * TILE_SIZE >= 0 && newX * TILE_SIZE < BACKGROUND_WIDTH
                && newY * TILE_SIZE >= 0 && newY * TILE_SIZE < BACKGROUND_HEIGHT) {
            playerX = newX;
            playerY = newY;
        }

        updatePlayerPosition();
        checkInteractions();
    }

    private void updatePlayerPosition() {
        playerLabel.setBounds(playerX * TILE_SIZE, playerY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        playerLabel.repaint();
        gamePanel.repaint();

        if (playerTrackerLabel != null) {
            playerTrackerLabel.setText("Player Position: (" + playerX + ", " + playerY + ")");
        }
    }

    private void checkInteractions() {
        if (playerX == keyX && playerY == keyY && !hasKey) {
            hasKey = true;
            showTemporaryDialog("You found a key!");
        }

        if (playerX == caveX && playerY == caveY && !caveEntered) {
            if (hasKey) {
                caveEntered = true;
                showTemporaryDialog("You used the key to enter the cave!");
            } else {
                showTemporaryDialog("You need a key to enter the cave!");
            }
        }

        // If at a goal position AND cave was entered => game complete
        if (goalPositions.contains(new Point(playerX, playerY)) && caveEntered && !gameCompleted) {
            gameCompleted = true;
            dialogBox.showText(
                    "Congratulations! You completed Mystic Forest!",
                    this::completeGame
            );
            dialogBox.setVisible(true);
            disableMovementButtons();
        }
    }

    // Show the dialog briefly and then restore buttons:
    private void showTemporaryDialog(String message) {
        disableMovementButtons();
        dialogBox.showText(message, this::enableMovementButtons);
        dialogBox.setVisible(true);
    }

    private void disableMovementButtons() {
        for (Component c : buttonPanel.getComponents()) {
            c.setEnabled(false);
        }
    }

    private void enableMovementButtons() {
        for (Component c : buttonPanel.getComponents()) {
            c.setEnabled(true);
        }
    }

    @Override
    protected String getGameSpecificAsciiArt() {
        return "MYSTIC FOREST ASCII ART HERE";
    }

    @Override
    protected String getIntroText() {
        return "You find yourself in the Mystic Forest. Find the key, enter the cave, "
                + "and then proceed to the exit to complete the mini-game.";
    }
}
