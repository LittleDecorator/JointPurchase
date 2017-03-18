package com.acme.util;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by kobzev on 17.02.17.
 * Кастомизация UUID генератора для ID
 *
  Usage:

  	@Id
 	@Column(name = "entity_id")
 	@GeneratedValue(generator = "inquisitive-uuid")
 	@GenericGenerator(name = "inquisitive-uuid", strategy = "com.myapp.persistence.generators.InquisitiveUUIDGenerator")
 	private String entityId;

 */
public class InquisitiveUUIDGenerator extends UUIDGenerator {

	private String entityName;

	@Override
	public void configure(Type type, Properties params, Dialect dialect) {
		entityName = params.getProperty(ENTITY_NAME);
		super.configure(type, params, dialect);
	}

	@Override
	public Serializable generate(SessionImplementor session, Object object) {
		Serializable id = session
				.getEntityPersister(entityName, object)
				.getIdentifier(object, session);

		if (id == null) {
			return super.generate(session, object);
		} else {
			return id;
		}
	}
}
