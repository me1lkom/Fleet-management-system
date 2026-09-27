package ru.mirea.project.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

    private static final Dotenv DOTENV = Dotenv.load();

    private static final String URL =
            "jdbc:postgresql://localhost:"
                + DOTENV.get("POSTGRES_PORT")
                + "/"
                + DOTENV.get("POSTGRES_DB");
    private static final String USER = DOTENV.get("POSTGRES_USER");
    private static final String PASSWORD = DOTENV.get("POSTGRES_PASSWORD");

    private DatabaseManager() {}

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
