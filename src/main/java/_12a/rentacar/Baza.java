package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import java.sql.*;

public class Baza {
    private static final Logger logger = LogManager.getLogger(Baza.class);
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
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(true);
            logger.info("Connected with database");
        } catch (SQLException e) {
            logger.error("Not connected with database");
            throw new RuntimeException(e);
        }
    }

    static class Clients {
        public static boolean addClient(
                String pesel,
                String first_name,
                String last_name,
                String address,
                String phone_number,
                String email) {
            String insertQuery = "INSERT INTO clients (pesel, first_name, last_name, address, phone_number, email) VALUES ( ?,?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, pesel);
                preparedStatement.setString(2, first_name);
                preparedStatement.setString(3, last_name);
                preparedStatement.setString(4, address);
                preparedStatement.setString(5, phone_number);
                preparedStatement.setString(6, email);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to insert record into database");
                throw new RuntimeException(e);
            }
        }

        public static boolean deleteClient(String pesel) {
            String deleteQuery = "DELETE FROM clients WHERE pesel = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, pesel);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to delete record from database");
                throw new RuntimeException(e);
            }
        }
    }

    static class Rentals {
        public static boolean addRental (
                String selectedCar,
                String pesel,
                String pickup_date,
                String return_date,
                int rental_cost
        ) {

            String insertQuery = "INSERT INTO rentals (carid, pesel, rent_date, return_date, rental_cost) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, Integer.parseInt(selectedCar));
                preparedStatement.setString(2, pesel);

                java.sql.Date sqlPickupDate = java.sql.Date.valueOf(pickup_date);  // pickup_date w formacie "yyyy-MM-dd"
                java.sql.Date sqlReturnDate = java.sql.Date.valueOf(return_date);  // return_date w formacie "yyyy-MM-dd"

                preparedStatement.setDate(3, sqlPickupDate);
                preparedStatement.setDate(4, sqlReturnDate);
                preparedStatement.setInt(5, rental_cost);



                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to insert record into database");
                throw new RuntimeException(e);
            }
        }
        
        public static boolean deleteRental(String id) {
            String deleteQuery = "DELETE FROM rental WHERE rentalid = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, id);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to delete record from database");
                throw new RuntimeException(e);
            }
        }
    }

    static class Cars {
        public static int getCostCar(String selectedCar) {
            String priceCarQuery = "SELECT daily_price FROM cars WHERE carid = ?";
            int daily_price = -1;

            try (PreparedStatement query = connection.prepareStatement(priceCarQuery)) {
                query.setInt(1, Integer.parseInt(selectedCar));
                try (ResultSet resultSet = query.executeQuery()) {
                    if (resultSet.next()) {
                        daily_price = resultSet.getInt("daily_price");
                    }
                }

                return daily_price;
            } catch (SQLException e) {
                logger.error("Failed to retrieve record from database");
                throw new RuntimeException(e);
            }
        }

        public static String getModelBrand(String selectedCar) {
            String query = "SELECT brand, Model FROM cars WHERE carid = ?";
            String brand = null, model = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Integer.parseInt(selectedCar));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        brand = resultSet.getString("brand");
                        model = resultSet.getString("Model");
                    }
                    return brand+" "+model;
                }
            } catch (SQLException e) {
                logger.error("Failed to retrieve record from database");
                throw new RuntimeException(e);
            }
        }
        
        public static boolean addCar(
            String Marka,
            String Model,
            int year_of_production,
            int daily_price,
            String availability
        ) {
            String query = "INSERT INTO cars (carid, brand, Model, year_of_production, daily_price, availability) VALUES (?, ?, ?, ?, ?,?)";

            try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, Marka);
                preparedStatement.setString(2, Model);
                preparedStatement.setInt(3, year_of_production);
                preparedStatement.setInt(4, daily_price);
                preparedStatement.setString(5, availability);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to insert record into database");
                throw new RuntimeException(e);
            }
        }

        public static boolean deleteCar(String id) {
            String deleteQuery = "DELETE FROM cars WHERE carid = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, id);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to delete record from database");
                throw new RuntimeException(e);
            }
        }
    }
}
