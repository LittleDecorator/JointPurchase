package com.acme.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by nikolay on 07.10.17.
 * <p>
 * Переадрисовываем все не API вызовы
 */

@Controller
public class IndexController {

    @RequestMapping(value = {"/**/{[path:[^\\.]*}"})
    public String part() {
        return "forward:/";
    }
}
