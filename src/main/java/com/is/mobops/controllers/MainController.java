package com.is.mobops.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.is.mobops.MailService;
import com.is.mobops.models.*;
import com.is.mobops.repos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TariffRepository tariffRepository;
    @Autowired
    private Tariff_OrdersRepository tariffOrdersRepository;
    @Autowired
    private Service_OrdersRepository serviceOrdersRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MailService mailService;
    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    public static final String SESSION_ID_KEY = "sessionId";
    public static Long UserID;
    private static int CODE;
    public static boolean isAdmin;
    public static String eMail;

    @GetMapping("/")
    public String home(HttpServletResponse response, Model model) {
     //   Хешерование паролей
//        List<User> users = userRepository.getAllUsers();
//        for (User user : users){
//            String password = user.getPassword();
//            String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt());
//            user.setPassword(hashedPassword);
//            userRepository.save(user);
//        }
//        ObjectMapper objectMapper = new ObjectMapper();
//       List<User> data = (List<User>) userRepository.getAllUsers(); // Полученные данные из таблицы
//        String json = objectMapper.writeValueAsString(data);
//        String filePath = "src/main/resources/users.json";
//        try (FileWriter fileWriter = new FileWriter(filePath)) {
//            fileWriter.write(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (UserID == null)
            model.addAttribute("userId", 0);

        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "home";
    }

    @GetMapping("/bib1")
    public void showImage1(HttpServletResponse response) throws IOException {
        String imagePath = "src/main/resources/ymnibibizian.jpg"; // Путь к вашему изображению

        File imageFile = new File(imagePath);
        FileInputStream inputStream = new FileInputStream(imageFile);

        // Устанавливаем тип содержимого (MIME type) в ответе
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        // Копируем содержимое файла в ответ
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();

        inputStream.close();
    }
    @GetMapping("/bib2")
    public void showImage2(HttpServletResponse response) throws IOException {
        String imagePath = "src/main/resources/comfort.jpg"; // Путь к вашему изображению

        File imageFile = new File(imagePath);
        FileInputStream inputStream = new FileInputStream(imageFile);

        // Устанавливаем тип содержимого (MIME type) в ответе
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        // Копируем содержимое файла в ответ
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();

        inputStream.close();
    }
    @GetMapping("/bib3")
    public void showImage3(HttpServletResponse response) throws IOException {
        String imagePath = "src/main/resources/bib.jpg"; // Путь к вашему изображению

        File imageFile = new File(imagePath);
        FileInputStream inputStream = new FileInputStream(imageFile);

        // Устанавливаем тип содержимого (MIME type) в ответе
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        // Копируем содержимое файла в ответ
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();

        inputStream.close();
    }
    @GetMapping("/login")
    public String authorization(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "login";
    }

    @Transactional
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletRequest request, Model model) {
        if (userRepository.existsByEmail(email)) {
            String storedPasswordHash = userRepository.getPasswordUserByEmail(email);
            if (BCrypt.checkpw(password, storedPasswordHash)) {
                String userId = String.valueOf(userRepository.getUsersIdByEmail(email));
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_ID_KEY, userId);
                isAdmin = userRepository.isAdminById(userRepository.getUsersIdByEmail(email));
                MDC.put(SESSION_ID_KEY, userId);
                UserID = Long.valueOf(userId);
                log.info("Пользователь {} вошел в систему ", userId);
                return "redirect:/";
            } else {
                model.addAttribute("error_message", "Неверный пароль!");
                return "login";
            }
        } else {
            model.addAttribute("error_message", "Пользователя с данной почтой нет!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute(SESSION_ID_KEY);
        if (userId != null) {
            session.invalidate();
            isAdmin = false;
            UserID = null;
            log.info("Пользователь {} вышел из системы", userId);
        }
        return "redirect:/login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "registration";
    }

    @Transactional
    @PostMapping("/registration")
    public String addNewUser(@RequestParam String name, @RequestParam String surname, @RequestParam String
            phone, @RequestParam String email, @RequestParam String address, @RequestParam String password, Model model) {
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error_message", "Пользователь с такой почтой уже зарегистрирован");
            return "redirect:/registration";
        } else {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.addUser(name, surname, phone, email, hashedPassword, address);
            if (password.equals("admin")) userRepository.setTrueAdminByEmail(email);
            return "redirect:/";
        }
    }

    @GetMapping("/verification")
    public String verification(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "passwordRecovery/verification";
    }

    @PostMapping("/verification")
    public String sendCode(@RequestParam("email") String email, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        Random generateCode = new Random();
        CODE = generateCode.nextInt(1000, 9999);
        UserID = userRepository.getUsersIdByEmail(email);
        if (UserID == null) {
            model.addAttribute("error_message", "Такая почта не зарегистрирована!");
            return "passwordRecovery/verification";
        } else {
            String subject = "Восстановление пароля";
            String message = "Ваш код для восстановления: " + CODE;
            mailService.sendEmail(email, subject, message);
            eMail = email;
            UserID = null;
            return "passwordRecovery/check-email";
        }
    }

    @GetMapping("/check-email")
    public String checkEmail(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "passwordRecovery/check-email";
    }

    @PostMapping("/check-email")
    public String checkCode(@RequestParam("code") double code, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        if (code == CODE) {
            return "edit-password";
        } else
            model.addAttribute("error_message", "Invalid code");
        return "redirect:/check-email";
    }

    @GetMapping("/edit-password")
    public String editPassword(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "passwordRecovery/edit-password";
    }

    @PostMapping("/edit-password")
    public String setCode(@RequestParam("password") String password, @RequestParam("password2") String password2, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        if (password.equals(password2)) {
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.setPasswordById(userRepository.getUsersIdByEmail(eMail), hashPassword);
            UserID = null;
            return "login";
        } else
            model.addAttribute("error_message", "Пароли не совпадают!");
        return "passwordRecovery/edit-password";
    }

    @GetMapping("/account")
    public String account(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);

        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        User user = userRepository.getUserById(UserID);
        model.addAttribute("user", user);
        Tariff_Orders tariffOrders = tariffOrdersRepository.getTariff_OrdersByUser(user);
        List<Service_Orders> serviceOrders = serviceOrdersRepository.getService_OrdersByUser(user);
        if (tariffOrders == null) {
            model.addAttribute("message", "У вас пока нет подключенных тарифов");
        } else {
            model.addAttribute("tariffConnect", tariffOrders);
            if (serviceOrders == null && serviceOrders.isEmpty()) {
                model.addAttribute("messageService", "У вас еще нет подключенных дополнительных услуг.");
            } else {
                model.addAttribute("serviceConnect", serviceOrders);
            }
        }

        return "account";
    }

    @Transactional
    @PostMapping("/account")
    public String editAccount(@RequestParam("name") String name, @RequestParam("surname") String surname, @RequestParam String
            phone, @RequestParam String email, @RequestParam String address, Model model) {
        userRepository.setDataById(UserID, name, surname, phone, email, address);
        User user = userRepository.getUserById(UserID);
        model.addAttribute("user", user);

        return "redirect:/account";
    }


    @GetMapping("/users")
    public String users(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);

        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<User> user = userRepository.getAllUsers();
        model.addAttribute("user", user);
        return "users";
    }

    @PostMapping("/balance/top-up")
    public String topup(@RequestParam("amount") double amount, Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);
        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        User user = userRepository.getUserById(UserID);
        user.setBalance(user.getBalance() + amount);
        Payment payment = new Payment(user, amount);
        paymentRepository.save(payment);
        userRepository.save(user);
        return "redirect:/account";
    }

    @PostMapping("/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Tariff> data = objectMapper.readValue(file.getBytes(), new TypeReference<>() {
            });
            tariffRepository.saveAll(data);
            System.out.println("Данные успешно сохранены в базу данных.");
            model.addAttribute("success", "Файл успешно загружен и данные сохранены.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ошибка при загрузке файла");
        }
        if (UserID == null)
            model.addAttribute("userId", 0);

        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<User> user = userRepository.getAllUsers();
        model.addAttribute("user", user);
        List<Tariff> tariffs = tariffRepository.getAllTariffs();
        List<Service> services = serviceRepository.getAllServices();
        List<Tariff_Orders> ordersTariffs = tariffOrdersRepository.getAllTariffsOrders();
        List<Service_Orders> orderServices = serviceOrdersRepository.getAllServiceOrders();
        model.addAttribute("tariffs", tariffs);
        model.addAttribute("services", services);
        model.addAttribute("ordersS", orderServices);
        model.addAttribute("ordersT", ordersTariffs);
        return "analysis";
    }

    @GetMapping("/export")
    public String exportData(HttpServletResponse response, Model model) {
        try {
            List<Tariff> tariffs = tariffRepository.getAllTariffs();

            // Создаем ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Настраиваем ObjectMapper для сериализации даты и времени в формат ISO8601
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Устанавливаем заголовки для ответа
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=\"tariffs.json\"");

            // Преобразуем данные в JSON и записываем в выходной поток
            objectMapper.writeValue(response.getOutputStream(), tariffs);
            model.addAttribute("success", "Данные экспортированы успешно");
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки
        }
        if (UserID == null)
            model.addAttribute("userId", 0);

        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<User> user = userRepository.getAllUsers();
        model.addAttribute("user", user);
        return "analysis";
    }

    @GetMapping("/analysis")
    public String analyse(Model model) {
        if (UserID == null)
            model.addAttribute("userId", 0);

        else {
            model.addAttribute("userId", 1);
            model.addAttribute("isAdmin", isAdmin);
        }
        List<Tariff> tariffs = tariffRepository.getAllTariffs();
        List<Service> services = serviceRepository.getAllServices();
        List<Tariff_Orders> ordersTariffs = tariffOrdersRepository.getAllTariffsOrders();
        List<Service_Orders> orderServices = serviceOrdersRepository.getAllServiceOrders();
        model.addAttribute("tariffs", tariffs);
        model.addAttribute("services", services);
        model.addAttribute("ordersS", orderServices);
        model.addAttribute("ordersT", ordersTariffs);
        return "analysis";
    }

    @GetMapping("/exportinexcel")
    public void exportOrdersToExcel(HttpServletResponse response) {
        // Set the response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ordersTariffs.xlsx");

        // Create a workbook and a sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        // Create the header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Идентификатор подлкючения");
        headerRow.createCell(1).setCellValue("Название тарифа");
        headerRow.createCell(2).setCellValue("Стоимость тарифа, бел. руб./месяц");
        headerRow.createCell(3).setCellValue("Пользователь");
        headerRow.createCell(4).setCellValue("Дата заказа");

        // Retrieve the data from the database (assuming you have a service or repository to fetch the data)
        List<Tariff_Orders> ordersList = tariffOrdersRepository.getAllTariffsOrders(); // Replace with your actual code to fetch orders

        // Create the data rows
        int rowNum = 1;
        CellStyle dateCellStyle = workbook.createCellStyle();
        short dateFormat = workbook.createDataFormat().getFormat("dd.MM.yyyy"); // Customize the date format as per your needs
        dateCellStyle.setDataFormat(dateFormat);
        for (Tariff_Orders order : ordersList) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(order.getId());
            dataRow.createCell(1).setCellValue(order.getTariff().getTitle());
            dataRow.createCell(2).setCellValue(order.getTariff().getPrice());
            dataRow.createCell(3).setCellValue(order.getUser().getName() + " " + order.getUser().getSurname());
            Cell dateCell = dataRow.createCell(4);
            dateCell.setCellValue(order.getDate_order());
            dateCell.setCellStyle(dateCellStyle);
        }

        // Write the workbook to the response output stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}