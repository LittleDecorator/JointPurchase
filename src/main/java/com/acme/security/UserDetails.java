package com.acme.security;

import com.acme.model.Subject;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * @author Y. Tyurin <tyurin23@gmail.com> 10.09.14
 */
public class UserDetails extends User {

    private Subject user;

    public UserDetails(Subject user, Set<GrantedAuthority> grantedAuthorities) {
        super(user.getEmail(), Objects.toString(user.getPassword(), ""), grantedAuthorities);
        this.user = user;
    }

    public Subject getUser() {
        return user;
    }
}
