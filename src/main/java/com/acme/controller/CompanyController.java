package com.acme.controller;

import com.acme.model.Company;
import com.acme.model.dto.MapDto;
import com.acme.repository.CategoryItemRepository;
import com.acme.repository.CompanyRepository;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import com.acme.service.TransliteService;
import com.acme.util.PageTools;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/company")
@Slf4j
public class CompanyController {

    CompanyRepository companyRepository;
    ItemService itemService;
    CategoryItemRepository categoryItemRepository;
    CategoryService categoryService;
    PlatformTransactionManager transactionManager;
    TransliteService transliteService;

    @Autowired
  public CompanyController(CompanyRepository companyRepository, ItemService itemService, CategoryItemRepository categoryItemRepository, CategoryService categoryService,
    PlatformTransactionManager transactionManager, TransliteService transliteService) {
    this.companyRepository = companyRepository;
    this.itemService = itemService;
    this.categoryItemRepository = categoryItemRepository;
    this.categoryService = categoryService;
    this.transactionManager = transactionManager;
    this.transliteService = transliteService;
  }

  /**
     * Получение списка компаний
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Company> getCompanies(Pageable pageable) {
      Page<Company> page = companyRepository.findAll(PageTools.getPageable(pageable));
      PageTools.setPageHeaders(page);
      return page.getContent();
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

          if(Strings.isNullOrEmpty(company.getTransliteName())){
            company.setTransliteName(transliteService.translite(company.getName()));
          }
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
