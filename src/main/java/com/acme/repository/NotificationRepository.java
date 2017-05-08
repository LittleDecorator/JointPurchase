package com.acme.repository;

import com.acme.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nikolay on 08.05.17.
 */
public interface NotificationRepository extends JpaRepository<Notification, String> {
}
