package by.bsu.lab3.connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.bsu.lab3.exept.JDBCConnectionException;

public final class ConnectionPool {
    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());
    private static final long TAKE_TIMEOUT_SECONDS = 10;

    private static final ConnectionPool INSTANCE = new ConnectionPool();

    private final JdbcConnector connector = new JdbcConnector();
    private final Set<Connection> allConnections = ConcurrentHashMap.newKeySet();

    private volatile BlockingQueue<Connection> freeConnections;
    private volatile boolean closed;

    private ConnectionPool() {
    }

    public static ConnectionPool getInstance() {
        return INSTANCE;
    }

    public synchronized void init() throws JDBCConnectionException {
        if (closed) {
            throw new JDBCConnectionException("Connection pool is already closed.");
        }
        if (freeConnections != null) {
            return;
        }

        int poolSize = ConfigurationManager.getInstance().getPoolSize();
        BlockingQueue<Connection> queue = new ArrayBlockingQueue<>(poolSize);
        freeConnections = queue;
        try {
            for (int i = 0; i < poolSize; i++) {
                Connection connection = connector.getConnection();
                allConnections.add(connection);
                queue.add(connection);
            }
        } catch (JDBCConnectionException e) {
            LOGGER.log(Level.SEVERE, "Connection pool initialization failed, closing created connections", e);
            try {
                closeAllConnections();
            } catch (JDBCConnectionException closeError) {
                e.addSuppressed(closeError);
            }
            freeConnections = null;
            throw e;
        }
        LOGGER.info("Connection pool initialized, size = " + poolSize);
    }

    public Connection takeConnection() throws JDBCConnectionException {
        if (freeConnections == null) {
            init();
        }
        if (closed) {
            throw new JDBCConnectionException("Connection pool is closed.");
        }
        BlockingQueue<Connection> queue = freeConnections;
        try {
            Connection connection = queue.poll(TAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (connection == null) {
                throw new JDBCConnectionException(
                        "No free connection in the pool within " + TAKE_TIMEOUT_SECONDS + " seconds.");
            }
            if (closed) {
                closeQuietly(connection);
                throw new JDBCConnectionException("Connection pool is closed.");
            }
            if (connection.isClosed()) {
                LOGGER.warning("Pooled connection was closed, creating a replacement");
                allConnections.remove(connection);
                if (closed) {
                    throw new JDBCConnectionException("Connection pool is closed.");
                }
                connection = connector.getConnection();
                allConnections.add(connection);
            }
            return connection;
        } catch (JDBCConnectionException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to take a connection from the pool", e);
            throw new JDBCConnectionException("Failed to take a connection from the pool.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Interrupted while waiting for a free connection", e);
            throw new JDBCConnectionException("Interrupted while waiting for a free connection.", e);
        }
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        if (closed || freeConnections == null) {
            closeQuietly(connection);
            return;
        }

        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
            if (connection.isClosed()) {
                allConnections.remove(connection);
                return;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Broken connection returned to the pool, dropping it", e);
            allConnections.remove(connection);
            closeQuietly(connection);
            return;
        }

        if (!freeConnections.offer(connection)) {
            LOGGER.warning("Connection pool queue is full, dropping the connection");
            allConnections.remove(connection);
            closeQuietly(connection);
        }
    }

    public synchronized void closePool() throws JDBCConnectionException {
        if (closed) {
            return;
        }
        closed = true;
        LOGGER.info("Closing connection pool, total connections = " + allConnections.size());
        closeAllConnections();
    }

    private void closeAllConnections() throws JDBCConnectionException {
        SQLException firstError = null;
        BlockingQueue<Connection> queue = freeConnections;
        if (queue != null) {
            queue.clear();
        }
        for (Connection connection : allConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Can't close pooled connection", e);
                if (firstError == null) {
                    firstError = e;
                }
            }
        }
        allConnections.clear();
        if (firstError != null) {
            throw new JDBCConnectionException("Some pooled connections can't be closed.", firstError);
        }
    }

    private void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "Can't close connection", e);
        }
    }
}
