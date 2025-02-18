package com.keyin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import com.keyin.hero.HeroService;
import com.keyin.location.LocationService;
import com.keyin.ui.GameInterface;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ClientApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class, args);

        HeroService heroService = context.getBean(HeroService.class);
        LocationService locationService = context.getBean(LocationService.class);

        GameInterface gameInterface = new GameInterface(heroService, locationService);

        gameInterface.start();
    }
}