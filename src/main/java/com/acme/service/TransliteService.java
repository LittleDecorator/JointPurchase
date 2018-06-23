package com.acme.service;

public interface TransliteService {

    void transliteCompanies(boolean all);

    void transliteCategories(boolean all);

  String translite(String input);

}
