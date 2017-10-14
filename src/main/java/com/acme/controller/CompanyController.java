package com.acme.controller;

import com.acme.model.Company;
import com.acme.repository.CompanyRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/company")
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    /**
     * Получение списка компаний
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Company> getCompanies() {
        return Lists.newArrayList(companyRepository.findAll());
    }

    /**
     * Получение компании по её ID
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Company getCompany(@PathVariable(value = "id") String id) {
        return companyRepository.findOne(id);
    }

    /**
     * Удаление компании по ID
     * @param id
     */
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteCompany(@PathVariable(value = "id") String id) {
        companyRepository.delete(id);
    }

    /**
     * Обновление/Добаление компании
     * @param company
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.PUT})
    public String createCompany(@RequestBody Company company) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            company = companyRepository.save(company);
            transactionManager.commit(status);
            return company.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
            transactionManager.rollback(status);
            return null;
        }
    }

    /**
     * Получение мапы компаний
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<CompanyMap> getCompanyMap() {
        List<CompanyMap> list = Lists.newArrayList();

        for(Company company : companyRepository.findAll()){
            list.add(new CompanyMap(company.getId(), company.getName()));
        }
        return list;
    }

    /*---------- NESTED ----------*/

    /**
     * Класс предоставляющий данные для списка слиентов
     */
    private class CompanyMap {

        String id;
        String name;

        CompanyMap(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
