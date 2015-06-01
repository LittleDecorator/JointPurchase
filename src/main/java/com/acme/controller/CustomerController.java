package com.acme.controller;

import com.acme.gen.domain.CompanyEmployersExample;
import com.acme.gen.domain.Person;
import com.acme.gen.domain.PersonExample;
import com.acme.gen.domain.PurchaseOrderExample;
import com.acme.gen.mapper.CompanyEmployersMapper;
import com.acme.gen.mapper.PersonMapper;
import com.acme.gen.mapper.PurchaseOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/customer")
public class CustomerController{

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PurchaseOrderMapper orderMapper;

    @Autowired
    private CompanyEmployersMapper employersMapper;

    
    @RequestMapping(method = RequestMethod.GET)
    public List<Person> getCustomers() {
        return personMapper.selectByExample(new PersonExample());
    }


    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Person> getCompanyCustomers(@PathVariable("id") String id) {
        System.out.println("company id -> "+ id);

        PersonExample example = new PersonExample();
        example.createCriteria().andCompanyIdEqualTo(id);
        return personMapper.selectByExample(example);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Person getCustomer(@PathVariable("id") String id) {
        return personMapper.selectByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Person createCustomer(@RequestBody Person person) {
        if(person.getId() != null){
            personMapper.updateByPrimaryKeySelective(person);
        } else {
            personMapper.insertSelective(person);
        }
        return person;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteGood(@PathVariable("id") String id) {
        //clear for employers
        CompanyEmployersExample employersExample = new CompanyEmployersExample();
        employersExample.createCriteria().andPersonIdEqualTo(id);
        employersMapper.deleteByExample(employersExample);
        //delete from orders
        PurchaseOrderExample orderExample = new PurchaseOrderExample();
        orderExample.createCriteria().andPersonIdEqualTo(id);
        orderMapper.deleteByExample(orderExample);
        //delete person itself
        personMapper.deleteByPrimaryKey(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getCustomerMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Person p : personMapper.selectByExample(new PersonExample())){
            map = new HashMap<>();
            map.put("id",p.getId());
            map.put("name",p.getFirstName()+ " "+ p.getLastName() + " "+ p.getMiddleName());
            list.add(map);
        }
        return list;
    }

}
