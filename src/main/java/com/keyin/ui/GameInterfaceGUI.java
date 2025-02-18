package com.keyin.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.keyin.hero.HeroDTO;
import com.keyin.hero.HeroService;
import com.keyin.location.LocationDTO;
import com.keyin.location.LocationService;

/**
 * This class defines the GUI for our RPG Adventure game.
 * It uses a CardLayout to switch between different "screens" such as
 * welcome, hero creation, hero update, start panel, location selection,
 * mini-game simulation, final boss, and a final congratulations panel.
 *
 * It interacts with the API via HeroService and LocationService.
 */
public class GameInterfaceGUI extends JFrame {
    // API service classes.
    private final HeroService heroService;
    private final LocationService locationService;
    private HeroDTO currentHero;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea outputArea;

    // Panel that will contain dynamically generated location buttons.
    private JPanel locationButtonPanel;

    // Game state variables.
    private int plushiesCollected = 0;
    private List<Long> completedLocations = new ArrayList<>();
    // List of locations fetched from the API.
    private List<LocationDTO> allLocations = new ArrayList<>();

    public GameInterfaceGUI(HeroService heroService, LocationService locationService) {
        this.heroService = heroService;
        this.locationService = locationService;
        initializeUI();
    }

    /**
     * Initializes the UI and creates all panels.
     */
    private void initializeUI() {
        setTitle("RPG Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createWelcomePanel();
        createHeroCreationPanel();
        createHeroUpdatePanel();         // Optional panel to update hero's name.
        createStartPanel();              // "Press any key to start" panel.
        createLocationSelectionPanel();  // Dynamically populated location selection panel.
        createMiniGamePanel();           // Mini-game simulation panel (win or lose).
        createFinalBossPanel();          // Final boss fight panel (win or lose).
        createFinalCongratulationsPanel(); // Final congratulations panel.

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    /**
     * 1. Welcome Panel: A simple welcome screen with a title and a button.
     */
    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("RPG Adventure", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(e -> cardLayout.show(mainPanel, "heroCreation"));
        panel.add(startButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "welcome");
    }

    /**
     * 2. Hero Creation Panel: Allows user to enter a hero name and create the hero via the API.
     */
    private void createHeroCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter your hero's name: "));
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameField);
        panel.add(inputPanel, BorderLayout.CENTER);

        JButton createButton = new JButton("Create Hero");
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    currentHero = heroService.createHero(name);
                    JOptionPane.showMessageDialog(this, "Hero " + name + " created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Proceed to hero update panel.
                    cardLayout.show(mainPanel, "heroUpdate");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error creating hero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(createButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "heroCreation");
    }

    /**
     * 3. Hero Update Panel (Optional): Lets the user optionally update the hero's name.
     * After updating, the panel fetches the list of locations.
     */
    private void createHeroUpdatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Update your hero's name (optional): "));
        JTextField updateField = new JTextField(20);
        inputPanel.add(updateField);
        panel.add(inputPanel, BorderLayout.CENTER);

        JButton updateButton = new JButton("Update & Continue");
        updateButton.addActionListener(e -> {
            String newName = updateField.getText().trim();
            if (!newName.isEmpty()) {
                currentHero.setName(newName);
                // Optionally, call an update API here.
            }
            // Fetch locations from the API.
            try {
                allLocations = locationService.getAllLocations();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error fetching locations: " + ex.getMessage());
            }
            cardLayout.show(mainPanel, "startPanel");
        });
        panel.add(updateButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "heroUpdate");
    }

