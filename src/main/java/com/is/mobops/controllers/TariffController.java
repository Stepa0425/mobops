package com.is.mobops.controllers;

import com.is.mobops.models.Tariff;
import com.is.mobops.models.Tariff_Orders;
import com.is.mobops.repos.TariffRepository;
import com.is.mobops.repos.Tariff_OrdersRepository;
import com.is.mobops.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.is.mobops.controllers.MainController.UserID;
import static com.is.mobops.controllers.MainController.isAdmin;


@Controller
public class TariffController {
    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private Tariff_OrdersRepository tariffOrdersRepository;
    @Autowired
    private UserRepository userRepository;

    List<Tariff> processedTariff = new ArrayList<Tariff>();

    @GetMapping("/tariffs")
    public String tariffs(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        processedTariff = tariffRepository.getAllTariffs();
        model.addAttribute("tariff", processedTariff);
        List<Object[]> result = tariffOrdersRepository.countUsersByTariff();
        List<Object[]> processedResult = result.stream()
                .map(arr -> {
                    Long tariffId = (Long) arr[0];
                    Long userCount = (Long) arr[1];
                    if (userCount == null) {
                        userCount = 0L;
                    }
                    return new Object[]{tariffId, userCount};
                })
                .collect(Collectors.toList());
        model.addAttribute("userCounts", processedResult);
        System.out.println(processedResult);
        return "tariff/tariffs-main";
    }

    @GetMapping("/tariffs/add")
    public String tariffsAdd(Model model) {
        return "tariffs-add";
    }

    @Transactional
    @PostMapping("/tariffs/add")
    public String tariffsAdd(@RequestParam String title, @RequestParam double price,
                             @RequestParam int internet, @RequestParam int sms,
                             @RequestParam int minutes, Model model) {
        Tariff tariff = new Tariff(title, price, internet, sms, minutes);
        tariffRepository.save(tariff);
        List<Tariff> tariffs = tariffRepository.getAllTariffs();
        model.addAttribute("tariff", tariffs);
        return "redirect:/tariffs";
    }

    @GetMapping("/tariffs/{id}/edit")
    public String tariffsEdit(@PathVariable(value = "id") long id, Model model) {
        if (!tariffRepository.existsById(id)) return "redirect:/tariffs";
        Tariff tariffs = tariffRepository.getTariffById(id);
        model.addAttribute("tariff", tariffs);
        return "tariff/tariffs-edit";
    }

    @Transactional
    @PostMapping("/tariffs/{id}/edit")
    public String tariffUpdate(@PathVariable(value = "id") long id, @RequestParam("title") String title,
                               @RequestParam("price") double price, @RequestParam("internet") int internet,
                               @RequestParam("sms") int sms, @RequestParam("minutes") int minutes, Model model) {
        Tariff tariffs = tariffRepository.getTariffById(id);
        tariffs.setInternet(internet);
        tariffs.setMinutes(minutes);
        tariffs.setSms(sms);
        tariffs.setTitle(title);
        tariffs.setInternet(internet);
        tariffs.setPrice(price);
        tariffRepository.save(tariffs);
        model.addAttribute("tariff", tariffs);
        return "redirect:/tariffs";
    }

    @Transactional
    @PostMapping("/tariffs/{id}/remove")
    public String tariffDelete(@PathVariable(value = "id") long id, Model model) {
        Optional<Tariff> tariffOptional = tariffRepository.findById(id);
        if (tariffOptional.isPresent()) {
            tariffRepository.deleteTariffById(id);
            return "redirect:/tariffs";
        } else {
            return "redirect:/tariffs";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        processedTariff = tariffRepository.search(query);
        if (processedTariff.isEmpty())
            model.addAttribute("message", "Ничего по вашему запросу не найдено.");
        else
            model.addAttribute("tariff", processedTariff);
        return "tariff/tariffs-main";
    }

    @GetMapping("/search/{sort}/{parameter}")
    public String tariffSort(@PathVariable(value = "sort") String sort, @PathVariable(value = "parameter") String parameter, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Tariff> tariffs = tariffRepository.getAllTariffs();
        if (!processedTariff.isEmpty()) {
            switch (sort) {
                case "esc": {
                    switch (parameter) {
                        case "price":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getPrice));
                            break;
                        case "minutes":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getMinutes));
                            break;
                        case "internet":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getInternet));
                            break;
                        case "sms":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getSms));
                            break;
                    }
                }
                break;
                case "desc": {
                    switch (parameter) {
                        case "price":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getPrice).reversed());
                            break;
                        case "minutes":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getMinutes).reversed());
                            break;
                        case "internet":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getInternet).reversed());
                            break;
                        case "sms":
                            processedTariff.sort(Comparator.comparingDouble(Tariff::getSms).reversed());
                            break;
                    }
                }
                break;

            }
            model.addAttribute("tariff", processedTariff);
        } else {
            switch (sort) {
                case "esc": {
                    switch (parameter) {
                        case "price":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getPrice));
                            break;
                        case "minutes":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getMinutes));
                            break;
                        case "internet":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getInternet));
                            break;
                        case "sms":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getSms));
                            break;
                    }
                }
                break;
                case "desc": {
                    switch (parameter) {
                        case "price":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getPrice).reversed());
                            break;
                        case "minutes":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getMinutes).reversed());
                            break;
                        case "internet":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getInternet).reversed());
                            break;
                        case "sms":
                            tariffs.sort(Comparator.comparingDouble(Tariff::getSms).reversed());
                            break;
                    }
                }
                break;
            }
            model.addAttribute("tariff", tariffs);
            processedTariff = tariffs;
        }
        return "tariff/tariffs-main";
    }

    @PostMapping("/filters")
    public String filtration(@RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, @RequestParam("minSMS") String minSMS,
                             @RequestParam("maxSMS") String maxSMS, @RequestParam("minInternet") String minInternet, @RequestParam("maxInternet") String maxInternet,
                             @RequestParam("minMinutes") String minMinutes, @RequestParam("maxMinutes") String maxMinutes, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        String[] params = {minPrice, maxPrice, minSMS, maxSMS, minInternet, maxInternet, minMinutes, maxMinutes};
        double[] usableParams = new double[8];
        for (int i = 0; i < params.length; i++) {
            if (params[i].isEmpty()) {
                if (i % 2 == 0)
                    params[i] = "0";
                else
                    params[i] = "100000000";
            }
            usableParams[i] = Double.parseDouble(params[i]);
        }
        processedTariff = tariffRepository.filtration(usableParams[0], usableParams[1], usableParams[2], usableParams[3], usableParams[4], usableParams[5], usableParams[6], usableParams[7]);
        if (processedTariff.isEmpty())
            model.addAttribute("message", "Ничего по вашему запросу не найдено.");
        else
            model.addAttribute("tariff", processedTariff);
        return "tariff/tariffs-main";
    }

}

