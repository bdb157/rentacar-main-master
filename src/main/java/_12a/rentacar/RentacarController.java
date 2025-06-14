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
            model.addAttribute("markaModel" + i, Baza.Cars.getModelBrand(String.valueOf(i)));
            model.addAttribute("cenaSamochodu" + i, Baza.Cars.getCostCar(String.valueOf(i)) + " zł");
        }

        logger.info("Loaded / mapping");
        return "index";
    }

    @RequestMapping("/rejestracja")
    public String rejestracja() {
        logger.info("Loaded /registration mapping");
        return "rejestracja";
    }

    @PostMapping("/process_rejestracja")
    public String registerUser(
            @RequestParam String pesel,
            @RequestParam String imie,
            @RequestParam String nazwisko,
            @RequestParam String adres,
            @RequestParam String numer_telefonu,
            @RequestParam String email) {
        boolean result = Baza.Clients.addClient(pesel, imie, nazwisko, adres, numer_telefonu, email);
        if (result) {
            logger.info("A record was added to the Customers table");
        } else {
            logger.error("Error adding record to Customers table");
        }

        return "redirect:/dzieki";
    }

    @RequestMapping("/wypozycz")
    public String wypozycz() {
        logger.info("Loaded /rental mapping");
        return "wypozycz";
    }

    @RequestMapping("/dzieki")
    public String dzieki() {
        logger.info("Loaded /thanks mapping");
        return "dzieki";
    }

    @PostMapping("/process_wypozycz")
    public String wypozycz_samochod(
            @RequestParam String pesel,
            @RequestParam("cars") String selectedCar,
            @RequestParam String pickup_date,
            @RequestParam String return_date) {

        int cenaDzienna = Baza.Cars.getCostCar(selectedCar);
        if (cenaDzienna >= 0) {
            long koszta = cenaDzienna * CountDays(pickup_date, return_date);
            boolean result = Baza.Rentals.addRental(selectedCar, pesel, pickup_date, return_date, (int) koszta);
            if (result) {
                logger.info("A record has been added to the Loans table");
            } else {
                logger.error("Error adding record to the Rentals table");
            }
        } else {
            logger.error("Price for car not found");
        }

        return "redirect:/wypozycz";
    }

    @RequestMapping("/dostepne")
    public String dostepne(Model model) {
        for(int i = 1; i<=6; i++) {
            model.addAttribute("markaModel" + i, Baza.Cars.getModelBrand(String.valueOf(i)));
            model.addAttribute("cenaSamochodu" + i, Baza.Cars.getCostCar(String.valueOf(i)) + " zł");
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
