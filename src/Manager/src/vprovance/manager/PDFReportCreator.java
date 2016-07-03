/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.manager;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.Database.Transaction;
import com.itextpdf.text.*;
import static com.itextpdf.text.Annotation.URL;
import com.itextpdf.text.pdf.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author DexpUser
 */
public class PDFReportCreator {
    
    static int FONT_SIZE_SMALL = 14;
    static int FONT_SIZE_BIG = 28;
    static int OFFSET = 40;
    
    DBConnection _connection;
    
    public PDFReportCreator(DBConnection connection) {
        _connection = connection;
    }
    
    public void CreateTransactionsReportOnPDF(String filepath) throws Exception {

        Document document = new Document();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        
        BaseFont bf1 = BaseFont.createFont("c:/Windows/Fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font1 = new Font(bf1, FONT_SIZE_BIG);
        Font font2 = new Font(bf1, FONT_SIZE_SMALL);
        Font tableFont = new Font(bf1, 11);
        
        PdfWriter.getInstance(document, 
                new FileOutputStream(filepath));

        document.open();

        // отцентрированный параграф
        Paragraph title = new Paragraph("ОТЧЁТ", font1);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8);
        document.add(title);
        
        // отцентрированный параграф
        Paragraph subtitle = new Paragraph("о перемещениях партий товара", font2);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        // параграф с добавленным чанком текста
        Paragraph date = new Paragraph();
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setFont(font2);
        date.setSpacingAfter(8);
        date.add(new Chunk("Дата составления: " + df.format(new Date())));
        document.add(date);

        PdfPTable t = new PdfPTable(9);
        t.setSpacingAfter(25);
        t.addCell(new PdfPCell(new Phrase("Время", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Тип", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Продукт", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Кол-во", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Ед.", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Отправитель", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Адресат", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Подтверждена", tableFont)));
        t.addCell(new PdfPCell(new Phrase("Время подтверждения", tableFont)));
        
        for(Transaction trans : _connection.GetTransactions()) {
            t.addCell(new PdfPCell(new Phrase(trans.getTime().toString(), tableFont)));
            t.addCell(new PdfPCell(new Phrase(trans.getAction(), tableFont)));
            t.addCell(new PdfPCell(new Phrase(trans.getResource(), tableFont)));
            t.addCell(new PdfPCell(new Phrase(String.valueOf(trans.getCount()), tableFont)));
            t.addCell(new PdfPCell(new Phrase(trans.getMeasure(), tableFont)));
            t.addCell(new PdfPCell(new Phrase(trans.getSubject(), tableFont)));
            t.addCell(new PdfPCell(new Phrase(String.format("%s", trans.getObject()), tableFont)));
            
            String acceptedString = "Нет";
            if(trans.getObject() == null) acceptedString = "";
            else if(trans.getAccepted() == true) acceptedString = "Да";
            t.addCell(new PdfPCell(new Phrase(acceptedString, tableFont)));
            
            String acceptedTimeString = "";
            if(trans.getAcceptedTime() != null) 
                acceptedTimeString = df.format(trans.getAcceptedTime());
            t.addCell(new PdfPCell(new Phrase(acceptedTimeString, tableFont)));
        }
        document.add(t);
        
        document.close();
    }
}