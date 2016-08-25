package com.acme.controller;

import com.acme.model.Subject;
import com.acme.repository.PurchaseOrderRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/customer")
public class SubjectController{

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    AuthService authService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Subject> getSubjects() {
        return subjectRepository.getAll();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Subject getSubject(@PathVariable("id") String id) {
        return subjectRepository.getById(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/private")
    public Subject getCurrentSubject() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        return subjectRepository.getById(authService.getClaims(servletRequest).getId());
    }

    @RequestMapping(method = RequestMethod.POST)
    public Subject createSubject(@RequestBody Subject subject) {
        subjectRepository.insertSelective(subject);
        return subject;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateSubject(@RequestBody Subject subject) {
        subjectRepository.updateSelectiveById(subject);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteSubject(@PathVariable("id") String id) {
        //delete from orders
        purchaseOrderRepository.deleteBySubjectId(id);
        //delete subject itself
        subjectRepository.deleteById(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getSubjectMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Subject p : subjectRepository.getAll()){
            map = new HashMap<>();
            map.put("id",p.getId());
            map.put("name",p.getFirstName()+ " "+ p.getLastName() + " "+ p.getMiddleName());
            list.add(map);
        }
        return list;
    }

}
