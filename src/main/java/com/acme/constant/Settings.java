package com.acme.constant;

public interface Settings {
    
    //TODO: Переделать на сервис или вынести в Constants и инжектированием property настроек приложения
//    String appMainPage="http://grimmstory.ru/#/";
    String appMainPage="http://localhost:7777/#/";
    String registrationResultPage=appMainPage+"registration/result";
    String restoreResultPage=appMainPage+"restore/result";

}
