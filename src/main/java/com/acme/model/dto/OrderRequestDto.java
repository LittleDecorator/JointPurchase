package com.acme.model.dto;

import com.acme.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class OrderRequestDto {

    private String subjectId;
    private String recipientFname;
    private String recipientLname;
    @Nullable
    private String recipientMname;
    private String recipientEmail;
    private String recipientPhone;
    @Nullable
    private String recipientAddress;
    @Nullable
    private String comment;
    @Nullable
    private String postAddress;
    private String delivery;
    private Integer payment;
    @Nullable
    private OrderStatus status;

}
