package com.acme.model.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class OrderFilter {

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime dateFrom;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime dateTo;
    private String delivery;
    private String status;
    private String subjectId;

}
