package com.is.mobops.controllers;

import com.is.mobops.MailService;
import com.is.mobops.models.*;
import com.is.mobops.repos.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.is.mobops.controllers.MainController.UserID;
import static com.is.mobops.controllers.MainController.isAdmin;

@Controller
public class ServiceOrdersController {
    @Autowired
    Service_OrdersRepository serviceOrdersRepository;
    @Autowired
    Tariff_OrdersRepository tariffOrdersRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    MailService mailService;
    static public long OrderIDForEmail;


    @PostMapping("/connectService/{id}")
    public String connectTariff(@PathVariable(value = "id") long id, Model model) throws DocumentException, IOException {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        User user = userRepository.getUserById(UserID);
        Service service = serviceRepository.getServiceById(id);
        List<Service_Orders> currentServiceOrder = serviceOrdersRepository.getService_OrdersByUser(user);
        if (user.getBalance() > 0) {
//            if (currentTariffOrder != null) {
//                Optional<Tariff_Orders> tariffOrdersOptional = tariffOrdersRepository.findById(currentTariffOrder.getId());
//                if (tariffOrdersOptional.isPresent()) {
//                    tariffOrdersRepository.delete(currentTariffOrder);
//                }
//            }
            boolean isExist = false;
            if (currentServiceOrder != null) {
                for (Service_Orders serviceOrders : currentServiceOrder) {
                    if (serviceOrders.getService().getId() == id) {
                        isExist = true;
                        model.addAttribute("error", "У вас уже подключена такая услуга!");
                        break;
                    }
                }
            }
            if (!isExist) {
                Service_Orders serviceOrders = new Service_Orders(user, service);
                serviceOrdersRepository.save(serviceOrders);
                user.setBalance(user.getBalance() - service.getPrice());
                Payment payment = new Payment(user, -service.getPrice());
                paymentRepository.save(payment);
                userRepository.save(user);
                OrderIDForEmail = serviceOrders.getId();
                byte[] pdfBytes = generateBookingServicePdf();
                mailService.sendPdfByEmail(serviceOrders.getUser().getEmail(), "Оформление дополнительной услуги", "Подробности заказа указаны в PDF-файле:", pdfBytes, "Хорошие новости");
            }
        } else {
            model.addAttribute("error_message", "Ваш баланс отрицательный, пополните его, чтобы подключить тариф!");

        }
        model.addAttribute("user", user);
        List<Service_Orders> serviceOrders = serviceOrdersRepository.getService_OrdersByUser(user);
        Tariff_Orders tariffOrders = tariffOrdersRepository.getTariff_OrdersByUser(user);
        if (tariffOrders == null) {
            model.addAttribute("message", "У вас еще нет подключенных тарифов");
        } else {
            model.addAttribute("tariffConnect", tariffOrders);
            if (serviceOrders == null && serviceOrders.isEmpty()) {
                model.addAttribute("messageService", "У вас еще нет подключенных дополнительных услуг.");
            } else {
                model.addAttribute("serviceConnect", serviceOrders);
            }
        }
        model.addAttribute("user", user);


        return "account";
    }

    public byte[] generateBookingServicePdf() throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setInitialLeading(16);

            // Установка шрифта для кириллицы
            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 14);

            // Открытие документа
            document.open();
            document.add(new Paragraph(userRepository.getUserById(UserID).getSurname() + " " + userRepository.getUserById(UserID).getName() + ", Вы успешно подключили новую дополнительную услугу!", font));
            LocalDate dateOrder = serviceOrdersRepository.getOrderById(OrderIDForEmail).getDate_order();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
            String formattedDate = dateOrder.format(formatter);
            document.add(new Paragraph("Вы подключили " + formattedDate + " новую дополнительную услугу и вам повезло!", font));
            document.add(new Paragraph("Название: " + serviceOrdersRepository.getOrderById(OrderIDForEmail).getService().getTitle(), font));
            document.add(new Paragraph("Стоимость в день: " + serviceOrdersRepository.getOrderById(OrderIDForEmail).getService().getPrice() + " руб", font));
            document.add(new Paragraph("--------------------------------------------------", font));
            document.add(new Paragraph("Отслеживать текущие подключенные тарифы и услуги вы можете в личном кабинете на нашем сайте.", font));
            document.add(new Paragraph("Мы благодарны вашему доверию нам!", font));
            document.add(new Paragraph("С уважением, BlackLink ", font));
            // Закрытие документа
            document.close();
        } catch (
                DocumentException e) {
            e.printStackTrace();
            throw new IOException("Error creating PDF file", e);
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    @Transactional
    @PostMapping("/disconnect/{id}/delete")
    public String serviceDisconnect(@PathVariable(value = "id") long id, Model model) {
//        Optional<Service_Orders> serviceOptional = serviceOrdersRepository.findById(id);
//        if (serviceOptional.isPresent()) {
//            serviceOrdersRepository.deleteById(id);
//            return "redirect:/account";
//        } else {
//            return "redirect:/account";
//        }
        serviceOrdersRepository.delete(id, UserID);
        return "redirect:/account";

    }

}



