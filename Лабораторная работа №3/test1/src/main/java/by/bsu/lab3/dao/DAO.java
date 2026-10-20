package by.bsu.lab3.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.bsu.lab3.connector.ConnectionPool;
import by.bsu.lab3.exept.DAOException;
import by.bsu.lab3.exept.JDBCConnectionException;

public abstract class DAO {
    private static final Logger LOGGER = Logger.getLogger(DAO.class.getName());

    protected final ConnectionPool pool = ConnectionPool.getInstance();

    protected Connection takeConnection() throws DAOException {
        try {
            return pool.takeConnection();
        } catch (JDBCConnectionException e) {
            LOGGER.log(Level.SEVERE, "Can't take connection from the pool", e);
            throw new DAOException("Can't take connection from the pool: " + e.getMessage(), e);
        }
    }

    protected void releaseConnection(Connection connection) {
        pool.releaseConnection(connection);
    }

    protected static DAOException fail(String message, SQLException cause) {
        LOGGER.log(Level.SEVERE, message, cause);
        return new DAOException(message + ": " + cause.getMessage(), cause);
    }

    protected static DAOException fail(String message) {
        LOGGER.log(Level.INFO, message);
        return new DAOException(message);
    }

    // MySQL 1451: на строку есть ссылки в других таблицах
    protected static boolean isForeignKeyViolation(SQLException e) {
        return e.getErrorCode() == 1451;
    }

    // MySQL 1062: дубль уникального ключа
    protected static boolean isDuplicateKey(SQLException e) {
        return e.getErrorCode() == 1062;
    }
}
