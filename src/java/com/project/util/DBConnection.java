package com.project.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                logger.warning("db.properties not found on classpath, falling back to defaults");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load db.properties", e);
        }
        URL      = props.getProperty("db.url",      "jdbc:derby://localhost:1527/UniversityManagementDB;create=true");
        USER     = props.getProperty("db.user",     "nbuser");
        PASSWORD = props.getProperty("db.password", "nbuser");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
