package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class testdatabase {

    private static final Logger logger = LogManager.getLogger(testdatabase.class);
    static Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static final String DB_URL = props.getProperty("spring.datasource.url");
    private static final String DB_USER = props.getProperty("spring.datasource.username");
    private static final String DB_PASSWORD = props.getProperty("spring.datasource.password");
    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Connected to database");
        } catch (SQLException e) {
            logger.error("Could not connect to database");
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void testTableCars() {
        String insertQuery = "INSERT INTO cars (brand, model, year_of_production, daily_price, availability) VALUES ('Test', 'Test', 1970, 250, 'Nie')";
        try (PreparedStatement insertion = connection.prepareStatement(insertQuery)) {
            insertion.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to add record to table");
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfRecordExistsClients() {
        String selectQuery = "SELECT * FROM clients WHERE pesel = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "02312808023");

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Query execution failed");
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfRecordExistsRental() {
        String selectQuery = "SELECT * FROM rentals WHERE pesel = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "02312808023");

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Query execution failed");
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfRecordExistsCars() {
        String selectQuery = "SELECT * FROM cars WHERE brand = ? AND year_of_production = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "Test");
            preparedStatement.setInt(2, 1970);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Query execution failed");
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecordIfExists() {

        String deleteQuery = "DELETE FROM rentals WHERE pesel = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, "02312808024");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Query execution failed");
            throw new RuntimeException(e);
        }

        deleteQuery = "DELETE FROM clients WHERE pesel = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, "02312808023");
            preparedStatement.executeUpdate();
            preparedStatement.setString(1, "02312808024");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Query execution failed");
            throw new RuntimeException(e);
        }

        deleteQuery = "DELETE FROM cars WHERE brand = ? AND year_of_production = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, "Test");
            preparedStatement.setInt(2, 1970);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Query execution failed");
            throw new RuntimeException(e);
        }

    }
}
