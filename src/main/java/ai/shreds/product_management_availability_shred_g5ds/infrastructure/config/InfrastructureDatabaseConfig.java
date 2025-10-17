package ai.shreds.product_management_availability_shred_g5ds.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories",
    transactionManagerRef = "transactionManager",
    entityManagerFactoryRef = "entityManagerFactory"
)
public class InfrastructureDatabaseConfig {

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/product_management}")
    private String databaseUrl;
    
    @Value("${spring.datasource.username:postgres}")
    private String databaseUsername;
    
    @Value("${spring.datasource.password:password}")
    private String databasePassword;
    
    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String databaseDriverClassName;
    
    @Value("${spring.jpa.hibernate.ddl-auto:validate}")
    private String hibernateDdlAuto;
    
    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;
    
    @Value("${spring.jpa.properties.hibernate.format_sql:true}")
    private boolean formatSql;
    
    @Value("${spring.jpa.properties.hibernate.dialect:org.hibernate.dialect.PostgreSQL95Dialect}")
    private String hibernateDialect;
    
    @Value("${hikari.connection-pool.maximum-pool-size:20}")
    private int maximumPoolSize;
    
    @Value("${hikari.connection-pool.minimum-idle:5}")
    private int minimumIdle;
    
    @Value("${hikari.connection-pool.connection-timeout:30000}")
    private long connectionTimeout;
    
    @Value("${hikari.connection-pool.idle-timeout:600000}")
    private long idleTimeout;
    
    @Value("${hikari.connection-pool.max-lifetime:1800000}")
    private long maxLifetime;
    
    @Value("${hikari.connection-pool.leak-detection-threshold:60000}")
    private long leakDetectionThreshold;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = hikariConfig();
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan("ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(showSql);
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", hibernateDialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        jpaProperties.put("hibernate.show_sql", showSql);
        jpaProperties.put("hibernate.format_sql", formatSql);
        jpaProperties.put("hibernate.use_sql_comments", true);
        jpaProperties.put("hibernate.jdbc.batch_size", 25);
        jpaProperties.put("hibernate.order_inserts", true);
        jpaProperties.put("hibernate.order_updates", true);
        jpaProperties.put("hibernate.jdbc.batch_versioned_data", true);
        jpaProperties.put("hibernate.connection.provider_disables_autocommit", true);
        jpaProperties.put("hibernate.query.plan_cache_max_size", 2048);
        jpaProperties.put("hibernate.query.plan_parameter_metadata_max_size", 128);
        jpaProperties.put("hibernate.cache.use_second_level_cache", false);
        jpaProperties.put("hibernate.cache.use_query_cache", false);
        jpaProperties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
        
        entityManagerFactory.setJpaProperties(jpaProperties);
        
        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        
        // Database connection settings
        config.setJdbcUrl(databaseUrl);
        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
        config.setDriverClassName(databaseDriverClassName);
        
        // Connection pool settings
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setLeakDetectionThreshold(leakDetectionThreshold);
        
        // Connection pool name for monitoring
        config.setPoolName("ProductManagementHikariPool");
        
        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        return config;
    }
}