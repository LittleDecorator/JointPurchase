package com.acme.controller;

import com.acme.model.Company;
import com.acme.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/company")
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Company> getCompanies() {
        return companyRepository.getAll();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Company getCompany(@PathVariable(value = "id") String id) {
        return companyRepository.getById(id);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteCompany(@PathVariable(value = "id") String id) {
        companyRepository.deleteById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Company createCompany(@RequestBody Company company) {
        companyRepository.insert(company);
        return company;
    }


    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getCompanyMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;

        for(Company company : companyRepository.getAll()){
            map = new HashMap<>();
            map.put("id",company.getId());
            map.put("name",company.getName());
            list.add(map);
        }
        return list;
    }

}
