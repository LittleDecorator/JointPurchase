package com.acme.service.impl;

import com.acme.model.Subject;
import com.acme.model.Subscriber;
import com.acme.model.filter.SubscriberFilter;
import com.acme.repository.OffsetBasePage;
import com.acme.repository.SubjectRepository;
import com.acme.repository.SubscriberRepository;
import com.acme.repository.specification.SubscriberSpecification;
import com.acme.service.SubscriberService;
import com.google.common.collect.Lists;
import java.util.Date;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Created by nikolay on 15.10.17.
 */

@Service
public class SubscriberServiceImpl implements SubscriberService {

    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Override
    public List<Subscriber> getAllSubscribers(SubscriberFilter filter) {
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd");
        return Lists.newArrayList(subscriberRepository.findAll(SubscriberSpecification.filter(filter), pageable).iterator());
    }

    @Override
    public Subscriber findSubscriber(String mail) {
        return subscriberRepository.findOneByEmail(mail);
    }

    @Override
    public Subscriber createSubscriber(Subscriber subscriber) {
        Subscriber result = findSubscriber(subscriber.getEmail());
        if(Objects.isNull(result)){
            // Проверим зарегистрирован ли подписчик
            Subject subject = subjectRepository.findByEmail(subscriber.getEmail());
            if(subject!=null){
                subscriber.setSubjectId(subject.getId());
            }
            if(subscriber.getDateAdd() == null){
                subscriber.setDateAdd(new Date());
            }
            result = subscriberRepository.save(subscriber);
        }
        return result;
    }

    @Override
    public void deleteSubscriber(String id) {
        subscriberRepository.delete(id);
    }
}
