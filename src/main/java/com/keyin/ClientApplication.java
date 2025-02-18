package com.keyin;

import com.keyin.hero.HeroService;
import com.keyin.location.LocationService;
import com.keyin.ui.GameInterfaceGUI;

import javax.swing.SwingUtilities;

public class ClientApplication {
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
