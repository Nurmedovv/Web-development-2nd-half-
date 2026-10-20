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

import by.bsu.lab3.entity.BodyType;
import by.bsu.lab3.exept.DAOException;

public class DAOBodyType extends DAO {
    private static final Logger LOGGER = Logger.getLogger(DAOBodyType.class.getName());

    public static final String SELECT_ALL_BODY_TYPES =
            "SELECT body_type_id, name FROM body_type ORDER BY body_type_id";
    public static final String SELECT_BODY_TYPE_BY_ID =
            "SELECT body_type_id, name FROM body_type WHERE body_type_id = ?";
    public static final String INSERT_BODY_TYPE =
            "INSERT INTO body_type (name) VALUES (?)";

    public List<BodyType> findAll() throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BODY_TYPES);
             ResultSet rs = statement.executeQuery()) {
            List<BodyType> bodyTypes = new ArrayList<>();
            while (rs.next()) {
                bodyTypes.add(map(rs));
            }
            LOGGER.log(Level.FINE, "Loaded {0} body types", bodyTypes.size());
            return bodyTypes;
        } catch (SQLException e) {
            throw fail("Can't read body types", e);
        } finally {
            releaseConnection(connection);
        }
    }

    public BodyType findById(int bodyTypeId) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BODY_TYPE_BY_ID)) {
            statement.setInt(1, bodyTypeId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw fail("Can't read body type with id = " + bodyTypeId, e);
        } finally {
            releaseConnection(connection);
        }
    }

    public int insert(BodyType bodyType) throws DAOException {
        Connection connection = takeConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BODY_TYPE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, bodyType.getName());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw fail("Body type was not created, no generated key returned");
                }
                int id = keys.getInt(1);
                bodyType.setId(id);
                LOGGER.info("Body type created: " + bodyType);
                return id;
            }
        } catch (DAOException e) {
            throw e;
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                throw fail("Body type '" + bodyType.getName() + "' already exists");
            }
            throw fail("Can't insert body type " + bodyType.getName(), e);
        } finally {
            releaseConnection(connection);
        }
    }

    private static BodyType map(ResultSet rs) throws SQLException {
        return new BodyType(rs.getInt("body_type_id"), rs.getString("name"));
    }
}
