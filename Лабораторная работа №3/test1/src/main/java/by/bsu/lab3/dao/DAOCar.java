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

import by.bsu.lab3.entity.Car;
import by.bsu.lab3.exept.DAOException;

public class DAOCar extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAOCar.class.getName());

    private static final String CAR_SELECT =
            "SELECT c.car_id, c.plate_number, c.model, c.body_type_id, b.name AS body_type_name, "
                    + "c.load_kg, c.status FROM car c "
                    + "JOIN body_type b ON b.body_type_id = c.body_type_id ";

    public static final String SELECT_ALL_CARS = CAR_SELECT + "ORDER BY c.car_id";
    public static final String SELECT_CARS_IN_REPAIR =
            CAR_SELECT + "WHERE c.status = 'REPAIR' ORDER BY c.car_id";
    public static final String SELECT_CAR_BY_ID = CAR_SELECT + "WHERE c.car_id = ?";
    public static final String INSERT_CAR =
            "INSERT INTO car (plate_number, model, body_type_id, load_kg, status) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_CAR_STATUS =
            "UPDATE car SET status = ? WHERE car_id = ?";
    public static final String DELETE_CAR =
            "DELETE FROM car WHERE car_id = ?";

    public List<Car> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CARS);
             ResultSet rs = statement.executeQuery()) {
            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                cars.add(map(rs));
            }
            LOGGER.log(Level.FINE, "Loaded {0} cars", cars.size());
            return cars;
        } catch (SQLException e) {
            throw fail("Can't read cars", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public List<Car> findInRepair() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_CARS_IN_REPAIR);
             ResultSet rs = statement.executeQuery()) {
            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                cars.add(map(rs));
            }
            LOGGER.info("Cars in repair: " + cars.size());
            return cars;
        } catch (SQLException e) {
            throw fail("Can't read cars in repair", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public Car findById(int carId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_CAR_BY_ID)) {
            statement.setInt(1, carId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read car with id = " + carId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int insert(Car car) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CAR, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getPlateNumber());
            statement.setString(2, car.getModel());
            statement.setInt(3, car.getBodyTypeId());
            statement.setInt(4, car.getLoadKg());
            statement.setString(5, car.getStatus());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Car was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                car.setId(id);
                LOGGER.info("Car created: " + car);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isForeignKeyViolation(e)) {
                throw fail("Body type with id = " + car.getBodyTypeId() + " is not found, add it first");
            }
            if (isDuplicateKey(e)) {
                throw fail("Car with plate " + car.getPlateNumber() + " already exists");
            }
            throw fail("Can't insert car " + car.getPlateNumber(), e);
        } finally {
            releaseConnection(connection);
        }
    }

    public void updateStatus(int carId, String status) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CAR_STATUS)) {
            statement.setString(1, status);
            statement.setInt(2, carId);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw fail("Car with id = " + carId + " is not found");
            }
            LOGGER.info("Car " + carId + " status -> " + status);
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            throw fail("Can't update status of car with id = " + carId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public void delete(int carId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_CAR)) {
            statement.setInt(1, carId);
            if (statement.executeUpdate() == 0) {
                throw fail("Car with id = " + carId + " is not found");
            }
            LOGGER.info("Car " + carId + " deleted");
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isForeignKeyViolation(e)) {
                throw fail("Car with id = " + carId + " is used by trips, delete those trips first");
            }
            throw fail("Can't delete car with id = " + carId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static Car map(ResultSet rs) throws SQLException {
        return new Car(rs.getInt("car_id"), rs.getString("plate_number"), rs.getString("model"),
                rs.getInt("body_type_id"), rs.getString("body_type_name"),
                rs.getInt("load_kg"), rs.getString("status"));
    }
}
