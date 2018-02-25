package com.acme.model.dto.mapper;

import org.hibernate.proxy.HibernateProxy;

/**
 * Some lazy elements need explicit deproxy
 */
public abstract class BaseMapper {

    /**
     * Получение реального объекта из ленивой загрузки
     * @param entity
     * @param baseClass
     * @param <T>
     * @return
     * @throws ClassCastException
     */
    protected <T> T deproxy(Object entity, Class<T> baseClass) throws ClassCastException {
        if (entity == null) {
            throw new NullPointerException("Entity passed for initialization is null");
        }
        if (entity instanceof HibernateProxy) {
            return baseClass.cast(((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation());
        }
        return baseClass.cast(entity);
    }

}
