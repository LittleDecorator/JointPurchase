package com.acme.service.impl;

import com.acme.model.Category;
import com.acme.model.Company;
import com.acme.repository.CategoryRepository;
import com.acme.repository.CompanyRepository;
import com.acme.service.TransliteService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ibm.icu.text.Transliterator;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransliteServiceImpl implements TransliteService {

    private static String RUSSIAN_TO_LATIN_BGN = "Russian-Latin/BGN";

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CategoryRepository categoryRepository;


    public void transliteCompanies(boolean all) {
        Stream<Company> companies = Lists.newArrayList(companyRepository.findAll()).stream();
        if(!all){
            // фильтруем если нужно
            companies = companies.filter(company -> Strings.isNullOrEmpty(company.getTransliteName()));

        }
        // обновляем
        companies.forEach(company -> {
            company.setTransliteName(translite(company.getName()));
            companyRepository.save(company);
        });
    }

    @Override
    public void transliteCategories(boolean all) {
        Stream<Category> categories = Lists.newArrayList(categoryRepository.findAll()).stream();
        if(!all){
            // фильтруем если нужно
            categories = categories.filter(category -> Strings.isNullOrEmpty(category.getTransliteName()));

        }
        // обновляем
        categories.forEach(category -> {
            category.setTransliteName(translite(category.getName()));
            categoryRepository.save(category);
        });
    }

    /**
     *
     * @param input
     * @return
     */
    public String translite(String input){
        Transliterator russianToLatinNoAccentsTrans = Transliterator.getInstance(RUSSIAN_TO_LATIN_BGN);
        return russianToLatinNoAccentsTrans.transliterate(input).replaceAll("·|ʹ|\\.|\"|,|\\(|\\)", "").replaceAll("\\*|\\s+","-").toLowerCase();
    }


}
