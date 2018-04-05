package com.acme.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "settings")
public class Settings implements Serializable {

    @Id
    private String key;

    private String type;

    @Column(name = "value")
    private String value;

    @Column
    private String description;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "date_add", updatable = false, insertable = false)
    private LocalDateTime dateAdd;

}
