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
import com.keyin.plushie.PlushieDTO;
import com.keyin.plushie.PlushieService;

/**
 * GameInterfaceGUI with:
 *  - Gradient backgrounds for screens
 *  - Larger, stylish fonts
 *  - Reverted to plain multiline text in the Start Panel (no HTML)
 */
public class GameInterfaceGUI extends JFrame {

    private final HeroService heroService;
    private final LocationService locationService;
    private final MiniGameService miniGameService;
    private final PlushieService plushieService;
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
    private List<PlushieDTO> collectedPlushies = new ArrayList<>();

    // Sound fields (kept in the GUI)
    private Clip musicClip;
    private boolean isMuted = false;

    // -----------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------
    public GameInterfaceGUI(HeroService heroService, LocationService locationService, PlushieService plushieService) {
        this.heroService = heroService;
        this.locationService = locationService;
        this.plushieService = plushieService;
        this.miniGameService = new MiniGameService();
        initializeUI();
    }

    // -----------------------------------------------------------------------------------
    // INITIALIZE UI
    // -----------------------------------------------------------------------------------
    private void initializeUI() {
        setTitle("RPG Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        GradientPanel panel = new GradientPanel(new Color(45, 72, 89), new Color(5, 15, 25));
        panel.setLayout(new BorderLayout(20, 20));

        JLabel titleLabel = new JLabel("RPG Adventure", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel introLabel = new JLabel("<html><center>"
            + "<p style='font-size:20px;color:lightgray;'>"
            + "Embark on a magical quest to collect the legendary plushies!<br>"
            + "Only the bravest heroes can prevail..."
            + "</p></center></html>", SwingConstants.CENTER);
        panel.add(introLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton startButton = new JButton("Begin Your Journey");
        styleButton(startButton, new Color(100, 30, 30), Color.WHITE, 20, 240, 50);
        startButton.addActionListener(e -> {
            stopMusic();
            cardLayout.show(mainPanel, "heroCreation");
        });

        JButton muteButton = new JButton("🔊");
        styleButton(muteButton, new Color(30, 30, 100), Color.WHITE, 20, 80, 50);
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

        // Music on show
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
        GradientPanel panel = new GradientPanel(new Color(70, 30, 70), new Color(20, 0, 20));
        panel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel createHeroTitle = new JLabel("Forge Your Hero");
        createHeroTitle.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        createHeroTitle.setForeground(Color.WHITE);
        createHeroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(createHeroTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Input name
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Hero's Name: ");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        // Create button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton createButton = new JButton("Begin Adventure!");
        styleButton(createButton, new Color(120, 30, 30), Color.WHITE, 22, 220, 40);

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid name.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    currentHero = heroService.updateHero(name);
                    JOptionPane.showMessageDialog(this,
                            "Hero name set to " + name + "!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "startPanel");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating hero: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(createButton);

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
        GradientPanel panel = new GradientPanel(new Color(15, 15, 40), new Color(50, 25, 85));
        panel.setLayout(new BorderLayout());

        // Some big art in center
        GameArtPanel gameArtPanel = new GameArtPanel();
        panel.add(gameArtPanel, BorderLayout.CENTER);

        // DialogBox at bottom
        dialogBox = new DialogBox();
        panel.add(dialogBox, BorderLayout.SOUTH);

        final int[] dialogState = {0};

        // KeyEventDispatcher
        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && panel.isShowing()) {
                    if (!dialogBox.isTyping()) {
                        switch (dialogState[0]) {
                            case 0:
                                int completedCount = locationService.getCompletedLocationsCount();
                                if (completedCount == 0) {
                                    // Show plain multiline text
                                    dialogBox.showText("* Are you ready to start your adventure?\nPress any key to continue...");
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
        panel.putClientProperty("keyEventDispatcher", keyEventDispatcher);

        // Show initial progress text
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        int completedCount = locationService.getCompletedLocationsCount();
                        String progressText = getProgressionText(completedCount);
                        SwingUtilities.invokeLater(() -> {
                            // Plain multiline text
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
    }

    /**
     * Return narrative text based on completed locations (plain multiline).
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
        GradientPanel locationSelectionPanel = new GradientPanel(new Color(25, 55, 55), new Color(0, 20, 20));
        locationSelectionPanel.setLayout(new BorderLayout(15,15));

        JLabel prompt = new JLabel("Select a Realm to Explore:", SwingConstants.CENTER);
        prompt.setFont(new Font("Serif", Font.BOLD, 26));
        prompt.setForeground(Color.WHITE);
        prompt.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        locationSelectionPanel.add(prompt, BorderLayout.NORTH);

        locationButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        locationButtonPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(locationButtonPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        locationSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton viewPlushiesButton = new JButton("View Plushies");
        styleButton(viewPlushiesButton, new Color(100, 50, 50), Color.WHITE, 16, 150, 40);
        viewPlushiesButton.addActionListener(e -> {
            PlushieService plushieService = new PlushieService("http://localhost:8080");
            PlushieDialog dialog = new PlushieDialog(this, plushieService);
            dialog.setVisible(true);
        });
        buttonPanel.add(viewPlushiesButton);

        JButton resetButton = new JButton("Reset Game");
        styleButton(resetButton, new Color(50, 50, 100), Color.WHITE, 16, 150, 40);
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
                    allLocations = locationService.getAllLocations();
                    populateLocationButtons();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GameInterfaceGUI.this,
                            "Error fetching locations: " + ex.getMessage());
                }
            }
        });

        mainPanel.add(locationSelectionPanel, "locationSelection");
    }

    private void populateLocationButtons() {
        locationButtonPanel.removeAll();

        List<LocationDTO> filteredLocations = allLocations.stream()
                .filter(location -> location.getId() != 1 && location.getId() != 7)
                .collect(Collectors.toList());

        for (LocationDTO location : filteredLocations) {
            JButton locButton = new JButton(location.getName());
            locButton.setFont(new Font("Serif", Font.BOLD, 20));
            locButton.setForeground(Color.WHITE);
            locButton.setBackground(new Color(70,70,100));
            locButton.setFocusPainted(false);
            locButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            // Hover effect
            locButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    locButton.setBackground(new Color(90,90,150));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    locButton.setBackground(new Color(70,70,100));
                }
            });

            locButton.addActionListener(e -> {
                if (completedLocations.contains(location.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "This location has already been completed!",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    startMiniGame(location.getId());
                }
            });

            if (completedLocations.contains(location.getId())) {
                locButton.setEnabled(false);
                locButton.setText(location.getName() + " (Completed)");
                locButton.setBackground(new Color(60,60,60));
            }

            locationButtonPanel.add(locButton);
        }

        locationButtonPanel.revalidate();
        locationButtonPanel.repaint();
    }

    // -----------------------------------------------------------------------------------
    // START MINI-GAME
    // -----------------------------------------------------------------------------------
    private void startMiniGame(Long locationId) {
        try {
            Long heroId = currentHero != null ? currentHero.getId() : 1L;
            System.out.println("GameInterfaceGUI: Starting minigame for location " + locationId);

            // Remove old panel if it exists
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

            // Create brand-new mini-game
            AbstractMiniGame miniGame = MiniGameFactory.createMiniGame(locationId, heroId, this);
            JPanel gamePanel = miniGame.getGamePanel();
            gamePanel.setName(panelName);

            // Callbacks
            miniGame.setOnCompleteCallback(() -> {
                System.out.println("GameInterfaceGUI: Minigame completed for location " + locationId);
                boolean success = locationService.completeLocation(locationId, allLocations);
                if (success) {
                    completedLocations.add(locationId);
                    try {
                        plushieService.collectPlushie((locationId - 1));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    plushiesCollected++;
                    if (plushiesCollected >= 5) {
                        cardLayout.show(mainPanel, "finalBoss");
                    } else {
                        cardLayout.show(mainPanel, "startPanel");
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update location status",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            miniGame.setOnFailCallback(() -> {
                System.out.println("GameInterfaceGUI: Minigame failed for location " + locationId);
                restartGame();
            });

            mainPanel.add(gamePanel, panelName);
            miniGame.startGame();
            cardLayout.show(mainPanel, panelName);

        } catch (Exception ex) {
            System.err.println("Error starting mini-game: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error starting mini-game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------------------
    // UTILITY METHODS
    // -----------------------------------------------------------------------------------
    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    private void styleButton(JButton button, Color bg, Color fg, int fontSize, int width, int height) {
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
    }

    // -----------------------------------------------------------------------------------
    // SOUND
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
    // PLUSHIEDIALOG
    // -----------------------------------------------------------------------------------
    public class PlushieDialog extends JDialog {
        public PlushieDialog(JFrame parent, PlushieService plushieService) {
            super(parent, "Your Plushie Collection", true);
            setSize(600, 400);
            setLocationRelativeTo(parent);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Your Magical Plushie Collection", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel plushiePanel = new JPanel();
            plushiePanel.setLayout(new BoxLayout(plushiePanel, BoxLayout.Y_AXIS));

            try {
                List<PlushieDTO> allPlushies = plushieService.getAllPlushies();
                // Filter to only show collected plushies
                List<PlushieDTO> collectedPlushies = allPlushies.stream()
                        .filter(PlushieDTO::isCollected)
                        .toList();

                if (collectedPlushies.isEmpty()) {
                    JLabel emptyLabel = new JLabel("No plushies collected yet!");
                    emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptyLabel.setFont(new Font("Serif", Font.ITALIC, 18));
                    plushiePanel.add(emptyLabel);
                } else {
                    for (PlushieDTO plushie : collectedPlushies) {
                        JPanel plushieItemPanel = createPlushieItemPanel(plushie);
                        plushiePanel.add(plushieItemPanel);
                        plushiePanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    }
                }
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Error loading plushies: " + e.getMessage());
                errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                errorLabel.setFont(new Font("Serif", Font.ITALIC, 18));
                errorLabel.setForeground(Color.RED);
                plushiePanel.add(errorLabel);
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

        private JPanel createPlushieItemPanel(PlushieDTO plushie) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            panel.setAlignmentX(Component.CENTER_ALIGNMENT);

            Color bgColor = plushie.getColor() != null ?
                    Color.decode(plushie.getColor()) :
                    new Color((int) (Math.random() * 0x1000000));
            panel.setBackground(bgColor);

            JLabel nameLabel = new JLabel(plushie.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            String description = plushie.getDescription() != null ?
                    plushie.getDescription() :
                    "An unknown but still magical plush!";
            JTextArea descArea = new JTextArea(description);
            descArea.setEditable(false);
            descArea.setOpaque(false);
            descArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            descArea.setForeground(Color.WHITE);
            descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Hover effect
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
    /**
     * Helper panel for gradient backgrounds
     */
    private static class GradientPanel extends JPanel {
        private final Color color1;
        private final Color color2;

        public GradientPanel(Color color1, Color color2) {
            this.color1 = color1;
            this.color2 = color2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }
    }
}
