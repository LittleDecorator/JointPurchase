package com.acme.controller;

import com.acme.model.Subject;
import com.acme.repository.PurchaseOrderRepository;
import com.acme.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.GET)
    public List<Subject> getSubjects() {
        return subjectRepository.getAll();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Subject getSubject(@PathVariable("id") String id) {
        return subjectRepository.getById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Subject createSubject(@RequestBody Subject subject) {
        if(subject.getId() != null){
            subjectRepository.updateSelectiveById(subject);
        } else {
            subjectRepository.insertSelective(subject);
        }
        return subject;
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
