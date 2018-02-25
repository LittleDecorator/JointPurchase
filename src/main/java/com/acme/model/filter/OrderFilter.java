package com.acme.model.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class OrderFilter {

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime dateFrom;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime dateTo;
    private String delivery;
    private String status;
    private String subjectId;
    private Integer limit = 15;
    private Integer offset = 0;

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "OrderFilter{" +
                "dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", delivery='" + delivery + '\'' +
                ", status=" + status +
                ", subjectId='" + subjectId + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
