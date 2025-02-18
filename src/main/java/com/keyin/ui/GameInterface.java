package com.keyin.ui;

import com.keyin.hero.HeroDTO;
import com.keyin.hero.HeroService;
import com.keyin.location.LocationDTO;
import com.keyin.location.LocationService;
import java.util.Scanner;
import java.util.List;

public class GameInterface {
    private final HeroService heroService;
    private final LocationService locationService;
    private final Scanner scanner;
    private boolean gameRunning;
    private HeroDTO currentHero;

    public GameInterface(HeroService heroService, LocationService locationService) {
        this.heroService = heroService;
        this.locationService = locationService;
        this.scanner = new Scanner(System.in);
        this.gameRunning = true;
    }

    public void start() {
        displayWelcome();
        waitForNewGame();
        if (createHero()) {
            mainGameMenu();
        }
    }

    private void displayWelcome() {
        System.out.println("===============================");
        System.out.println("       RPG Adventure");
        System.out.println("===============================");
        System.out.println("Press Enter to start new game...");
    }

    private void waitForNewGame() {
        scanner.nextLine();
    }

    private boolean createHero() {
        System.out.print("Enter your hero's name: ");
        String name = scanner.nextLine();
        try {
            currentHero = heroService.createHero(name);
            System.out.println("\nHero " + name + " created! Your journey begins...");
            System.out.println("Welcome " + name + "! You find yourself in a mysterious world...\n");
            return true;
        } catch (Exception e) {
            System.out.println("Error creating hero: " + e.getMessage());
            return false;
        }
    }

    private void mainGameMenu() {
        while (gameRunning) {
            System.out.println("\nAvailable Actions:");
            System.out.println("1. View areas");
            System.out.println("2. View plushies");
            System.out.println("3. View hero status");
            System.out.println("4. Quit");
            System.out.print("> ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewLocations();
                    break;
                case "2":
                    System.out.println("Plushies feature coming soon!");
                    break;
                case "3":
                    viewHeroStatus();
                    break;
                case "4":
                    quit();
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private void viewLocations() {
        try {
            List<LocationDTO> locations = locationService.getAllLocations();
            if (locations.isEmpty()) {
                System.out.println("\nNo locations found!");
                return;
            }

            System.out.println("\nAvailable Locations:");
            System.out.println("-------------------");
            for (LocationDTO location : locations) {
                System.out.println(location.getName() + ":");
                System.out.println("  " + location.getDescription());
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error fetching locations: " + e.getMessage());
        }
    }

    private void viewHeroStatus() {
        try {
            HeroDTO hero = heroService.getCurrentHero();
            if (hero == null) {
                System.out.println("Error: Hero information not available");
                return;
            }

            System.out.println("\nHero Status:");
            System.out.println("------------");
            System.out.println("Name: " + hero.getName());
            System.out.println("Created: " + hero.getCreatedAt());
        } catch (Exception e) {
            System.out.println("Error fetching hero status: " + e.getMessage());
        }
    }

    private void quit() {
        System.out.println("Thanks for playing! Goodbye!");
        gameRunning = false;
        System.exit(0);
    }
}