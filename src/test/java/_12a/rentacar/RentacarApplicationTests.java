package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.BeforeTest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RentacarApplicationTests {
    private final testdatabase database = new testdatabase();
    private static final Logger logger = LogManager.getLogger(RentacarApplicationTests.class);

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testConToDB() {
        if (database.getConnection() != null) {
            logger.info("Connected with database!");
        } else {
            logger.error("Something went wrong!");
        }
        assertNotNull(database.getConnection());
    }


    @Test
    void testDaysCount() {
        logger.info("Expected output: 29 Dni");
        RentacarController controller = new RentacarController();  //yyyy-MM-dd
        long result = controller.CountDays("2023-12-01", "2023-12-30");
        logger.info("Result method: " + result + " days");
        assertEquals(29, result);
    }


    @Test
    public void testRentalMapping() throws Exception {
        MvcResult rent = mockMvc.perform(MockMvcRequestBuilders.get("/rent"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + rent.getResponse().getStatus());
    }

    @Test
    public void testMainPageMapping() throws Exception {
        MvcResult mainpage = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + mainpage.getResponse().getStatus());
    }

    @Test
    public void testThanksMapping() throws Exception {
        MvcResult thanks = mockMvc.perform(MockMvcRequestBuilders.get("/thanks"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + thanks.getResponse().getStatus());
    }

    @Test
    public void testAccessibleMapping() throws Exception {
        MvcResult map = mockMvc.perform(MockMvcRequestBuilders.get("/accessible"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + map.getResponse().getStatus());
    }

    @Test
    public void testregistrationMapping() throws Exception {
        MvcResult registration = mockMvc.perform(MockMvcRequestBuilders.get("/registration"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + registration.getResponse().getStatus());
    }

    @Test
    public void testInsertForCar() {
        database.testTableCars();

        if(database.checkIfRecordExistsCars()) {
            logger.info("Test record added to Cars table!");
        } else {
            logger.error("Test record not added to Cars table!");
        }
        assertTrue(database.checkIfRecordExistsCars());
    }

    @Test
    public void testInsertForClients() {
        boolean result = Baza.Clients.addClient("02312808023", "test", "test", "test", "000000000", "test@example.com");
        if(result) {
            logger.info("Test record added to database");
        } else {
            logger.error("Failed to add test record to database");
        }
        assertTrue(result);
    }

    @Test
    public void testInsertForRental() {
        Baza.Clients.addClient("02312808024", "test", "test", "test", "000000001", "test@example.com");
        boolean result = Baza.Rentals.addRental("2","02312808024", "1999-01-01", "1999-01-02", Integer.parseInt("200"));
        if(result) {
            logger.info("Record added to database!");
        } else {
            logger.error("Failed to add test record to database");
        }
        assertTrue(result);
    }

    @AfterAll
    public static void cleanup() {
        testdatabase.deleteRecordIfExists();
    }
}
