package com.keyin.ui;

import java.util.stream.Collectors;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Box;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keyin.hero.HeroDTO;
import com.keyin.hero.HeroService;
import com.keyin.location.LocationDTO;
import com.keyin.location.LocationService;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GameInterfaceGUI extends JFrame {
    private final HeroService heroService;
    private final LocationService locationService;
    private HeroDTO currentHero;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea outputArea;
    private JPanel locationButtonPanel;
    private DialogBox dialogBox;

    private int plushiesCollected = 0;
    private List<Long> completedLocations = new ArrayList<>();
    private List<LocationDTO> allLocations = new ArrayList<>();
    private List<String> collectedPlushies = new ArrayList<>();

    // Add these new fields for music
    private Clip musicClip;
    private boolean isMuted = false;

    public GameInterfaceGUI(HeroService heroService, LocationService locationService) {
        this.heroService = heroService;
        this.locationService = locationService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("RPG Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createWelcomePanel();
        createHeroCreationPanel();
        createStartPanel();
        createLocationSelectionPanel();
        createMiniGamePanel();
        createFinalBossPanel();
        createFinalCongratulationsPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("RPG Adventure", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        // Create button panel for multiple buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(e -> {
            stopMusic();
            cardLayout.show(mainPanel, "heroCreation");
        });

        // Add mute button
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

        // Add component listener to play music when panel is shown
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
                    // Update instead of create
                    currentHero = heroService.updateHero(name);
                    JOptionPane.showMessageDialog(this, "Hero name set to " + name + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Go directly to start panel
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



    private boolean updateLocationCompletion(Long locationId, boolean completed) {
        try {
            URL url = new URL("http://localhost:8080/location/" + locationId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create the request body
            LocationDTO updateDto = new LocationDTO();
            updateDto.setId(locationId);
            updateDto.setCompleted(completed);

            ObjectMapper mapper = new ObjectMapper();
            String jsonInputString = mapper.writeValueAsString(updateDto);

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Update completion status response code: " + responseCode);

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            System.err.println("Error updating location completion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private int getCompletedLocationsCount() {
        try {
            URL url = new URL("http://localhost:8080/location");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                System.out.println("Response body: " + response.toString());

                ObjectMapper mapper = new ObjectMapper();
                List<LocationDTO> locations = mapper.readValue(
                        response.toString(),
                        new TypeReference<List<LocationDTO>>(){}
                );

                System.out.println("Found " + locations.size() + " locations");

                int completedCount = 0;
                for (LocationDTO loc : locations) {
                    if (loc.isCompleted()) {
                        completedCount++;
                    }
                }
                return completedCount;
            } else {
                System.err.println("Server returned error code: " + responseCode);
                if (responseCode == 404) {
                    System.err.println("The endpoint /location was not found. Please verify:");
                    System.err.println("1. The Spring Boot server is running");
                    System.err.println("2. The correct endpoint URL is being used");
                    System.err.println("3. The LocationController is properly mapped to /location");
                }

                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    System.err.println("Error response: " + errorResponse.toString());
                } catch (Exception e) {
                    System.err.println("Could not read error stream");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while fetching locations: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

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

    private void createStartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Add the game art panel
        GameArtPanel gameArtPanel = new GameArtPanel();
        panel.add(gameArtPanel, BorderLayout.CENTER);

        // Add the dialog box
        dialogBox = new DialogBox();
        panel.add(dialogBox, BorderLayout.SOUTH);

        // Track the dialog state
        final int[] dialogState = {0};

        // Create global keyboard manager
        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && panel.isShowing()) {
                    if (!dialogBox.isTyping()) {
                        switch (dialogState[0]) {
                            case 0:
                                int completedCount = getCompletedLocationsCount();
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

        // Add the global keyboard manager
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);

        // Component listener for initial setup
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        int completedCount = getCompletedLocationsCount();
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
            PlushieDialog dialog = new PlushieDialog(this, collectedPlushies);
            dialog.setVisible(true);
        });
        buttonPanel.add(viewPlushiesButton);

        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reset the game? All progress will be lost.",
                    "Reset Game",
                    JOptionPane.YES_NO_OPTION
            );

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
                    JOptionPane.showMessageDialog(GameInterfaceGUI.this, "Error fetching locations: " + ex.getMessage());
                }
            }
        });

        mainPanel.add(locationSelectionPanel, "locationSelection");
    }

    private void populateLocationButtons() {
        locationButtonPanel.removeAll();
        // Filter out locations with IDs 1 and 7
        List<LocationDTO> filteredLocations = allLocations.stream()
                .filter(location -> location.getId() != 1 && location.getId() != 7)
                .collect(Collectors.toList());

        for (LocationDTO location : filteredLocations) {
            JButton locButton = new JButton(location.getName());
            locButton.addActionListener((ActionEvent e) -> {
                if (completedLocations.contains(location.getId())) {
                    JOptionPane.showMessageDialog(this, "This location has already been completed!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        // Start showing the location entry immediately
                        outputArea.setText("You have entered " + location.getName() + ".\nPlaying mini-game...");

                        // Complete location in background
                        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                            @Override
                            protected Boolean doInBackground() throws Exception {
                                return completeLocation(location.getId());
                            }

                            @Override
                            protected void done() {
                                try {
                                    if (get()) {  // if completion was successful
                                        completedLocations.add(location.getId());
                                        plushiesCollected++;
                                        String progressText = getProgressionText(completedLocations.size());
                                        dialogBox.clear();
                                        dialogBox.showText(progressText);
                                        locButton.setEnabled(false);
                                        cardLayout.show(mainPanel, "miniGame");
                                    } else {
                                        JOptionPane.showMessageDialog(GameInterfaceGUI.this,
                                                "Failed to update location status",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (Exception ex) {
                                    System.err.println("Error completing location: " + ex.getMessage());
                                    JOptionPane.showMessageDialog(GameInterfaceGUI.this,
                                            "Error: " + ex.getMessage(),
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        };
                        worker.execute();

                    } catch (Exception ex) {
                        System.err.println("Error initiating location completion: " + ex.getMessage());
                        JOptionPane.showMessageDialog(GameInterfaceGUI.this,
                                "Error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
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
    private boolean completeLocation(Long locationId) {
        try {
            URL url = new URL("http://localhost:8080/location/" + locationId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create a minimal JSON body with just the completion status
            String jsonInputString = "{\"id\":" + locationId + ",\"completed\":true}";

            // Send the request
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Complete location response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully completed location " + locationId);
                // Get the location name and add it to collected plushies
                for (LocationDTO location : allLocations) {
                    if (location.getId().equals(locationId)) {
                        collectedPlushies.add(location.getName() + " Plushie");
                        break;
                    }
                }
                return true;
            } else {
                System.err.println("Failed to complete location " + locationId);
                // Read and print error response if any
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.err.println("Error response: " + response.toString());
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception completing location: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public class PlushieDialog extends JDialog {
        public PlushieDialog(JFrame parent, List<String> plushies) {
            super(parent, "Your Plushie Collection", true);
            setSize(400, 300);
            setLocationRelativeTo(parent);

            // Create main panel with a nice border layout
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add title at the top
            JLabel titleLabel = new JLabel("Your Magical Plushie Collection", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            // Create panel for plushies with vertical BoxLayout
            JPanel plushiePanel = new JPanel();
            plushiePanel.setLayout(new BoxLayout(plushiePanel, BoxLayout.Y_AXIS));

            if (plushies.isEmpty()) {
                JLabel emptyLabel = new JLabel("No plushies collected yet!");
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                plushiePanel.add(emptyLabel);
            } else {
                for (String plushie : plushies) {
                    JPanel plushieItemPanel = createPlushieItemPanel(plushie);
                    plushiePanel.add(plushieItemPanel);
                    plushiePanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing between items
                }
            }

            // Add plushie panel to a scroll pane
            JScrollPane scrollPane = new JScrollPane(plushiePanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Add close button at the bottom
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        }

        private JPanel createPlushieItemPanel(String plushieName) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

            JLabel nameLabel = new JLabel(plushieName);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(nameLabel);
            return panel;
        }
    }
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

    private void restartGame() {
        plushiesCollected = 0;
        completedLocations.clear();
        collectedPlushies.clear();

        // Reset locations in the database
        try {
            URL url = new URL("http://localhost:8080/location/reset");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully reset all locations");

                // Refresh local location data
                allLocations = locationService.getAllLocations();
                populateLocationButtons();

                // Show welcome screen
                cardLayout.show(mainPanel, "welcome");
            } else {
                System.err.println("Failed to reset locations. Response code: " + responseCode);
                JOptionPane.showMessageDialog(this,
                        "Failed to reset game state",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("Error resetting game: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error resetting game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void playMusic() {
        try {
            // Load the sound file from resources
            java.net.URL soundURL = getClass().getResource("/audio/opening-theme.wav");
            if (soundURL == null) {
                System.err.println("Could not find audio file");
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);

            // Set initial volume (optional)
            setVolume(0.5f); // 50% volume

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
                FloatControl gainControl =
                        (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
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

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";
        HeroService heroService = new HeroService(baseUrl);
        LocationService locationService = new LocationService(baseUrl);
        SwingUtilities.invokeLater(() -> {
            GameInterfaceGUI gui = new GameInterfaceGUI(heroService, locationService);
            gui.setVisible(true);
        });
    }
}