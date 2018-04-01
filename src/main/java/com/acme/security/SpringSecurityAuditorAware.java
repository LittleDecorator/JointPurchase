//package com.acme.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.stereotype.Component;
//
///**
// * Created by vkokurin on 03.07.2015.
// */
//@Component
//public class SpringSecurityAuditorAware implements AuditorAware<String> {
//    @Autowired
//    private SecurityUtils securityUtils;
//
//    @Override
//    public String getCurrentAuditor() {
//        String userName = securityUtils.getCurrentLogin();
//        return (userName != null ? userName : "system");
//    }
//}
