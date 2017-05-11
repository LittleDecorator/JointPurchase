package com.acme.repository;

import com.acme.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by nikolay on 08.05.17.
 */
public interface NotificationRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {

    /**
     * Получение уведомлений
     * @param viewedSubjectId - просмотревший
     * @param isRootOnly - только для root'а
     * @return
     */
    List<Notification> findAllByViewedSubjectIdAndIsRootOnly(String viewedSubjectId, boolean isRootOnly);

}
