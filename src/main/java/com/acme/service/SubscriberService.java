package com.acme.service;

import com.acme.model.Subscriber;
import com.acme.model.filter.SubscriberFilter;

import java.util.List;

/**
 * Created by nikolay on 15.10.17.
 */
public interface SubscriberService {

    List<Subscriber> getAllSubscribers(SubscriberFilter filter);

    Subscriber createSubscriber(Subscriber subscriber);

    Subscriber findSubscriber(String mail);

    void deleteSubscriber(String id);

}
