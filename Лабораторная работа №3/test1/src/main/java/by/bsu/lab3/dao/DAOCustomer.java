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

import by.bsu.lab3.entity.Customer;
import by.bsu.lab3.exept.DAOException;

public class DAOCustomer extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAOCustomer.class.getName());

    public static final String SELECT_ALL_CUSTOMERS =
            "SELECT customer_id, name FROM customer ORDER BY customer_id";
    public static final String SELECT_CUSTOMER_BY_ID =
            "SELECT customer_id, name FROM customer WHERE customer_id = ?";
    public static final String INSERT_CUSTOMER =
            "INSERT INTO customer (name) VALUES (?)";

    public List<Customer> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CUSTOMERS);
             ResultSet rs = statement.executeQuery()) {
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(map(rs));
            }
            LOGGER.log(Level.FINE, "Loaded {0} customers", customers.size());
            return customers;
        } catch (SQLException e) {
            throw fail("Can't read customers", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public Customer findById(int customerId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_CUSTOMER_BY_ID)) {
            statement.setInt(1, customerId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read customer with id = " + customerId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int insert(Customer customer) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customer.getName());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Customer was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                customer.setId(id);
                LOGGER.info("Customer created: " + customer);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                throw fail("Customer '" + customer.getName() + "' already exists");
            }
            throw fail("Can't insert customer " + customer.getName(), e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static Customer map(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt("customer_id"), rs.getString("name"));
    }
}
