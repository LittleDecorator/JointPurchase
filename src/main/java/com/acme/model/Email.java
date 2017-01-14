//package com.acme.model;
//
//import java.util.Arrays;
//import java.util.Date;
//
//public class Email {
//
//    String id;
//    String from;
//    String to;
//    Date date;
//    byte[] content;
//    String subject;
//    String mime;
//    boolean isNew;
//
//    public Email() {
//    }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public String getTo() {
//        return to;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    public byte[] getContent() {
//        return content;
//    }
//
//    public void setContent(byte[] content) {
//        this.content = content;
//    }
//
//    public String getMime() {
//        return mime;
//    }
//
//    public void setMime(String mime) {
//        this.mime = mime;
//    }
//
//    public boolean isNew() {
//        return isNew;
//    }
//
//    public void setIsNew(boolean isNew) {
//        this.isNew = isNew;
//    }
//
//    public String getSubject() {
//        return subject;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @Override
//    public String toString() {
//        return "Email{" +
//                "id='" + id + '\'' +
//                ", from='" + from + '\'' +
//                ", to='" + to + '\'' +
//                ", date=" + date +
//                ", content=" + Arrays.toString(content) +
//                ", subject='" + subject + '\'' +
//                ", mime='" + mime + '\'' +
//                ", isNew=" + isNew +
//                '}';
//    }
//}
