//package com.acme.security;
//
//import org.jetbrains.annotations.Nullable;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import ru.mdimension.mda.auth.domain.security.User;
//import ru.mdimension.mda.auth.repository.security.UserRepository;
//
///**
// * Created by vkokurin on 05.03.2015.
// *
// */
//@Service
//public class SecurityUtils {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Nullable
//    @Transactional(propagation = Propagation.MANDATORY)
//    public User getCurrentUser() {
//        Object principal = getPrincipal();
//        if (principal == null)
//            return null;
//        if (principal instanceof ru.mdimension.mda.auth.security.UserDetails) {
//            return ((ru.mdimension.mda.auth.security.UserDetails) principal).getUser();
//        } else {
//            return userRepository.findOneByLoginIgnoreCase(getCurrentLogin());
//        }
//    }
//
//    @Nullable
//    public String getCurrentLogin() {
//        Object principal = getPrincipal();
//        if (principal == null)
//            return null;
//        if (principal instanceof UserDetails) {
//            return ((UserDetails) principal).getUsername();
//        } else if (principal instanceof String) {
//            return (String) principal;
//        }
//        else return null;
//    }
//
//    public boolean isUserInRole(String role) {
//        return isUserInAnyRole(role);
//    }
//
//    public boolean isUserInAnyRole(String... roles) {
//        Object principal = getPrincipal();
//        if (principal == null)
//            return false;
//        if(principal instanceof UserDetails) {
//            UserDetails springSecurityUser = (UserDetails) principal;
//            boolean inRole = false;
//            for(String role : roles){
//                if (springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(role)))
//                    return true;
//            }
//        }
//        return false;
//    }
//
//    @Nullable
//    private Object getPrincipal() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null)
//            return null;
//        return authentication.getPrincipal();
//    }
//}
