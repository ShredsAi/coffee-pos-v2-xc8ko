package ai.shreds.product_management_availability_shred_g5ds.infrastructure.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class InfrastructureJMSConfig {

    @Value("${spring.activemq.broker-url:tcp://localhost:61616}")
    private String brokerUrl;
    
    @Value("${spring.activemq.user:admin}")
    private String username;
    
    @Value("${spring.activemq.password:admin}")
    private String password;
    
    @Value("${jms.connection.pool.max-connections:10}")
    private int maxConnections;
    
    @Value("${jms.connection.pool.idle-timeout:30000}")
    private int idleTimeout;
    
    @Value("${jms.listener.concurrency:1-5}")
    private String listenerConcurrency;
    
    @Value("${jms.listener.receive-timeout:1000}")
    private long receiveTimeout;
    
    @Value("${jms.template.delivery-persistent:true}")
    private boolean deliveryPersistent;
    
    @Value("${jms.template.time-to-live:0}")
    private long timeToLive;
    
    @Value("${jms.template.priority:4}")
    private int priority;

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        
        // Connection pool configuration
        connectionFactory.setMaxThreadPoolSize(maxConnections);
        connectionFactory.setOptimizeAcknowledge(true);
        connectionFactory.setUseAsyncSend(true);
        connectionFactory.setAlwaysSessionAsync(false);
        
        // Security and reliability settings
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setCloseTimeout(idleTimeout);
        
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(messageConverter());
        
        // Delivery settings
        template.setDeliveryPersistent(deliveryPersistent);
        template.setTimeToLive(timeToLive);
        template.setPriority(priority);
        template.setExplicitQosEnabled(true);
        
        // Performance settings
        template.setSessionTransacted(true);
        template.setReceiveTimeout(receiveTimeout);
        
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(messageConverter());
        
        // Concurrency settings
        factory.setConcurrency(listenerConcurrency);
        
        // Transaction settings
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(javax.jms.Session.SESSION_TRANSACTED);
        
        // Error handling settings
        factory.setAutoStartup(true);
        factory.setReceiveTimeout(receiveTimeout);
        
        // Recovery settings
        factory.setRecoveryInterval(5000L); // 5 seconds
        factory.setCacheLevelName("CACHE_CONSUMER");
        
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}