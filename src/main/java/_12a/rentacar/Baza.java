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
            logger.info("Connected with database");
        } catch (SQLException e) {
            logger.error("Not connected with database");
            throw new RuntimeException(e);
        }
    }

    static class Clients {
        public static boolean addClient(
                String pesel,
                String imie,
                String nazwisko,
                String adres,
                String numer_telefonu,
                String email) {

            String insertQuery = "INSERT INTO Klienci (pesel, imie, nazwisko, adres, numer_telefonu, email) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, pesel);
                preparedStatement.setString(2, imie);
                preparedStatement.setString(3, nazwisko);
                preparedStatement.setString(4, adres);
                preparedStatement.setString(5, numer_telefonu);
                preparedStatement.setString(6, email);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to insert record into database");
                throw new RuntimeException(e);
            }
        }

        public static boolean deleteClient(String pesel) {
            String deleteQuery = "DELETE FROM Klienci WHERE pesel = ?";

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
                int koszta
        ) {

            String insertQuery = "INSERT INTO Wypozyczenia (SamochodID, pesel, DataWypozyczenia, DataZwrotu, KosztWypozyczenia) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, Integer.parseInt(selectedCar));
                preparedStatement.setString(2, pesel);

                java.sql.Date sqlPickupDate = java.sql.Date.valueOf(pickup_date);  // pickup_date w formacie "yyyy-MM-dd"
                java.sql.Date sqlReturnDate = java.sql.Date.valueOf(return_date);  // return_date w formacie "yyyy-MM-dd"

                preparedStatement.setDate(3, sqlPickupDate);
                preparedStatement.setDate(4, sqlReturnDate);
                preparedStatement.setInt(5, koszta);



                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to insert record into database");
                throw new RuntimeException(e);
            }
        }
        
        public static boolean deleteRental(String id) {
            String deleteQuery = "DELETE FROM Wypozyczenia WHERE WypozyczenieID = ?";

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
            String priceCarQuery = "SELECT CenaDzienna FROM Samochody WHERE SamochodID = ?";
            int cenaDzienna = -1;

            try (PreparedStatement query = connection.prepareStatement(priceCarQuery)) {
                query.setInt(1, Integer.parseInt(selectedCar));
                try (ResultSet resultSet = query.executeQuery()) {
                    if (resultSet.next()) {
                        cenaDzienna = resultSet.getInt("cenaDzienna");
                    }
                }

                return cenaDzienna;
            } catch (SQLException e) {
                logger.error("Failed to retrieve record from database");
                throw new RuntimeException(e);
            }
        }

        public static String getModelBrand(String selectedCar) {
            String query = "SELECT Marka, Model FROM Samochody WHERE SamochodID = ?";
            String brand = null, model = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, Integer.parseInt(selectedCar));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        brand = resultSet.getString("Marka");
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
            int RokProdukcji,
            int CenaDzienna,
            String Dostepnosc
        ) {
            String query = "INSERT INTO Samochody (SamochodID, Marka, Model, RokProdukcji, CenaDzienna, Dostepnosc) VALUES ('?', '?', '?', '?', '?')";

            try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, Marka);
                preparedStatement.setString(2, Model);
                preparedStatement.setInt(3, RokProdukcji);
                preparedStatement.setInt(4, CenaDzienna);
                preparedStatement.setString(5, Dostepnosc);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Failed to insert record into database");
                throw new RuntimeException(e);
            }
        }

        public static boolean deleteCar(String id) {
            String deleteQuery = "DELETE FROM Samochody WHERE SamochodID = ?";

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
