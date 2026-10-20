package by.bsu.lab3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.bsu.lab3.entity.Driver;
import by.bsu.lab3.exept.DAOException;

public class DAODriver extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAODriver.class.getName());

    public static final String SELECT_ALL_DRIVERS =
            "SELECT driver_id, full_name, license_category, phone FROM driver ORDER BY driver_id";
    public static final String SELECT_DRIVER_BY_ID =
            "SELECT driver_id, full_name, license_category, phone FROM driver WHERE driver_id = ?";
    public static final String INSERT_DRIVER =
            "INSERT INTO driver (full_name, license_category, phone) VALUES (?, ?, ?)";
    public static final String DELETE_DRIVER =
            "DELETE FROM driver WHERE driver_id = ?";

    public List<Driver> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_DRIVERS);
             ResultSet rs = statement.executeQuery()) {
            List<Driver> drivers = new ArrayList<>();
            while (rs.next()) {
                drivers.add(map(rs));
            }
            LOGGER.log(Level.FINE, "Loaded {0} drivers", drivers.size());
            return drivers;
        } catch (SQLException e) {
            throw fail("Can't read drivers", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public Driver findById(int driverId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_DRIVER_BY_ID)) {
            statement.setInt(1, driverId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read driver with id = " + driverId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int insert(Driver driver) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_DRIVER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, driver.getFullName());
            statement.setString(2, driver.getLicenseCategory());
            statement.setString(3, driver.getPhone());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Driver was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                driver.setId(id);
                LOGGER.info("Driver created: " + driver);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            throw fail("Can't insert driver " + driver.getFullName(), e);
        } finally {
            releaseConnection(connection);
        }
    }

    public void delete(int driverId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_DRIVER)) {
            statement.setInt(1, driverId);
            if (statement.executeUpdate() == 0) {
                throw fail("Driver with id = " + driverId + " is not found");
            }
            LOGGER.info("Driver " + driverId + " deleted");
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isForeignKeyViolation(e)) {
                throw fail("Driver with id = " + driverId + " is assigned to trips, delete those trips first");
            }
            throw fail("Can't delete driver with id = " + driverId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static Driver map(ResultSet rs) throws SQLException {
        return new Driver(rs.getInt("driver_id"), rs.getString("full_name"),
                rs.getString("license_category"), rs.getString("phone"));
    }
}
