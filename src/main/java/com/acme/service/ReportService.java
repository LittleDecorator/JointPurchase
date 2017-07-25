package com.acme.service;

import com.acme.model.Item;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * Created by nikolay on 23.07.17.
 */
public interface ReportService {

    HSSFWorkbook generateItemsReport();

    HSSFWorkbook generateItemsReport(List<Item> items);
}
