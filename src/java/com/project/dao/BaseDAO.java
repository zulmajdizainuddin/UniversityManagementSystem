package com.project.dao;

import com.project.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseDAO {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    protected void logError(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
