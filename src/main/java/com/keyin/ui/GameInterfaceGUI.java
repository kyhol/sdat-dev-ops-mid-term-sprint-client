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

public class GameInterfaceGUI extends JFrame {
    private final HeroService heroService;
    private final LocationService locationService;
    private HeroDTO currentHero;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea outputArea;

    // Instance variable for dynamic location buttons panel
    private JPanel locationButtonPanel;

    // Game state variables
    private int plushiesCollected = 0;
    private List<Long> completedLocations = new ArrayList<>();
    // List that will hold the fetched locations from the API
    private List<LocationDTO> allLocations = new ArrayList<>();

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
        createHeroUpdatePanel();         // Optional update panel after hero creation
        createStartPanel();              // "Press any key to start" panel
        createLocationSelectionPanel();  // Location selection panel (dynamically populated)
        createMiniGamePanel();           // Mini-game simulation panel
        createFinalBossPanel();          // Final boss panel
        createFinalCongratulationsPanel(); // Final congratulations panel

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    // 1. Welcome Panel
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

    // 2. Hero Creation Panel
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
                    // Proceed to optional update panel
                    cardLayout.show(mainPanel, "heroUpdate");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error creating hero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(createButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "heroCreation");
    }

    // 3. Hero Update Panel (optional)
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
                // In a full implementation, you might call an update endpoint here.
            }
            // Fetch locations from the API for the location selection panel
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

    // 4. Start Panel ("Press any key to start")
    private void createStartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Press any key to start your adventure!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);

        // Using a button to simulate a key press.
        JButton pressKeyButton = new JButton("Press any key");
        pressKeyButton.addActionListener(e -> cardLayout.show(mainPanel, "locationSelection"));
        panel.add(pressKeyButton, BorderLayout.SOUTH);

        // Optionally add a KeyListener:
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                cardLayout.show(mainPanel, "locationSelection");
            }
        });
        panel.setFocusable(true);

        mainPanel.add(panel, "startPanel");
    }

    // 5. Location Selection Panel (dynamically populated)
    private void createLocationSelectionPanel() {
        JPanel locationSelectionPanel = new JPanel(new BorderLayout());
        JLabel prompt = new JLabel("Choose a location to explore:", SwingConstants.CENTER);
        prompt.setFont(new Font("Arial", Font.BOLD, 18));
        locationSelectionPanel.add(prompt, BorderLayout.NORTH);

        // Create an instance variable panel for location buttons
        locationButtonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(locationButtonPanel);
        locationSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button to re-fetch locations and update the panel manually
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

        // Add a component listener so that when the panel is shown, it fetches and populates locations.
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

    // Helper method to populate the location buttons dynamically
    private void populateLocationButtons() {
        locationButtonPanel.removeAll();
        for (LocationDTO location : allLocations) {
            JButton locButton = new JButton(location.getName());
            locButton.addActionListener((ActionEvent e) -> {
                if (completedLocations.contains(location.getId())) {
                    JOptionPane.showMessageDialog(this, "This location has already been completed!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        // Call your service to simulate moving to the next location
                        locationService.moveToNextLocation(currentHero.getId());
                    } catch (Exception ex) {
                        System.err.println("Error moving to location: " + ex.getMessage());
                    }
                    completedLocations.add(location.getId());
                    plushiesCollected++;

                    outputArea.setText("You have entered " + location.getName() + ".\nPlaying mini-game...");
                    cardLayout.show(mainPanel, "miniGame");
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

    // 6. Mini-Game Simulation Panel
    private void createMiniGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> {
            // If fewer than 5 plushies, return to the start panel; otherwise, move to final boss.
            if (plushiesCollected < 5) {
                cardLayout.show(mainPanel, "startPanel");
            } else {
                cardLayout.show(mainPanel, "finalBoss");
            }
        });
        panel.add(continueButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "miniGame");
    }

    // 7. Final Boss Panel
    private void createFinalBossPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("FINAL BOSS!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JButton fightButton = new JButton("Fight Final Boss");
        fightButton.addActionListener(e -> {
            // Simulate the final boss fight.
            outputArea.setText("Fighting final boss...\nVictory!");
            cardLayout.show(mainPanel, "finalCongratulations");
        });
        panel.add(fightButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "finalBoss");
    }

    // 8. Final Congratulations Panel
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