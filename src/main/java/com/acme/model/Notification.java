package com.acme.model;

import com.acme.enums.ItemStatus;
import com.acme.enums.NotificationType;
import com.acme.enums.converters.ItemStatusConverter;
import com.acme.enums.converters.NotificationTypeConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nikolay on 07.05.17.
 */

@Entity
@Table(name = "notification")
public class Notification implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "viewed_subject_id")
    private String viewedSubjectId;

    @Column(name = "viewed_date")
    private Date viewDate;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "target_resource")
    private String targetResource;

    @Column(name = "is_root_only")
    private boolean isRootOnly;

    @Convert(converter = NotificationTypeConverter.class)
    private NotificationType type = NotificationType.NORMAL;

    @Transient
    private Subject viewedSubject;

    @Transient
    private boolean isViewed;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    @PostLoad
    public void initTransient(){
        setViewed(viewedSubjectId != null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getViewedSubjectId() {
        return viewedSubjectId;
    }

    public void setViewedSubjectId(String viewedSubjectId) {
        this.viewedSubjectId = viewedSubjectId;
    }

    public Date getViewDate() {
        return viewDate;
    }

    public void setViewDate(Date viewDate) {
        this.viewDate = viewDate;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(String targetResource) {
        this.targetResource = targetResource;
    }

    public boolean getRootOnly() {
        return isRootOnly;
    }

    public void setRootOnly(boolean rootOnly) {
        isRootOnly = rootOnly;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public boolean isRootOnly() {
        return isRootOnly;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Subject getViewedSubject() {
        return viewedSubject;
    }

    public void setViewedSubject(Subject viewedSubject) {
        this.viewedSubject = viewedSubject;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", viewedSubjectId='" + viewedSubjectId + '\'' +
                ", viewDate=" + viewDate +
                ", targetId='" + targetId + '\'' +
                ", targetResource='" + targetResource + '\'' +
                ", isRootOnly=" + isRootOnly +
                ", type=" + type +
                ", viewedSubject=" + viewedSubject +
                ", isViewed=" + isViewed +
                ", dateAdd=" + dateAdd +
                '}';
    }
}
