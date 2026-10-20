package by.bsu.lab3.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.bsu.lab3.entity.Car;
import by.bsu.lab3.entity.Driver;
import by.bsu.lab3.entity.Request;
import by.bsu.lab3.entity.Trip;
import by.bsu.lab3.exept.DAOException;

public class DAOTrip extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAOTrip.class.getName());

    private static final String TRIP_SELECT =
            "SELECT t.trip_id, t.request_id, t.driver_id, t.car_id, t.status, t.departure_date, "
                    + "t.completion_date, t.car_condition_after, "
                    + "d.full_name AS driver_name, c.plate_number AS car_plate, "
                    + "ro.city_from AS route_from, ro.city_to AS route_to "
                    + "FROM trip t "
                    + "LEFT JOIN driver d ON d.driver_id = t.driver_id "
                    + "LEFT JOIN car c ON c.car_id = t.car_id "
                    + "LEFT JOIN request rq ON rq.request_id = t.request_id "
                    + "LEFT JOIN route ro ON ro.route_id = rq.route_id ";

    public static final String SELECT_ALL_TRIPS = TRIP_SELECT + "ORDER BY t.trip_id";
    public static final String SELECT_TRIPS_BY_DRIVER = TRIP_SELECT + "WHERE t.driver_id = ? ORDER BY t.trip_id";
    public static final String SELECT_TRIP_BY_ID = TRIP_SELECT + "WHERE t.trip_id = ?";
    public static final String INSERT_TRIP =
            "INSERT INTO trip (request_id, driver_id, car_id, status, departure_date) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_TRIP_DRIVER =
            "UPDATE trip SET driver_id = ? WHERE trip_id = ?";
    public static final String UPDATE_TRIP_CAR =
            "UPDATE trip SET car_id = ? WHERE trip_id = ?";
    public static final String UPDATE_TRIP_COMPLETED =
            "UPDATE trip SET status = ?, completion_date = ?, car_condition_after = ? WHERE trip_id = ?";
    public static final String DELETE_TRIP =
            "DELETE FROM trip WHERE trip_id = ?";

    private final DAODriver driverDao = new DAODriver();
    private final DAOCar carDao = new DAOCar();
    private final DAORequest requestDao = new DAORequest();

    public List<Trip> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_TRIPS);
             ResultSet rs = statement.executeQuery()) {
            List<Trip> trips = readTrips(rs);
            LOGGER.log(Level.FINE, "Loaded {0} trips", trips.size());
            return trips;
        } catch (SQLException e) {
            throw fail("Can't read trips", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public List<Trip> findByDriver(int driverId) throws DAOException {
        Driver driver = driverDao.findById(driverId);
        if (driver == null) {
            throw fail("Driver with id = " + driverId + " is not found");
        }

        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_TRIPS_BY_DRIVER)) {
            statement.setInt(1, driverId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Trip> trips = readTrips(rs);
                LOGGER.info("Trips of driver " + driver.getFullName() + ": " + trips.size());
                return trips;
            }
        } catch (SQLException e) {
            throw fail("Can't read trips of driver with id = " + driverId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public Trip findById(int tripId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_TRIP_BY_ID)) {
            statement.setInt(1, tripId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read trip with id = " + tripId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int create(int requestId, Integer carId, LocalDate departureDate) throws DAOException {
        Request request = requestDao.findById(requestId);
        if (request == null) {
            throw fail("Request with id = " + requestId + " is not found");
        }
        if (carId != null) {
            Car car = carDao.findById(carId);
            checkCarFitsRequest(request, car, carId);
        }

        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TRIP, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, requestId);
            statement.setNull(2, Types.INTEGER);
            if (carId == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, carId);
            }
            statement.setString(4, Trip.STATUS_PLANNED);
            statement.setObject(5, departureDate);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Trip was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                LOGGER.info("Trip created: id = " + id + ", request = " + requestId + ", car = " + carId);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            throw fail("Can't create trip for request with id = " + requestId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public void assignDriver(int tripId, int driverId) throws DAOException {
        Trip trip = requireTrip(tripId);
        requireNotCompleted(trip);

        Driver driver = driverDao.findById(driverId);
        if (driver == null) {
            throw fail("Driver with id = " + driverId + " is not found");
        }

        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_TRIP_DRIVER)) {
            statement.setInt(1, driverId);
            statement.setInt(2, tripId);
            statement.executeUpdate();
            LOGGER.info("Driver " + driver.getFullName() + " assigned to trip " + tripId);
        } catch (SQLException e) {
            throw fail("Can't assign driver to trip with id = " + tripId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public void assignCar(int tripId, int carId) throws DAOException {
        Trip trip = requireTrip(tripId);
        requireNotCompleted(trip);

        Request request = requestDao.findById(trip.getRequestId());
        if (request == null) {
            throw fail("Request with id = " + trip.getRequestId() + " is not found");
        }
        Car car = carDao.findById(carId);
        checkCarFitsRequest(request, car, carId);

        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_TRIP_CAR)) {
            statement.setInt(1, carId);
            statement.setInt(2, tripId);
            statement.executeUpdate();
            LOGGER.info("Car " + car.getPlateNumber() + " assigned to trip " + tripId);
        } catch (SQLException e) {
            throw fail("Can't assign car to trip with id = " + tripId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public void complete(int tripId, String carCondition) throws DAOException {
        Trip trip = requireTrip(tripId);
        requireNotCompleted(trip);
        if (trip.getDriverId() == null) {
            throw fail("Driver is not assigned to trip with id = " + tripId);
        }
        if (trip.getCarId() == null) {
            throw fail("Car is not assigned to trip with id = " + tripId);
        }

        Connection connection = takeConnection();
        boolean failed = true;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement updateTrip = connection.prepareStatement(UPDATE_TRIP_COMPLETED)) {
                updateTrip.setString(1, Trip.STATUS_COMPLETED);
                updateTrip.setObject(2, LocalDate.now());
                updateTrip.setString(3, carCondition);
                updateTrip.setInt(4, tripId);
                if (updateTrip.executeUpdate() == 0) {
                    throw fail("Trip with id = " + tripId + " is not found");
                }
            }
            try (PreparedStatement updateCar = connection.prepareStatement(DAOCar.UPDATE_CAR_STATUS)) {
                updateCar.setString(1, carCondition);
                updateCar.setInt(2, trip.getCarId());
                updateCar.executeUpdate();
            }
            connection.commit();
            failed = false;
            LOGGER.info("Trip " + tripId + " completed, car " + trip.getCarId() + " condition -> " + carCondition);
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            throw fail("Can't complete trip with id = " + tripId, e);
        } finally {
            if (failed) {
                LOGGER.log(Level.WARNING, "Transaction failed, rolling back trip " + tripId);
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Rollback failed for trip " + tripId, e);
                }
            }
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Can't restore autoCommit", e);
            }
            releaseConnection(connection);
        }
    }

    public void delete(int tripId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_TRIP)) {
            statement.setInt(1, tripId);
            if (statement.executeUpdate() == 0) {
                throw fail("Trip with id = " + tripId + " is not found");
            }
            LOGGER.info("Trip " + tripId + " deleted");
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            throw fail("Can't delete trip with id = " + tripId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    private Trip requireTrip(int tripId) throws DAOException {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw fail("Trip with id = " + tripId + " is not found");
        }
        return trip;
    }

    private void requireNotCompleted(Trip trip) throws DAOException {
        if (Trip.STATUS_COMPLETED.equals(trip.getStatus())) {
            throw fail("Trip with id = " + trip.getId() + " is already completed");
        }
    }

    private void checkCarFitsRequest(Request request, Car car, int carId) throws DAOException {
        if (car == null) {
            throw fail("Car with id = " + carId + " is not found");
        }
        if (!car.isServiceable()) {
            throw fail("Car " + car.getPlateNumber() + " is in repair and can't be assigned to a trip");
        }
        if (car.getBodyTypeId() != request.getRequiredBodyTypeId()) {
            throw fail("Car body type '" + car.getBodyTypeName() + "' does not match the request (required '"
                    + request.getRequiredBodyTypeName() + "')");
        }
        if (car.getLoadKg() < request.getRequiredLoadKg()) {
            throw fail("Car load capacity " + car.getLoadKg() + " kg is less than required "
                    + request.getRequiredLoadKg() + " kg");
        }
    }

    private static List<Trip> readTrips(ResultSet rs) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        while (rs.next()) {
            trips.add(map(rs));
        }
        return trips;
    }

    private static Trip map(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setId(rs.getInt("trip_id"));
        trip.setRequestId(rs.getInt("request_id"));
        int driverId = rs.getInt("driver_id");
        trip.setDriverId(rs.wasNull() ? null : driverId);
        int carId = rs.getInt("car_id");
        trip.setCarId(rs.wasNull() ? null : carId);
        trip.setStatus(rs.getString("status"));
        trip.setDepartureDate(rs.getDate("departure_date").toLocalDate());
        Date completionDate = rs.getDate("completion_date");
        trip.setCompletionDate(completionDate == null ? null : completionDate.toLocalDate());
        trip.setCarConditionAfter(rs.getString("car_condition_after"));
        trip.setDriverName(rs.getString("driver_name"));
        trip.setCarPlate(rs.getString("car_plate"));
        trip.setRouteFrom(rs.getString("route_from"));
        trip.setRouteTo(rs.getString("route_to"));
        return trip;
    }
}
