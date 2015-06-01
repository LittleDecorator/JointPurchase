package com.acme.controller;

import com.acme.gen.domain.Company;
import com.acme.gen.domain.CompanyExample;
import com.acme.gen.mapper.CompanyMapper;
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
    private CompanyMapper companyMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<Company> getCompanies() {
        return companyMapper.selectByExample(new CompanyExample());
    }


    @RequestMapping(method = RequestMethod.POST)
    public Company createCompany(@RequestBody Company company) {
        companyMapper.insertSelective(company);
        return company;
    }


    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getCompanyMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;

        for(Company company : companyMapper.selectByExample(new CompanyExample())){
            map = new HashMap<>();
            map.put("id",company.getId());
            map.put("name",company.getName());
            list.add(map);
        }
        return list;
    }

}
