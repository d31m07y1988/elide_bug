package com.example.demo;

import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.Child;
import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideSettings;
import com.yahoo.elide.ElideSettingsBuilder;
import com.yahoo.elide.audit.AuditLogger;
import com.yahoo.elide.audit.Slf4jLogger;
import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.filter.dialect.RSQLFilterDialect;
import com.yahoo.elide.datastores.hibernate5.AbstractHibernateStore;
import com.yahoo.elide.datastores.hibernate5.HibernateEntityManagerStore;
import org.hibernate.ScrollMode;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Properties;

@SpringBootApplication
@EnableTransactionManagement
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private org.springframework.core.env.Environment env;

    @Bean
    public SessionFactory getSessionFactory(DataSource dataSource) {
        org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration();
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put(Environment.DATASOURCE, dataSource);
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
        properties.put(Environment.SHOW_SQL, env.getProperty("spring.jpa.show-sql"));
        properties.put(Environment.FORMAT_SQL, env.getProperty("spring.jpa.hibernate.format_sql"));
        config.setProperties(properties);
        config.addAnnotatedClass(BaseEntity.class);
        config.addAnnotatedClass(Child.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(config.getProperties()).build();

        return config
                .buildSessionFactory(serviceRegistry);

    }

    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);

        return transactionManager;
    }

    @Bean
    public Elide elide(SessionFactory sessionFactory, EntityManager em) {
        final DataStore store =  new HibernateEntityManagerStore(sessionFactory.createEntityManager(), true, ScrollMode.FORWARD_ONLY);
        final AuditLogger logger = new Slf4jLogger();
        EntityDictionary dictionary = new EntityDictionary(Collections.emptyMap());
        final ElideSettings settings = new ElideSettingsBuilder(store)
                .withAuditLogger(logger)
                .withEntityDictionary(dictionary)
                .withUseFilterExpressions(true)
                .withJoinFilterDialect(new RSQLFilterDialect(dictionary))
                .withSubqueryFilterDialect(new RSQLFilterDialect(dictionary))
                .build();
        return new Elide(settings);
    }
}

