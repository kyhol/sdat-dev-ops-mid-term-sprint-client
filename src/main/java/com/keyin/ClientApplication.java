package com.keyin;

import com.keyin.hero.HeroService;
import com.keyin.location.LocationService;
import com.keyin.plushie.PlushieService;
import com.keyin.ui.GameInterfaceGUI;

import javax.swing.SwingUtilities;

public class ClientApplication {
    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";

        HeroService heroService = new HeroService(baseUrl);
        LocationService locationService = new LocationService(baseUrl);
        PlushieService plushieService = new PlushieService(baseUrl);

        SwingUtilities.invokeLater(() -> {
            GameInterfaceGUI gui = new GameInterfaceGUI(heroService, locationService, plushieService);
            gui.setVisible(true);
        });
    }
}
