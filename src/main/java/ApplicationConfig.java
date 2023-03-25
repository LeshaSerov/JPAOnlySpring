import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//@ComponentScan("repository")
@EnableJpaRepositories
@EnableTransactionManagement
public class ApplicationConfig {
//
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/simple");
//        dataSource.setUsername("postgres");
//        dataSource.setPassword("admin");
//        return dataSource;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("repository.entity");
//        factory.setDataSource(dataSource());
//        return factory;
//    }
//
////    @Bean
////    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
////        JpaTransactionManager txManager = new JpaTransactionManager();
////        txManager.setEntityManagerFactory(entityManagerFactory.getObject());
////        return txManager;
////    }


    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(
                "repository.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        //https://easyjava.ru/data/pool/nastrojka-dbcp/
//        DBCP, как это ни удивительно звучит, ещё одна библиотека для создания пулов соединений. Вместе с HikariCP и c3p0 они составляют триумвират наиболее популярных библиотек пулов для java. DBCP разрабатывается The Apache Foundation, что сделало его некоторым образом тяжёловесным.
//        Простое создание пула соединений:
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/simple");
        dataSource.setUsername("postgres");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        //Опция  <property name="hibernate.hbm2ddl.auto">update</property>
        // говорит Hibernate, что надо сканировать все классы, имеющие аннотацию @Entity и
        // обновить схему таблиц базы данных сообразно этим классам
        //Hibernate: drop table User if exists
        //Hibernate: drop sequence if exists hibernate_sequence
        //Hibernate: create sequence hibernate_sequence start with 1 increment by 1
        //Hibernate: create table User (id integer not null, name varchar(255), primary key (id))
        //hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop"); - пришлось закомментировать,
        //т.к. в первый раз создались таблицы и последовательности, а потом не все удалялось, и
        //при следующих запусках с любыми параметрами были ошибки, что уже что-нибудь существует.
//        validate: проверить схему, не вносить изменения в базу данных.
//        update: обновить схему.
//        create: создает схему, уничтожая предыдущие данные.
//        create-drop: отказаться от схемы, когда SessionFactory закрывается явно, как правило, когда приложение остановлено.
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        // hibernateProperties.setProperty("hibernate.format_sql", "true");
        hibernateProperties.setProperty("hibernate.generate_statistics", "true");
        //If our entities use the GenerationType.IDENTITY identifier generator,
        // Hibernate will silently disable batch inserts/updates.
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", "5");
        hibernateProperties.put("hibernate.order_inserts", "true");//сохраняет порядок вставки при batch операциях
        hibernateProperties.put("hibernate.order_updates", "true");//сохраняет порядок update при batch операциях
        hibernateProperties.put("hibernate.batch_versioned_data", "true");//сохраняет порядок версионирования при batch операциях

        //https://vladmihalcea.com/why-you-should-always-use-hibernate-connection-provider_disables_autocommit-for-resource-local-jpa-transactions/
        //hibernate.connection.provider_disables_autocommit=false  - при первом подключении к базе устанавливает там
        //hint autocommit=false. Это поведение дает возможность в случае, когда у нас single Datasource, захватывать соединение с базой
        //не при старте транзакции, а при выполнении первого запроса.
        //В распределенных транзакциях именно такое поведение: соединение с базами захватывается только на время выполнения Statement.
        hibernateProperties.put("hibernate.connection.provider_disables_autocommit", "false");

        hibernateProperties.put("hibernate.cache.use_second_level_cache", "true");
        hibernateProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        //Фабрика сессий также может генерировать и сохранять статистику использования всех объектов, регионов, зависимостей в кеше:
        hibernateProperties.put("hibernate.cache.use_structured_entries", "true");

        return hibernateProperties;
    }
}


//    @Bean
//    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory);
//        return transactionManager;
//    }
////
//    // Конфигурируем адаптер продавца jpa (см. Реальный бой p320)
//    @Bean
//    public JpaVendorAdapter jpaVendorAdapter() {
//        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
//        // Установить тип базы данных (вы можете использовать класс перечисления базы данных в пакете org.springframework.orm.jpa.vendor)
//        jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
//        // Устанавливаем print sql Statement
//        jpaVendorAdapter.setShowSql(true);
//        // Устанавливаем не генерировать операторы DDL
//        jpaVendorAdapter.setGenerateDdl(false);
//        // Установить спящий диалект
//        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
//        return jpaVendorAdapter;
//    }
//
//    // Настройка фабрики диспетчера сущностей
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
//        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
//        // Вставить источник данных
//        emfb.setDataSource(dataSource);
//        // Внедрить адаптер производителя jpa
//        emfb.setJpaVendorAdapter(jpaVendorAdapter);
//        // Установить базовый пакет сканирования
//        emfb.setPackagesToScan("repository.entity");
//        return emfb;
//    }
//
//    @Bean
//    public LocalSessionFactoryBean sessionFactory() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource());
//        sessionFactory.setPackagesToScan(
//                "epam.edu.domain");
//        sessionFactory.setHibernateProperties(hibernateProperties());
//
//        return sessionFactory;
//    }
//
//
//    // Настройка менеджера транзакций jpa
//    @Bean
////    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        // Настройка фабрики диспетчера сущностей
////        transactionManager.setEntityManagerFactory(emf);
//        return transactionManager;
//    }
//}
