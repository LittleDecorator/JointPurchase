package com.acme.db.interceptors;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

@Intercepts({ @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class }) })
public class UUIDGeneratorInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(UUIDGeneratorInterceptor.class);

	private Properties properties = new Properties();

	public static final String KEY_UUID_COLUMN = "column";
	public static final String DEFAULT_UUID_COLUMN = "id";

	private String getUUIDProperty() throws IOException {
		return properties.getProperty(KEY_UUID_COLUMN, DEFAULT_UUID_COLUMN);
	}

	private String getUUIDColumnName() throws IOException {
		String uuidColumnName = getUUIDProperty();
		uuidColumnName = Character.toUpperCase(uuidColumnName.charAt(0)) + uuidColumnName.substring(1);
		return uuidColumnName;
	}

	/*private static SqlSession getSqlSession(Invocation invocation) {
		MappedStatement statement = MappedStatement.class.cast(invocation.getArgs()[0]);
		SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(statement.getConfiguration());
		return sessionFactory.openSession();
	}*/

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object obj = invocation.getArgs()[1];
		log.debug("Try generate uuid property '{}' for entity '{}'",getUUIDProperty(), obj.getClass().getName());

		Method getId = null;
		Method setId = null;

		try {
			getId = obj.getClass().getMethod("get" + getUUIDColumnName());
			setId = obj.getClass().getMethod("set" + getUUIDColumnName(),
					String.class);
			if (getId.invoke(obj) == null) {
				setId.invoke(obj, UUID.randomUUID().toString());
			}
		} catch (NoSuchMethodException e) {
			log.debug("Generation of uuid property passed. Entity '{}' without uuid property '{}'", obj.getClass().getName(), getUUIDProperty());
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;

	}

}