package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nikolay on 15.10.17.
 */

@Entity
@Table(name = "subscribers")
public class Subscriber implements BaseModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    private String email;

    @Column(name = "subject_id")
    private String subjectId;

    @Column(name = "active")
    private boolean active;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        if(dateAdd == null){
            this.dateAdd = new Date();
        } else {
            this.dateAdd = dateAdd;
        }
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", active=" + active +
                ", dateAdd=" + dateAdd +
                '}';
    }
}