    /**
     * 4. Start Panel ("Press any key to start"): Prompts the user to start their adventure.
     */
    private void createStartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Press any key to start your adventure!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);

        // Button to simulate a key press.
        JButton pressKeyButton = new JButton("Press any key");
        pressKeyButton.addActionListener(e -> cardLayout.show(mainPanel, "locationSelection"));
        panel.add(pressKeyButton, BorderLayout.SOUTH);

        // Optional KeyListener.
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                cardLayout.show(mainPanel, "locationSelection");
            }
        });
        panel.setFocusable(true);

        mainPanel.add(panel, "startPanel");
    }

    /**
     * 5. Location Selection Panel: Dynamically shows location buttons fetched from the API.
     * When a location is chosen, it simulates moving there, awards a plushie,
     * and then switches to the mini-game panel.
     */
    private void createLocationSelectionPanel() {
        JPanel locationSelectionPanel = new JPanel(new BorderLayout());
        JLabel prompt = new JLabel("Choose a location to explore:", SwingConstants.CENTER);
        prompt.setFont(new Font("Arial", Font.BOLD, 18));
        locationSelectionPanel.add(prompt, BorderLayout.NORTH);

        // Panel for location buttons.
        locationButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(locationButtonPanel);
        locationSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button to manually update locations.
        JButton refreshButton = new JButton("Refresh Locations");
        refreshButton.addActionListener(e -> {
            try {
                allLocations = locationService.getAllLocations();
                populateLocationButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error fetching locations: " + ex.getMessage());
            }
        });
        locationSelectionPanel.add(refreshButton, BorderLayout.SOUTH);

        // When the panel becomes visible, automatically fetch and populate locations.
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

    /**
     * Helper method to dynamically create buttons for each location.
     */
    private void populateLocationButtons() {
        locationButtonPanel.removeAll();
        for (LocationDTO location : allLocations) {
            JButton locButton = new JButton(location.getName());
            locButton.addActionListener((ActionEvent e) -> {
                // Prevent selecting an already completed location.
                if (completedLocations.contains(location.getId())) {
                    JOptionPane.showMessageDialog(this, "This location has already been completed!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        // Simulate moving to the location.
                        locationService.moveToNextLocation(currentHero.getId());
                    } catch (Exception ex) {
                        System.err.println("Error moving to location: " + ex.getMessage());
                    }
                    // Mark as completed and increment plushie count.
                    completedLocations.add(location.getId());
                    plushiesCollected++;

                    outputArea.setText("You have entered " + location.getName() + ".\nPlaying mini-game...");
                    cardLayout.show(mainPanel, "miniGame");
                }
            });
            // Disable the button if the location is completed.
            if (completedLocations.contains(location.getId())) {
                locButton.setEnabled(false);
            }
            locationButtonPanel.add(locButton);
        }
        locationButtonPanel.revalidate();
        locationButtonPanel.repaint();
    }

    /**
     * 6. Mini-Game Simulation Panel:
     *    Simulates a mini-game with two options: win or lose.
     *    - If win: If fewer than 5 plushies, return to the start panel; else go to final boss.
     *    - If lose: Restart the game.
     */
    private void createMiniGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Two buttons for winning or losing the mini-game.
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton winButton = new JButton("Win Mini-Game");
        winButton.addActionListener(e -> {
            // If fewer than 5 plushies, go back to the start panel; otherwise, proceed to the final boss.
            if (plushiesCollected < 5) {
                cardLayout.show(mainPanel, "startPanel");
            } else {
                cardLayout.show(mainPanel, "finalBoss");
            }
        });
        JButton loseButton = new JButton("Lose Mini-Game");
        loseButton.addActionListener(e -> {
            outputArea.setText("You lost the mini-game. Game Over!");
            restartGame();  // Restart if the mini-game is lost.
        });
        buttonPanel.add(winButton);
        buttonPanel.add(loseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "miniGame");
    }

    /**
     * 7. Final Boss Panel:
     *    Simulates a final boss fight with two outcomes:
     *    - Win: Display victory and move to the final congratulations panel.
     *    - Lose: Restart the game.
     */
    private void createFinalBossPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("FINAL BOSS!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        // Two buttons for final boss fight outcomes.
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton winButton = new JButton("Win Final Boss");
        winButton.addActionListener(e -> {
            outputArea.setText("Fighting final boss...\nVictory!");
            cardLayout.show(mainPanel, "finalCongratulations");
        });
        JButton loseButton = new JButton("Lose Final Boss");
        loseButton.addActionListener(e -> {
            outputArea.setText("You lost to the final boss. Game Over!");
            restartGame(); // Restart if the final boss is lost.
        });
        buttonPanel.add(winButton);
        buttonPanel.add(loseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "finalBoss");
    }

    /**
     * 8. Final Congratulations Panel:
     *    Displays a congratulatory message when the adventure is completed.
     */
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

    /**
     * Resets game state and returns to the welcome screen.
     * This method is called when a mini-game or final boss is lost.
     */
    private void restartGame() {
        plushiesCollected = 0;
        completedLocations.clear();
        try {
            allLocations = locationService.getAllLocations();
            populateLocationButtons();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error resetting game: " + ex.getMessage());
        }
        cardLayout.show(mainPanel, "welcome");
    }

    /**
     * Main method: Initializes services and launches the GUI.
     */
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
