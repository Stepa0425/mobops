package com.is.mobops.controllers;

import com.is.mobops.models.Service;
import com.is.mobops.repos.ServiceRepository;
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

import static com.is.mobops.controllers.MainController.UserID;
import static com.is.mobops.controllers.MainController.isAdmin;


@Controller
public class ServiceController {
    @Autowired
    private ServiceRepository serviceRepository;
    List<Service> processedServices = new ArrayList<>();
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/services")
    public String services(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        processedServices = serviceRepository.getAllServices();
        model.addAttribute("service", processedServices);
        return "services/services-main";
    }

    @GetMapping("/searchServices")
    public String search(@RequestParam("query") String query, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        processedServices = serviceRepository.search(query);
        if (processedServices.isEmpty())
            model.addAttribute("message", "Ничего по вашему запросу не найдено.");
        else
            model.addAttribute("service", processedServices);
        return "services/services-main";
    }

    @GetMapping("/searchServices/{sort}/{parameter}")
    public String serviceSort(@PathVariable(value = "sort") String sort, @PathVariable(value = "parameter") String parameter, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Service> services = serviceRepository.getAllServices();
        if (!processedServices.isEmpty()) {
            switch (sort) {
                case "esc": {
                    switch (parameter) {
                        case "price":
                            processedServices.sort(Comparator.comparingDouble(Service::getPrice));
                            break;
                    }
                }
                break;
                case "desc": {
                    switch (parameter) {
                        case "price":
                            processedServices.sort(Comparator.comparingDouble(Service::getPrice).reversed());
                            break;
                    }
                }
                break;

            }
            model.addAttribute("service", processedServices);
        } else {
            switch (sort) {
                case "esc": {
                    switch (parameter) {
                        case "price":
                            services.sort(Comparator.comparingDouble(Service::getPrice));
                            break;
                    }
                }
                break;
                case "desc": {
                    switch (parameter) {
                        case "price":
                            services.sort(Comparator.comparingDouble(Service::getPrice).reversed());
                            break;
                    }
                }
                break;
            }
            model.addAttribute("service", services);
            processedServices = services;
        }
        return "services/services-main";
    }
    @PostMapping("/filtersServices")
    public String filtration(@RequestParam("minPrice") String minPrice, @RequestParam("maxPrice") String maxPrice, Model model) {

        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        String[] params = {minPrice, maxPrice};
        double[] usableParams = new double[2];
        for (int i = 0; i < params.length; i++) {
            if (params[i].isEmpty()){
                if (i % 2 == 0)
                    params[i] = "0";
                else
                    params[i] = "100000";
            }
            usableParams[i] = Double.parseDouble(params[i]);
        }
        processedServices = serviceRepository.filtration(usableParams[0], usableParams[1]);
        if (processedServices.isEmpty())
            model.addAttribute("message", "Ничего по вашему запросу не найдено.");
        else
            model.addAttribute("service", processedServices);
        return "services/services-main";
    }

    @GetMapping("/services/add")
    public String servicesAdd(Model model) {
        return "services-add";
    }

    @Transactional
    @PostMapping("/services/add")
    public String serviceAdd(@RequestParam String title, @RequestParam double price,
                             @RequestParam String description, Model model) {
        serviceRepository.addService(title, price, description);
        List<Service> services = serviceRepository.getAllServices();
        model.addAttribute("service", services);
        return "redirect:/services";
    }
    @GetMapping("/services/{id}/edit")
    public String servicesEdit(@PathVariable(value = "id") long id, Model model) {
        if (!serviceRepository.existsById(id)) return "redirect:/services";
        Service services = serviceRepository.getServiceById(id);
        model.addAttribute("service", services);
        return "services/services-edit";
    }

    @Transactional
    @PostMapping("/services/{id}/edit")
    public String serviceUpdate(@PathVariable(value = "id") long id, @RequestParam String title,
                                @RequestParam double price,@RequestParam String description, Model model) {
        Service services = serviceRepository.getServiceById(id);
        services.setDescription(description);
        services.setPrice(price);
        services.setTitle(title);
        serviceRepository.save(services);
        model.addAttribute("service", services);
        return "redirect:/services";
    }


    

}
