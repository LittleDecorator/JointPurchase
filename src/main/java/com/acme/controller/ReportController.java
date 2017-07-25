package com.acme.controller;

import com.acme.model.Item;
import com.acme.model.filter.ItemFilter;
import com.acme.repository.ItemRepository;
import com.acme.repository.specification.ItemSpecifications;
import com.acme.service.ReportService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nikolay on 23.07.17.
 */
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    @Autowired
    ItemRepository itemRepository;

    @RequestMapping(path = "/items/{fileName}", method = RequestMethod.GET)
    public void getItemsReport(ItemFilter filter, HttpServletResponse response, @PathVariable("fileName") String fileName) {

        List<Item> items = itemRepository.findAll(ItemSpecifications.filter(filter));

        Comparator<Item> byCompanyName = (e1, e2) -> e1.getCompany().getName().compareTo(e2.getCompany().getName());
        Comparator<Item> byArticle = (e1, e2) -> e1.getArticle().compareTo(e2.getArticle());
        items = items.stream().sorted(byCompanyName.thenComparing(byArticle)).collect(Collectors.toList());

        HSSFWorkbook workbook = reportService.generateItemsReport(items);
        try {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            workbook.write(outByteStream);
            byte [] outArray = outByteStream.toByteArray();
            response.setContentType("application/ms-excel");
            response.setContentLength(outArray.length);
            response.setHeader("Expires:", "0"); // eliminates browser caching
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            OutputStream outStream = response.getOutputStream();
            outStream.write(outArray);
            outStream.flush();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
