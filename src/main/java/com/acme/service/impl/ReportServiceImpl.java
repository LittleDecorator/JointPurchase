package com.acme.service.impl;

import com.acme.model.Item;
import com.acme.repository.ItemRepository;
import com.acme.service.ReportService;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nikolay on 23.07.17.
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ItemRepository itemRepository;

    @Override
    public HSSFWorkbook generateItemsReport(List<Item> items) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("items");

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);

        //the following three statements are required only for HSSF
        sheet.setAutobreaks(true);
        printSetup.setFitHeight((short)1);
        printSetup.setFitWidth((short)1);

        // Auto size the column widths - not working, so we set it explicit
        sheet.setColumnWidth(0, 256*6);
        sheet.setColumnWidth(1, 256*30);
        sheet.setColumnWidth(2, 256*20);
        sheet.setColumnWidth(3, 256*50);
        sheet.setColumnWidth(6, 256*10);

        /* стили для ячеек заголовка */
        CellStyle headerStyle = workbook.createCellStyle();//Create style
        Font boldFont = workbook.createFont();//Create font
        boldFont.setFontName(HSSFFont.FONT_ARIAL);
        boldFont.setBold(true);//Make font bold
        boldFont.setFontHeightInPoints((short)10);
        headerStyle.setFont(boldFont);//set it to bold
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle style = workbook.createCellStyle();//Create style
        Font font = workbook.createFont();//Create font
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints((short)10);
        style.setFont(font);//set it to bold
        style.setAlignment(HorizontalAlignment.CENTER);

        int rowNum = 0, colNum = 1;

        String[] headers = {"ID","Компания", "Название", "Артикул", "кол-во", "Цена (руб)"};

        /* Заголовки */
        Row title = sheet.createRow(rowNum++);
        for (String head: headers){
            Cell cell = title.createCell(colNum++);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(head);
        }

        for (Item item: items) {
            colNum = 0;
            Row row = sheet.createRow(rowNum);
            Cell rowNumber = row.createCell(colNum++);
            rowNumber.setCellValue(rowNum++);
            rowNumber.setCellStyle(style);
            Cell id = row.createCell(colNum++);
            id.setCellValue(item.getId());
            Cell company = row.createCell(colNum++);
            company.setCellValue(item.getCompany().getName());
            Cell name = row.createCell(colNum++);
            name.setCellValue(item.getName());
            Cell article = row.createCell(colNum++);
            article.setCellValue(item.getArticle());
            article.setCellStyle(style);
            Cell count = row.createCell(colNum++);
            count.setCellValue(item.getInStock());
            count.setCellStyle(style);
            Cell price = row.createCell(colNum);
            price.setCellValue(item.getPrice());
            price.setCellStyle(style);
        }
        return workbook;
    }

    @Override
    public HSSFWorkbook generateItemsReport() {
        return generateItemsReport(itemRepository.findAll());
    }
}
