package com.mediahub.service;

import com.mediahub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.security.SecureRandom;

/**
 * Au premier démarrage (table users vide) :
 *  - SEED_DEMO_DATA=true  : importe seed.sql (données de l'ancienne version PHP/JSON)
 *  - SEED_DEMO_DATA=false : crée uniquement le compte administrateur défini dans .env
 */
@Component
public class DatabaseSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final UserRepository users;
    private final UserService userService;
    private final DataSource dataSource;
    private final boolean seedDemoData;
    private final String adminEmail;
    private final String adminPassword;

    public DatabaseSeeder(UserRepository users, UserService userService, DataSource dataSource,
                          @Value("${app.seed-demo-data:true}") boolean seedDemoData,
                          @Value("${app.admin.email:admin@mediahub.com}") String adminEmail,
                          @Value("${app.admin.password:}") String adminPassword) {
        this.users = users;
        this.userService = userService;
        this.dataSource = dataSource;
        this.seedDemoData = seedDemoData;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            return;
        }
        if (seedDemoData) {
            new ResourceDatabasePopulator(new ClassPathResource("seed.sql")).execute(dataSource);
            log.info("Base vide : données de démonstration importées depuis seed.sql.");
            return;
        }
        String password = adminPassword;
        boolean generated = password == null || password.isBlank();
        if (generated) {
            password = randomPassword(16);
        }
        userService.register("System", "Admin", adminEmail, password, "admin");
        if (generated) {
            log.warn("Compte administrateur créé : {} / {}  (mot de passe généré, à noter puis à modifier)", adminEmail, password);
        } else {
            log.info("Compte administrateur créé : {}", adminEmail);
        }
    }

    private static String randomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
