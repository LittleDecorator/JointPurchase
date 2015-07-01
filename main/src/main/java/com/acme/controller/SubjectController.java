package com.acme.controller;

import com.acme.gen.domain.PurchaseOrderExample;
import com.acme.gen.domain.Subject;
import com.acme.gen.domain.SubjectExample;
import com.acme.gen.mapper.PurchaseOrderMapper;
import com.acme.gen.mapper.SubjectMapper;
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
    private SubjectMapper subjectMapper;

    @Autowired
    private PurchaseOrderMapper orderMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<Subject> getSubjects() {
        return subjectMapper.selectByExample(new SubjectExample());
    }


    /*@RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Subject> getCompanyCustomers(@PathVariable("id") String id) {
        System.out.println("company id -> "+ id);

        SubjectExample example = new SubjectExample();
        example.createCriteria().andCompanyIdEqualTo(id);
        return personMapper.selectByExample(example);
    }*/

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Subject getSubject(@PathVariable("id") String id) {
        return subjectMapper.selectByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Subject createSubject(@RequestBody Subject subject) {
        if(subject.getId() != null){
            subjectMapper.updateByPrimaryKeySelective(subject);
        } else {
            subjectMapper.insertSelective(subject);
        }
        return subject;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteSubject(@PathVariable("id") String id) {
        //delete from orders
        PurchaseOrderExample orderExample = new PurchaseOrderExample();
        orderExample.createCriteria().andSubjectIdEqualTo(id);
        orderMapper.deleteByExample(orderExample);
        //delete subject itself
        subjectMapper.deleteByPrimaryKey(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getSubjectMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Subject p : subjectMapper.selectByExample(new SubjectExample())){
            map = new HashMap<>();
            map.put("id",p.getId());
            map.put("name",p.getFirstName()+ " "+ p.getLastName() + " "+ p.getMiddleName());
            list.add(map);
        }
        return list;
    }

}
