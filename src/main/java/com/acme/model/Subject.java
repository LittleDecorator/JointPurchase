package com.acme.model;

import com.acme.util.StringTemplate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Entity
@Table(name = "subject")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Subject implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    private String address;

    @Column(name = "post_address")
    private Integer postAddress;

    @Transient
    private String fullName;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    @PostLoad
    public void initTransient(){
        StringTemplate stringTemplate = new StringTemplate("{firstName} {middleName} {lastName}");
        Map<String, String> params = ImmutableMap.of("firstName", Strings.nullToEmpty(firstName),"middleName", Strings.nullToEmpty(middleName),"lastName",Strings.nullToEmpty(lastName));
        setFullName(stringTemplate.format(params));
    }


}