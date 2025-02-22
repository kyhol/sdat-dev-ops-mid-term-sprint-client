package com.keyin.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
// Sound imports (ensure these are available in your JDK)
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.keyin.hero.HeroDTO;
import com.keyin.hero.HeroService;
import com.keyin.location.LocationDTO;
import com.keyin.location.LocationService;  // This is the client REST wrapper
import com.keyin.minigame.AbstractMiniGame;
import com.keyin.minigame.MiniGameFactory;
import com.keyin.minigame.MiniGameService;

/**
 * GameInterfaceGUI now only handles user interface responsibilities:
 * - It displays screens using a CardLayout.
 * - It processes user input (button clicks, key events, etc.).
 * - It delegates all backend data retrieval, mini-game creation, and plushie awarding
 *   to dedicated service classes (LocationService, MiniGameService, HeroService).
 * - Only the sound management logic remains here.
 */
public class GameInterfaceGUI extends JFrame {

    private final HeroService heroService;
    private final LocationService locationService;
    private final MiniGameService miniGameService;
    private HeroDTO currentHero;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea outputArea;
    private JPanel locationButtonPanel;
    private DialogBox dialogBox; // Assumes DialogBox is implemented elsewhere

    // Game progress variables
    private int plushiesCollected = 0;
    private List<Long> completedLocations = new ArrayList<>();
    private List<LocationDTO> allLocations = new ArrayList<>();
    private List<String> collectedPlushies = new ArrayList<>();

    // Sound fields (kept in the GUI by design)
    private Clip musicClip;
    private boolean isMuted = false;

    // -----------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------
    public GameInterfaceGUI(HeroService heroService, LocationService locationService) {
        this.heroService = heroService;
        this.locationService = locationService;
        this.miniGameService = new MiniGameService();
        initializeUI();
    }

    // -----------------------------------------------------------------------------------
    // INITIALIZE UI
    // -----------------------------------------------------------------------------------
    private void initializeUI() {
        setTitle("RPG Adventure");

        // Make sure we exit on close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Basic size
        setSize(600, 500);

        // Full-screen on start
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Center on screen if not in full-screen (just in case)
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createWelcomePanel();
        createHeroCreationPanel();
        createStartPanel();
        createLocationSelectionPanel();
        createMiniGamePanel(); // fallback mini-game panel
        createFinalBossPanel();
        createFinalCongratulationsPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    // -----------------------------------------------------------------------------------
    // WELCOME PANEL
    // -----------------------------------------------------------------------------------
    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("RPG Adventure", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(e -> {
            stopMusic(); // Stop music before switching screens
            cardLayout.show(mainPanel, "heroCreation");
        });

        JButton muteButton = new JButton("🔊");
        muteButton.addActionListener(e -> {
            isMuted = !isMuted;
            if (isMuted) {
                muteButton.setText("🔇");
                stopMusic();
            } else {
                muteButton.setText("🔊");
                playMusic();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(muteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isMuted) {
                    playMusic();
                }
            }
        });

        mainPanel.add(panel, "welcome");
    }

    // -----------------------------------------------------------------------------------
    // HERO CREATION PANEL
    // -----------------------------------------------------------------------------------
    private void createHeroCreationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel nameLabel = new JLabel("Enter your hero's name: ");
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create Hero");
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    // Delegate hero creation to HeroService
                    currentHero = heroService.updateHero(name);
                    JOptionPane.showMessageDialog(this, "Hero name set to " + name + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "startPanel");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating hero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(createButton);

        formPanel.add(Box.createVerticalGlue());
        formPanel.add(inputPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalGlue());

        panel.add(formPanel);
        mainPanel.add(panel, "heroCreation");
    }

    // -----------------------------------------------------------------------------------
    // START PANEL
    // -----------------------------------------------------------------------------------
    private void createStartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        GameArtPanel gameArtPanel = new GameArtPanel(); // Custom panel to display game art
        panel.add(gameArtPanel, BorderLayout.CENTER);

        dialogBox = new DialogBox(); // Used to display narrative text
        panel.add(dialogBox, BorderLayout.SOUTH);

        final int[] dialogState = {0};

