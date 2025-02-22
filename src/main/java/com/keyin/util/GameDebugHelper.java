package com.keyin.util;

import java.awt.Component;
import java.util.Arrays;
import javax.swing.JPanel;

/**
 * A utility class to help debug issues with the game UI and flow.
 * Add this to your project and use it to troubleshoot panel navigation.
 */
public class GameDebugHelper {
    private static boolean DEBUG_ENABLED = true;

    /**
     * Logs a message with the current method name if debugging is enabled
     */
    public static void log(String message) {
        if (!DEBUG_ENABLED) return;

        // Get the calling method name
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callerMethod = stackTrace.length > 2 ? stackTrace[2].getMethodName() : "unknown";
        String callerClass = stackTrace.length > 2 ? stackTrace[2].getClassName() : "unknown";

        System.out.println("[DEBUG] " + callerClass + "." + callerMethod + "(): " + message);
    }

    /**
     * Logs a list of all panels in a parent panel (useful for card layout debugging)
     */
    public static void logPanels(JPanel parentPanel) {
        if (!DEBUG_ENABLED) return;

        System.out.println("[DEBUG] Available panels in " + parentPanel.getName() + ":");
        Component[] components = parentPanel.getComponents();
        Arrays.stream(components)
                .filter(c -> c instanceof JPanel)
                .map(c -> (JPanel)c)
                .forEach(p -> System.out.println("   - " + (p.getName() != null ? p.getName() : "unnamed") +
                        " [visible=" + p.isVisible() + "]"));
    }

    /**
     * Logs information about an AbstractMiniGame
     */
    public static void logMiniGame(com.keyin.minigame.AbstractMiniGame game) {
        if (!DEBUG_ENABLED) return;

        System.out.println("[DEBUG] MiniGame Info:");
        System.out.println("   - Class: " + game.getClass().getSimpleName());
        System.out.println("   - Location ID: " + game.getLocationId());
        System.out.println("   - Hero ID: " + game.getHeroId());

        // Check if panels are properly connected
        boolean hasDialogBox = game.getDialogBox() != null;
        boolean hasArtPanel = game.getArtPanel() != null;

        System.out.println("   - Has DialogBox: " + hasDialogBox);
        System.out.println("   - Has ArtPanel: " + hasArtPanel);
    }

    /**
     * Enables or disables debug logging
     */
    public static void setDebugEnabled(boolean enabled) {
        DEBUG_ENABLED = enabled;
        System.out.println("[DEBUG] Debug logging " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Logs an exception with stack trace
     */
    public static void logException(Exception e) {
        if (!DEBUG_ENABLED) return;

        System.err.println("[DEBUG] Exception: " + e.getMessage());
        e.printStackTrace();
    }
}