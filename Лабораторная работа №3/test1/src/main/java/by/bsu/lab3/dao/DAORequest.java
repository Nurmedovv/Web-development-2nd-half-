package by.bsu.lab3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.bsu.lab3.entity.Request;
import by.bsu.lab3.exept.DAOException;

public class DAORequest extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAORequest.class.getName());

    private static final String REQUEST_SELECT =
            "SELECT r.request_id, r.customer_id, cu.name AS customer_name, r.route_id, "
                    + "ro.city_from AS route_from, ro.city_to AS route_to, "
                    + "r.required_body_type_id, bt.name AS body_type_name, "
                    + "r.required_load_kg, r.departure_date "
                    + "FROM request r "
                    + "JOIN customer cu ON cu.customer_id = r.customer_id "
                    + "JOIN route ro ON ro.route_id = r.route_id "
                    + "JOIN body_type bt ON bt.body_type_id = r.required_body_type_id ";

    public static final String SELECT_ALL_REQUESTS = REQUEST_SELECT + "ORDER BY r.request_id";
    public static final String SELECT_REQUEST_BY_ID = REQUEST_SELECT + "WHERE r.request_id = ?";
    public static final String INSERT_REQUEST =
            "INSERT INTO request (customer_id, route_id, required_body_type_id, required_load_kg, "
                    + "departure_date) VALUES (?, ?, ?, ?, ?)";

    public List<Request> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_REQUESTS);
             ResultSet rs = statement.executeQuery()) {
            List<Request> requests = new ArrayList<>();
            while (rs.next()) {
                requests.add(map(rs));
            }
            LOGGER.log(Level.FINE, "Loaded {0} requests", requests.size());
            return requests;
        } catch (SQLException e) {
            throw fail("Can't read requests", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public Request findById(int requestId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_REQUEST_BY_ID)) {
            statement.setInt(1, requestId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read request with id = " + requestId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int insert(Request request) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_REQUEST, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, request.getCustomerId());
            statement.setInt(2, request.getRouteId());
            statement.setInt(3, request.getRequiredBodyTypeId());
            statement.setInt(4, request.getRequiredLoadKg());
            statement.setObject(5, request.getDepartureDate());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Request was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                request.setId(id);
                LOGGER.info("Request created: " + request);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isForeignKeyViolation(e)) {
                throw fail("Customer, route or body type of the request is not found, add it first");
            }
            throw fail("Can't insert request of customer with id = " + request.getCustomerId(), e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static Request map(ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setId(rs.getInt("request_id"));
        request.setCustomerId(rs.getInt("customer_id"));
        request.setCustomerName(rs.getString("customer_name"));
        request.setRouteId(rs.getInt("route_id"));
        request.setRouteFrom(rs.getString("route_from"));
        request.setRouteTo(rs.getString("route_to"));
        request.setRequiredBodyTypeId(rs.getInt("required_body_type_id"));
        request.setRequiredBodyTypeName(rs.getString("body_type_name"));
        request.setRequiredLoadKg(rs.getInt("required_load_kg"));
        LocalDate departureDate = rs.getDate("departure_date").toLocalDate();
        request.setDepartureDate(departureDate);
        return request;
    }
}
