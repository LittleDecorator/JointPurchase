package com.acme.model.dto;

import com.acme.enums.OrderStatus;
import java.util.Date;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    private String id;
    /*----------- INFO ----------*/
    // order number
    private Long uid;
    // date create
    private Date dateAdd;
    // close date
    private Date closeOrderDate;
    private Integer payment;
    private OrderStatus status;
    private String deliveryId;
    private String deliveryName;
    private String recipientAddress;
    private String comment;

    /*------------ RECEPIENT ---------*/
    private String subjectId;
    private String recipientFname;
    private String recipientLname;
    private String recipientMname;
    private String recipientFullName;
    private String recipientEmail;
    private String recipientPhone;

    /*------ ITEMS ------*/
    private Set<String> itemIds;
}
