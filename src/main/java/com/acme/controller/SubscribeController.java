package com.acme.controller;

import com.acme.model.Subscriber;
import com.acme.model.filter.SubscriberFilter;
import com.acme.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by nikolay on 15.10.17.
 */
@RestController
@RequestMapping(value = "/api/subscriber")
public class SubscribeController {

    @Autowired
    SubscriberService subscriberService;

    /**
     * Получение всех подписчиков по фильтру
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Subscriber> getSubscribers(SubscriberFilter filter) {
        return subscriberService.getAllSubscribers(filter);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{mail}")
    public Subscriber getSubscriber(@PathVariable("mail") String mail) {
        return subscriberService.findSubscriber(mail);
    }

    /**
     * Добавление подписчика
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Subscriber addSubscriber(@RequestBody Subscriber request) {
        return subscriberService.createSubscriber(request);
    }

    /**
     * Удаление подписчика по ID
     *
     * @param id - subscriber ID
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void removeSubscriber(@PathVariable("id") String id) {
        subscriberService.deleteSubscriber(id);
    }

}