        // KeyEventDispatcher to progress narrative on key press
        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && panel.isShowing()) {
                    if (!dialogBox.isTyping()) {
                        switch (dialogState[0]) {
                            case 0:
                                int completedCount = locationService.getCompletedLocationsCount();
                                if (completedCount == 0) {
                                    dialogBox.showText("* Are you ready to start your adventure?");
                                    dialogState[0] = 1;
                                } else {
                                    cardLayout.show(mainPanel, "locationSelection");
                                }
                                break;
                            case 1:
                                cardLayout.show(mainPanel, "locationSelection");
                                break;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        int completedCount = locationService.getCompletedLocationsCount();
                        String progressText = getProgressionText(completedCount);
                        SwingUtilities.invokeLater(() -> {
                            dialogBox.showText(progressText);
                            dialogState[0] = 0;
                        });
                        return null;
                    }
                };
                worker.execute();
            }
        });

        mainPanel.add(panel, "startPanel");
        panel.putClientProperty("keyEventDispatcher", keyEventDispatcher);
    }

    /**
     * Returns narrative text based on the number of completed locations.
     */
    private String getProgressionText(int completedCount) {
        switch (completedCount) {
            case 0:
                return "* In a world where plushies hold magical powers...\n* Your quest to collect them all begins.";
            case 1:
                return "* You've found your first magical plushie!\n* But there are still many more to discover...";
            case 2:
                return "* Two magical plushies in your collection!\n* Their combined power grows stronger...";
            case 3:
                return "* Three magical plushies gathered!\n* You're becoming a true collector...";
            case 4:
                return "* Four magical plushies united!\n* Their magic resonates throughout the land...";
            default:
                return "* You've become a legendary plushie collector!\n* Your collection's power is unmatched!";
        }
    }

    // -----------------------------------------------------------------------------------
    // LOCATION SELECTION PANEL
    // -----------------------------------------------------------------------------------
    private void createLocationSelectionPanel() {
        JPanel locationSelectionPanel = new JPanel(new BorderLayout());
        JLabel prompt = new JLabel("Choose a location to explore:", SwingConstants.CENTER);
        prompt.setFont(new Font("Arial", Font.BOLD, 18));
        locationSelectionPanel.add(prompt, BorderLayout.NORTH);

        locationButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(locationButtonPanel);
        locationSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewPlushiesButton = new JButton("View Plushies");
        viewPlushiesButton.addActionListener(e -> {
            // Open the plushie dialog (inner class)
            PlushieDialog dialog = new PlushieDialog(this, collectedPlushies);
            dialog.setVisible(true);
        });
        buttonPanel.add(viewPlushiesButton);

        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reset the game? All progress will be lost.",
                    "Reset Game",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            }
        });
        buttonPanel.add(resetButton);

        locationSelectionPanel.add(buttonPanel, BorderLayout.SOUTH);

        locationSelectionPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                try {
                    // Delegate data retrieval to client LocationService
                    allLocations = locationService.getAllLocations();
                    populateLocationButtons();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GameInterfaceGUI.this, "Error fetching locations: " + ex.getMessage());
                }
            }
        });

        mainPanel.add(locationSelectionPanel, "locationSelection");
    }

    /**
     * Populates the location buttons based on the retrieved locations.
     * When a location button is clicked, it delegates mini-game launching to MiniGameService.
     */
    private void populateLocationButtons() {
        locationButtonPanel.removeAll();
        List<LocationDTO> filteredLocations = allLocations.stream()
                .filter(location -> location.getId() != 1 && location.getId() != 7)
                .collect(Collectors.toList());

        for (LocationDTO location : filteredLocations) {
            JButton locButton = new JButton(location.getName());
            locButton.addActionListener(e -> {
                if (completedLocations.contains(location.getId())) {
                    JOptionPane.showMessageDialog(this, "This location has already been completed!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    startMiniGame(location.getId());
                }
            });
            if (completedLocations.contains(location.getId())) {
                locButton.setEnabled(false);
            }
            locationButtonPanel.add(locButton);
        }
        locationButtonPanel.revalidate();
        locationButtonPanel.repaint();
    }

    // -----------------------------------------------------------------------------------
    // START MINI-GAME (MODIFIED)
    // -----------------------------------------------------------------------------------
    private void startMiniGame(Long locationId) {
        try {
            Long heroId = currentHero != null ? currentHero.getId() : 1L;
            System.out.println("GameInterfaceGUI: Starting minigame for location " + locationId);

            // 1) Remove any old panel named "miniGame-locationId", so we don't re-use stale data
            String panelName = "miniGame-" + locationId;
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof JPanel panel
                    && panel.getName() != null
                    && panel.getName().equals(panelName)) {
                    mainPanel.remove(panel);
                    break;
                }
            }
            mainPanel.revalidate();
            mainPanel.repaint();

            // 2) Create a brand-new mini-game object each time
            AbstractMiniGame miniGame = MiniGameFactory.createMiniGame(locationId, heroId, this);
            JPanel gamePanel = miniGame.getGamePanel();
            gamePanel.setName(panelName);

            // Set up callbacks
            miniGame.setOnCompleteCallback(() -> {
                System.out.println("GameInterfaceGUI: Minigame completed for location " + locationId);
                boolean success = locationService.completeLocation(locationId, allLocations, collectedPlushies);
                if (success) {
                    completedLocations.add(locationId);
                    plushiesCollected++;
                    if (plushiesCollected >= 5) {
                        cardLayout.show(mainPanel, "finalBoss");
                    } else {
                        cardLayout.show(mainPanel, "startPanel");
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update location status",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            miniGame.setOnFailCallback(() -> {
                System.out.println("GameInterfaceGUI: Minigame failed for location " + locationId);
                // If you want the ENTIRE game to reset on fail, do:
                restartGame();
                // Otherwise, show "locationSelection" or something else.
            });

            // 3) Add the new panel to the card layout
            mainPanel.add(gamePanel, panelName);

            // 4) Start the mini-game
            miniGame.startGame();

            // 5) Show the new panel
            cardLayout.show(mainPanel, panelName);

        } catch (Exception ex) {
            System.err.println("Error starting mini-game: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error starting mini-game: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------------------
    // MINI-GAME PANEL (FALLBACK)
    // -----------------------------------------------------------------------------------
    private void createMiniGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton winButton = new JButton("Win Mini-Game");
        winButton.addActionListener(e -> {
            if (plushiesCollected < 5) {
                cardLayout.show(mainPanel, "startPanel");
            } else {
                cardLayout.show(mainPanel, "finalBoss");
            }
        });
        JButton loseButton = new JButton("Lose Mini-Game");
        loseButton.addActionListener(e -> {
            outputArea.setText("You lost the mini-game. Game Over!");
            restartGame();
        });
        buttonPanel.add(winButton);
        buttonPanel.add(loseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "miniGame");
    }

    // -----------------------------------------------------------------------------------
    // FINAL BOSS PANEL
    // -----------------------------------------------------------------------------------
    private void createFinalBossPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("FINAL BOSS!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton winButton = new JButton("Win Final Boss");
        winButton.addActionListener(e -> {
            outputArea.setText("Fighting final boss...\nVictory!");
            cardLayout.show(mainPanel, "finalCongratulations");
        });
        JButton loseButton = new JButton("Lose Final Boss");
        loseButton.addActionListener(e -> {
            outputArea.setText("You lost to the final boss. Game Over!");
            restartGame();
        });
        buttonPanel.add(winButton);
        buttonPanel.add(loseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "finalBoss");
    }

    // -----------------------------------------------------------------------------------
    // FINAL CONGRATULATIONS PANEL
    // -----------------------------------------------------------------------------------
    private void createFinalCongratulationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Congratulations! You've completed the adventure!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        panel.add(quitButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "finalCongratulations");
    }

    // -----------------------------------------------------------------------------------
    // RESTART GAME
    // -----------------------------------------------------------------------------------
    private void restartGame() {
        plushiesCollected = 0;
        completedLocations.clear();
        collectedPlushies.clear();

        try {
            locationService.resetAllLocations();
            allLocations = locationService.getAllLocations();
            populateLocationButtons();
            cardLayout.show(mainPanel, "welcome");
        } catch (Exception ex) {
            System.err.println("Error resetting game: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error resetting game: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------------------
    // UTILITY METHODS FOR MINI-GAME SERVICE (Used by MiniGameService to update the GUI)
    // -----------------------------------------------------------------------------------
    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    // -----------------------------------------------------------------------------------
    // SOUND METHODS (Kept in the GUI as per design)
    // -----------------------------------------------------------------------------------
    private void playMusic() {
        try {
            java.net.URL soundURL = getClass().getResource("/audio/opening-theme.wav");
            if (soundURL == null) {
                System.err.println("Could not find audio file");
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            setVolume(0.5f);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    private void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    private void setVolume(float volume) {
        if (musicClip != null) {
            try {
                FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            } catch (Exception e) {
                System.err.println("Error setting volume: " + e.getMessage());
            }
        }
    }

    @Override
    public void dispose() {
        stopMusic();
        super.dispose();
    }

    // -----------------------------------------------------------------------------------
    // INNER CLASS: PLUSHIEDIALOG
    // (Ensures references to PlushieDialog compile properly.)
    // -----------------------------------------------------------------------------------
    public class PlushieDialog extends JDialog {
        // Optional: define color-coded plushies or descriptions
        private final Map<String, Color> plushieColors = new HashMap<>();
        private final Map<String, String> plushieDescriptions = new HashMap<>();

        public PlushieDialog(JFrame parent, List<String> plushies) {
            super(parent, "Your Plushie Collection", true);
            setSize(600, 400);
            setLocationRelativeTo(parent);

            // Prepopulate some color associations or default
            plushieColors.put("UNDERWORLD PLUSHIE", new Color(128, 0, 128)); // purple
            plushieColors.put("DRAGON'S PEAK PLUSHIE", new Color(139, 69, 19)); // brown
            plushieColors.put("MYSTIC FOREST PLUSHIE", new Color(0, 100, 0)); // green
            plushieColors.put("SHADOW REALM PLUSHIE", new Color(64, 64, 64)); // dark gray
            plushieColors.put("SKY SANCTUARY PLUSHIE", new Color(70, 130, 180)); // steelBlue

            // Prepopulate some descriptions or ASCII
            plushieDescriptions.put("UNDERWORLD PLUSHIE", 
                "A plush from the dark underworld.\nMysterious energies swirl around it.");
            plushieDescriptions.put("DRAGON'S PEAK PLUSHIE",
                "A fiery plush from high peaks.\nIt might roar when squeezed!");
            plushieDescriptions.put("MYSTIC FOREST PLUSHIE", 
                "Soft and earthy plush with vine-like patterns.\nIt smells of pine!");
            plushieDescriptions.put("SHADOW REALM PLUSHIE", 
                "Creepy but cool.\nIt seems to fade if you don't look closely!");
            plushieDescriptions.put("SKY SANCTUARY PLUSHIE", 
                "Cloud-like plush, light as air.\nYou feel a breeze around it.");

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Your Magical Plushie Collection", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel plushiePanel = new JPanel();
            plushiePanel.setLayout(new BoxLayout(plushiePanel, BoxLayout.Y_AXIS));

            if (plushies.isEmpty()) {
                JLabel emptyLabel = new JLabel("No plushies collected yet!");
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyLabel.setFont(new Font("Serif", Font.ITALIC, 18));
                plushiePanel.add(emptyLabel);
            } else {
                for (String plushie : plushies) {
                    JPanel plushieItemPanel = createPlushieItemPanel(plushie);
                    plushiePanel.add(plushieItemPanel);
                    plushiePanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }

            JScrollPane scrollPane = new JScrollPane(plushiePanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("Serif", Font.BOLD, 16));
            closeButton.addActionListener(e -> dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        }

        /**
         * Creates a single plushie "card" with a background color, 
         * a name label, and a short description.
         */
        private JPanel createPlushieItemPanel(String plushieName) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            panel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Decide background color
            // If plushieName isn't in the map, pick random color
            Color bgColor = plushieColors.getOrDefault(plushieName.toUpperCase(),
                    new Color((int) (Math.random() * 0x1000000)));
            panel.setBackground(bgColor);

            // Title
            JLabel nameLabel = new JLabel(plushieName);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Description
            String desc = plushieDescriptions.getOrDefault(plushieName.toUpperCase(),
                    "An unknown but still magical plush!");
            JTextArea descArea = new JTextArea(desc);
            descArea.setEditable(false);
            descArea.setOpaque(false);
            descArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            descArea.setForeground(Color.WHITE);
            descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Hover effect (brighten background)
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(bgColor.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(bgColor);
                }
            });

            panel.add(nameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(descArea);

            return panel;
        }
    }
}