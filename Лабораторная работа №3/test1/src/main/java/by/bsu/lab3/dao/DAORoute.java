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

import by.bsu.lab3.entity.Route;
import by.bsu.lab3.exept.DAOException;

public class DAORoute extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAORoute.class.getName());

    public static final String SELECT_ALL_ROUTES =
            "SELECT route_id, city_from, city_to FROM route ORDER BY route_id";
    public static final String SELECT_ROUTE_BY_ID =
            "SELECT route_id, city_from, city_to FROM route WHERE route_id = ?";
    public static final String INSERT_ROUTE =
            "INSERT INTO route (city_from, city_to) VALUES (?, ?)";

    public List<Route> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ROUTES);
             ResultSet rs = statement.executeQuery()) {
            List<Route> routes = new ArrayList<>();
            while (rs.next()) {
                routes.add(map(rs));
            }
            LOGGER.log(Level.FINE, "Loaded {0} routes", routes.size());
            return routes;
        } catch (SQLException e) {
            throw fail("Can't read routes", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public Route findById(int routeId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ROUTE_BY_ID)) {
            statement.setInt(1, routeId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read route with id = " + routeId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int insert(Route route) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_ROUTE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, route.getCityFrom());
            statement.setString(2, route.getCityTo());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Route was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                route.setId(id);
                LOGGER.info("Route created: " + route);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                throw fail("Route " + route.getCityFrom() + " - " + route.getCityTo() + " already exists");
            }
            throw fail("Can't insert route " + route.getCityFrom() + " - " + route.getCityTo(), e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static Route map(ResultSet rs) throws SQLException {
        return new Route(rs.getInt("route_id"), rs.getString("city_from"), rs.getString("city_to"));
    }
}
