package by.bsu.lab3.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.bsu.lab3.exept.JDBCConnectionException;

public class JdbcConnector {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnector.class.getName());

    public Connection getConnection() throws JDBCConnectionException {
        ConfigurationManager cfg = ConfigurationManager.getInstance();
        try {
            Class.forName(cfg.getDriver());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database driver is not on the classpath: " + cfg.getDriver(), e);
            throw new JDBCConnectionException("Can't load database driver " + cfg.getDriver() + ".", e);
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(cfg.getURL(), cfg.getUser(), cfg.getPass());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Can't connect to database " + cfg.getURL(), e);
            throw new JDBCConnectionException("Can't connect to database " + cfg.getURL() + ".", e);
        }

        if (connection == null) {
            throw new JDBCConnectionException("Driver type is not correct in URL " + cfg.getURL() + ".");
        }
        LOGGER.fine("Opened connection to " + cfg.getURL());
        return connection;
    }
}
