package com.is.mobops.controllers;

import com.is.mobops.models.Payment;
import com.is.mobops.repos.PaymentRepository;
import com.is.mobops.repos.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.is.mobops.controllers.MainController.UserID;

@Controller
public class PaymentController {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    UserRepository userRepository;

    public byte[] generateBookingPdf() throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setInitialLeading(16);
//
//            // Установка шрифта для кириллицы
//            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//            Font font = new Font(baseFont, 12);
//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//            // Открытие документа
//            document.open();
//
//            // Добавление информации из объекта Booking в документ
//            List<Payment> paymentList = paymentRepository.getPaymentByUser(userRepository.getUserById(UserID));
//            if(userRepository.getUserById(UserID).getBalance() > 0)
//                document.add(new Paragraph("У вас положительный баланс!", font));
//            else
//                document.add(new Paragraph("Ваш баланс отрицательный! Не забудь его пополнить, пожалуйста!", font));
//            document.add(new Paragraph("Текущий баланс: " + userRepository.getUserById(UserID).getBalance() + " руб", font));
//            // Добавление заголовка
//            document.add(new Paragraph("Ваши платежи", font));
//            document.add(new Paragraph("--------------------------------------------------------------", font));
//
//            // Добавление информации о платежах
//            for (Payment payment : paymentList) {
//                Paragraph paragraph = new Paragraph();
//                paragraph.add("ID платежа: " + payment.getId(), font));
//                paragraph.add("\nДата платежа: " + payment.getDate_order().format(dateFormatter));
//                paragraph.add("\nСумма: " + payment.getSum());
//
//                document.add(paragraph);
//                document.add(new Paragraph("--------------------------------------------------------------", font));
//            }

            // Открытие документа
            document.open();
            List<Payment> paymentList = paymentRepository.getPaymentByUser(userRepository.getUserById(UserID));
            // Установка шрифта для кириллицы
            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12);

            // Создание форматтера для даты
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            Paragraph title = new Paragraph("Ваши платежные операции", font);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ", font));
            // Создание таблицы
            PdfPTable table = new PdfPTable(3); // Задайте нужное количество столбцов
            table.setWidthPercentage(100);

            // Создание шапки таблицы
            PdfPCell cell = new PdfPCell(new Phrase("ID платежа", font));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // Выравнивание по центру
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Цвет фона
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Дата платежа", font));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // Выравнивание по центру
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Цвет фона
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Сумма", font));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // Выравнивание по центру
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Цвет фона
            table.addCell(cell);

            // Добавление информации о платежах
            for (Payment payment : paymentList) {
                cell = new PdfPCell(new Phrase(payment.getId().toString(), font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(payment.getDate_payment().format(dateFormatter), font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(payment.getSum()) + " руб", font));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);

                // Окрашивание строк в зависимости от значения суммы
                if (payment.getSum() > 0) {
                    for (int i = 0; i < 3; i++) {
                        table.getRow(table.getRows().size() - 1).getCells()[i].setBackgroundColor(BaseColor.GREEN);
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        table.getRow(table.getRows().size() - 1).getCells()[i].setBackgroundColor(BaseColor.RED);
                    }
                }
            }

            // Добавление таблицы в документ
            document.add(table);
            document.add(new Paragraph("Текущий баланс: " + userRepository.getUserById(UserID).getBalance() + " руб", font));
            if(userRepository.getUserById(UserID).getBalance() > 0)
               document.add(new Paragraph("У вас положительный баланс!", font));
            else
                document.add(new Paragraph("Ваш баланс отрицательный! Не забудь его пополнить, пожалуйста!", font));

            // Закрытие документа
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new IOException("Error creating PDF file", e);
        }

        return baos.toByteArray();
    }

    @GetMapping("/orderdetailization")
    public ResponseEntity<byte[]> downloadBookingPdf() throws IOException, DocumentException {
        // Создание PDF-файла
        byte[] pdfBytes = generateBookingPdf();
        // Отправка файла в ответе
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "payments.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
