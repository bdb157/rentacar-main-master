package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Controller
public class RentacarController {
    private static final Logger logger = LogManager.getLogger(RentacarController.class);

    @RequestMapping("/")
    public String home(Model model) {
        for(int i = 1; i<=3; i++) {
            model.addAttribute("brandModel" + i, Baza.Cars.getModelBrand(String.valueOf(i)));
            model.addAttribute("price_car" + i, Baza.Cars.getCostCar(String.valueOf(i)) + " zł");
        }

        logger.info("Loaded / mapping");
        return "index";
    }

    @RequestMapping("/registration")
    public String registration() {
        logger.info("Loaded /registration mapping");
        return "registration";
    }

    @PostMapping("/process_registration")
    public String registerUser(
            @RequestParam String pesel,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String address,
            @RequestParam String phone_number,
            @RequestParam String email) {
        boolean result = Baza.Clients.addClient(pesel, first_name, last_name, address, phone_number, email);
        if (result) {
            logger.info("A record was added to the Customers table");
        } else {
            logger.error("Error adding record to Customers table");
        }

        return "redirect:/thanks";
    }

    @RequestMapping("/rent")
    public String rent() {
        logger.info("Loaded /rent mapping");
        return "rent";
    }

    @RequestMapping("/thanks")
    public String thanks() {
        logger.info("Loaded /thanks mapping");
        return "thanks";
    }

    @PostMapping("/process_rent")
    public String rent_car(
            @RequestParam String pesel,
            @RequestParam("cars") String selectedCar,
            @RequestParam String pickup_date,
            @RequestParam String return_date) {

        int daily_price = Baza.Cars.getCostCar(selectedCar);
        if (daily_price >= 0) {
            long cost = daily_price * CountDays(pickup_date, return_date);
            boolean result = Baza.Rentals.addRental(selectedCar, pesel, pickup_date, return_date, (int) cost);
            if (result) {
                logger.info("A record has been added to the Loans table");
            } else {
                logger.error("Error adding record to the Rentals table");
            }
        } else {
            logger.error("Price for car not found");
        }

        return "redirect:/rent";
    }

    @RequestMapping("/accessible")
    public String accessible(Model model) {
        for(int i = 1; i<=6; i++) {
            model.addAttribute("brandModel" + i, Baza.Cars.getModelBrand(String.valueOf(i)));
            model.addAttribute("price_car" + i, Baza.Cars.getCostCar(String.valueOf(i)) + " zł");
        }
        logger.info("Loaded /accessible mapping");
        return "accessible";
    }


    // Metody poniżej
    long CountDays(String rentDate, String returnDate) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime date1 = LocalDate.parse(rentDate, format).atStartOfDay();
        LocalDateTime date2 = LocalDate.parse(returnDate, format).atStartOfDay();
        return Duration.between(date1, date2).toDays();
    }
}
