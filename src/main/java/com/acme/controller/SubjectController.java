package com.acme.controller;

import com.acme.model.Subject;
import com.acme.model.filter.SubjectFilter;
import com.acme.repository.OffsetBasePage;
import com.acme.repository.OrderRepository;
import com.acme.repository.SubjectRepository;
import com.acme.repository.specification.OrderViewSpecifications;
import com.acme.repository.specification.SubjectSpecifications;
import com.acme.service.AuthService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/customer")
public class SubjectController{

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    OrderRepository purchaseOrderRepository;

    @Autowired
    AuthService authService;

    /**
     * Получение списка клиентов (без фильтра)
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Subject> getSubjects(SubjectFilter filter) {
        /* Сортировка видимо будет идти по модели, и в запросе не участвует */
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd");
        return Lists.newArrayList(subjectRepository.findAll(SubjectSpecifications.filter(filter), pageable).iterator());
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
    public List<SubjectMap> getSubjectMap() {
        List<SubjectMap> list = Lists.newArrayList();
        for(Subject subject : subjectRepository.findAll()){
            list.add(new SubjectMap(subject.getId(), subject.getFirstName()+ " "+ subject.getLastName() + " "+ subject.getMiddleName()));
        }
        return list;
    }


    /*---------- NESTED ----------*/

    /**
     * Класс предоставляющий данные для списка слиентов
     */
    private class SubjectMap {

        String id;
        String name;

        SubjectMap(String id, String name) {
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
