/*
package com.acme.reports;

import com.acme.gen.domain.PurchaseOrder;
import com.acme.gen.domain.PurchaseOrderExample;
import com.acme.gen.mapper.PurchaseOrderMapper;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import static net.sf.dynamicreports.report.builder.column.Columns.column;
import static net.sf.dynamicreports.report.builder.column.Columns.reportRowNumberColumn;

public class OrderReport {

    @Autowired
    private PurchaseOrderMapper orderMapper;

    private JasperReportBuilder buidReport() throws DRException {
        JasperReportBuilder report = report();

        //add style bold-center
        StyleBuilder boldCenter = stl.style().bold().setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder center = stl.style().setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder borderCenter = stl.style().setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder(stl.pen1Point());
        StyleBuilder columnHeader = stl.style(boldCenter).setBorder(stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY);
        StyleBuilder columnBorder = stl.style().setBorder(stl.pen1Point()).setLeftPadding(mm(2));

        //add title
        TextFieldBuilder<String> title = cmp.text("Dynamic OrderReport");
        title.setStyle(boldCenter);
        report.title(title);

        //add columns
        TextColumnBuilder<String> name = column("Name", "name", type.stringType()).setStyle(columnBorder).setWidth(mm(30));
        TextColumnBuilder<Date> orderDate = column("Order Date", "createOrderDate",type.dateType()).setStyle(columnBorder).setWidth(mm(20));
        TextColumnBuilder<Date> closeDate = column("Close Date","closeOrderDate",type.dateType()).setStyle(columnBorder).setWidth(mm(20));
        TextColumnBuilder<String> status = column("Status","status",type.stringType()).setStyle(columnBorder).setWidth(mm(15));
        TextColumnBuilder<BigDecimal> payment = column("Payment","payment",type.bigDecimalType()).setStyle(columnBorder);

        //total payment column (right)
//        TextColumnBuilder<BigDecimal> totalPayment = payment.multiply(1).setTitle("Total payment").setDataType(type.bigDecimalType());

        //row number
        TextColumnBuilder<Integer> rowNumber = reportRowNumberColumn("No.").setStyle(borderCenter).setWidth(mm(10));

        //add table
        report.addColumn(rowNumber).addColumn(name).addColumn(orderDate).addColumn(closeDate).addColumn(status).addColumn(payment);

        //subtotals
        report.subtotalsAtSummary(sbt.sum(payment));

        //set column header style
        report.setColumnTitleStyle(columnHeader);

        //add datasource
        report.setDataSource(getData());

        //highlight
        report.highlightDetailOddRows();

        return report;
    }

    public void exportReport() throws DRException, FileNotFoundException {
        buidReport().toPdf(new FileOutputStream(new File("/tmp/test.pdf")));
    }

    public void showReport() throws DRException {
        buidReport().show();
    }

    private JRDataSource getData(){
        if(orderMapper!=null){
            return new JRBeanCollectionDataSource(orderMapper.selectByExample(new PurchaseOrderExample()));
        } else {
            List<PurchaseOrder> orders = new ArrayList<>();
            PurchaseOrder order1 = new PurchaseOrder();
            order1.setId("f266904c-ecc7-4b8b-97d6-ef61571cde66");
            order1.setName("test purchase");
            order1.setPersonId("d7651309-e8f4-47d5-a5a7-e4930456eae9");
            order1.setPayment(new BigDecimal(350));
            PurchaseOrder order2 = new PurchaseOrder();
            order2.setId("ad7d2c80-fb59-4968-9862-a10282a5477d");
            order2.setName("another test purchase");
            order2.setPersonId("d7651309-e8f4-47d5-a5a7-e4930456eae9");
            order2.setPayment(new BigDecimal(1350));
            orders.add(order1);
            orders.add(order2);
            return new JRBeanCollectionDataSource(orders);
        }

    }


}
*/
