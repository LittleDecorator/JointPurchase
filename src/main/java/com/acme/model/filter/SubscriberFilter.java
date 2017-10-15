package com.acme.model.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Created by nikolay on 15.10.17.
 */
public class SubscriberFilter {

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime dateFrom;
    private String mail;
    private Integer limit;
    private Integer offset;

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
