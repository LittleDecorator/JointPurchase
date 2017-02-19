package com.acme.controller;

import com.acme.model.Subject;
import com.acme.repository.PurchaseOrderRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/customer")
public class SubjectController{

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    AuthService authService;

    /**
     * Получение списка клиентов (без фильтра)
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Subject> getSubjects() {
        return (List<Subject>) subjectRepository.findAll();
    }

    /**
     * Получение конкретного клиента
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Subject getSubject(@PathVariable("id") String id) {
        return subjectRepository.findOne(id);
    }

    /**
     * Получение пользователя на заголовку запроса
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/private")
    public Subject getCurrentSubject() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        return subjectRepository.findOne(authService.getClaims(servletRequest).getId());
    }

    /**
     * Добавление нового клиента
     *
     * @param subject
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectRepository.save(subject);
    }

    /**
     * Обновление существующего клиента
     *
     * @param subject
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateSubject(@RequestBody Subject subject) {
        subjectRepository.save(subject);
    }

    /**
     * Удаление клиента по ID
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteSubject(@PathVariable("id") String id) {
        //delete from orders
        purchaseOrderRepository.delete(id);
        //delete subject itself
        subjectRepository.delete(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getSubjectMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Subject p : subjectRepository.findAll()){
            map = new HashMap<>();
            map.put("id",p.getId());
            map.put("name",p.getFirstName()+ " "+ p.getLastName() + " "+ p.getMiddleName());
            list.add(map);
        }
        return list;
    }

}
