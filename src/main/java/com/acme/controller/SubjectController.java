package com.acme.controller;

import com.acme.model.Order;
import com.acme.model.Subject;
import com.acme.model.dto.SubjectDto;
import com.acme.model.dto.SubjectMapDto;
import com.acme.model.dto.mapper.SubjectMapper;
import com.acme.model.filter.SubjectFilter;
import com.acme.repository.CredentialRepository;
import com.acme.repository.OffsetBasePage;
import com.acme.repository.OrderItemRepository;
import com.acme.repository.OrderRepository;
import com.acme.repository.SubjectRepository;
import com.acme.repository.specification.SubjectSpecifications;
import com.acme.service.AuthService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/customer")
public class SubjectController{

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    OrderRepository purchaseOrderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    AuthService authService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    SubjectMapper subjectMapper;

    /**
     * Получение списка клиентов (без фильтра)
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<SubjectDto> getSubjects(SubjectFilter filter) {
        /* Сортировка видимо будет идти по модели, и в запросе не участвует */
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd");
        Page<Subject> page = subjectRepository.findAll(SubjectSpecifications.filter(filter), pageable);
        return subjectMapper.toSimpleDto(page.getContent());
    }

    /**
     * Получение конкретного клиента
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public SubjectDto getSubject(@PathVariable("id") String id) {
        return subjectMapper.toDto(subjectRepository.findOne(id));
    }

    /**
     * Получение пользователя на заголовку запроса
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/private")
    public SubjectDto getCurrentSubject() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        Subject subject = subjectRepository.findOne(authService.getClaims(servletRequest).getId());
        return subjectMapper.toDto(subject);
    }

    /**
     * Добавление нового клиента
     *
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public SubjectDto createSubject(@RequestBody SubjectDto dto) {
        Subject subject = subjectMapper.toEntity(dto);
        subject = subjectRepository.save(subject);
        return subjectMapper.toDto(subject);
    }

    /**
     * Обновление существующего клиента
     *
     * @param dto
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateSubject(@RequestBody SubjectDto dto) {
        Subject subject = subjectMapper.toEntity(dto);
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
        try {
            List<String> orderIds = purchaseOrderRepository.findAllBySubjectId(id).stream().map(Order::getId).collect(Collectors.toList());
            // удаляем связь товара с заказами
            orderItemRepository.deleteByIdOrderIdIn(orderIds);
            // удаляем заказы
            purchaseOrderRepository.deleteBySubjectId(id);
        } catch (EmptyResultDataAccessException ex) {
            ex.printStackTrace(System.out);
        }
        try {
            // удаляем из credentials
            credentialRepository.delete(id);
        } catch (EmptyResultDataAccessException ex) {
            ex.printStackTrace(System.out);
        }
        //delete subject itself
        subjectRepository.delete(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public Set<SubjectMapDto> getSubjectMap() {
        return subjectMapper.toMapDto(subjectRepository.findAll());
    }

}
