package com.acme.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "content")
@Getter
@Setter
public class Content implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "file_name")
    private String fileName;

    private String mime;

    private String type;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "is_instagram")
    private boolean isInstagram;

    @Column(name = "is_profile")
    private boolean isProfile;

    @Column(name = "meta_info")
    //@Enumerated(EnumType.STRING)
    private String metaInfo;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    private String content;

}