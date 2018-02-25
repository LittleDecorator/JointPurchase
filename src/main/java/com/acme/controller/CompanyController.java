package com.acme.controller;

import com.acme.model.Company;
import com.acme.model.dto.MapDto;
import com.acme.repository.CategoryItemRepository;
import com.acme.repository.CompanyRepository;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import com.acme.service.TransliteService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ibm.icu.text.Transliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/company")
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ItemService itemService;

    @Autowired
    CategoryItemRepository categoryItemRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    TransliteService transliteService;

    /**
     * Получение списка компаний
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Company> getCompanies() {
        return Lists.newArrayList(companyRepository.findAll());
    }

    //TODO: ПЕРЕДЕЛАТЬ
    @Transactional
    @RequestMapping(method = RequestMethod.PATCH, value = "translite")
    public void transliteItems(@RequestParam(name = "all", required = false, defaultValue = "true") Boolean all){
        transliteService.transliteCompanies(all);
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
     * Получение компании по её ID
     * @param name
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/detail")
    public Company getCompanyDetail(@RequestParam(value = "name") String name) {
        return companyRepository.findOneByTransliteName(name);
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
    public List<MapDto> getCompanyMap() {
        List<MapDto> list = Lists.newArrayList();

        for(Company company : companyRepository.findAll()){
            list.add(new MapDto(company.getId(), company.getName()));
        }
        return list;
    }

    /**
     * Получение мапы компаний
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}/categories")
    public List<MapDto> getCompanyCategories(@PathVariable("id") String id) {
        List<MapDto> result = Lists.newArrayList(new MapDto(null, "Все"));
        result.addAll(categoryService.getRootCategories(id).stream().map(category -> new MapDto(category.getId(), category.getName())).collect(Collectors.toList()));
        return result;
    }

}
