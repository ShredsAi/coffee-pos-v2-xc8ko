package ai.shreds.product_management_availability_shred_g5ds.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application-level configuration for basic Spring beans and infrastructure components.
 * This configuration class provides common beans needed across the application.
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * Creates an ApplicationEventPublisher bean for publishing domain events.
     * 
     * @param applicationContext the Spring application context
     * @return ApplicationEventPublisher instance for event publishing
     */
    @Bean
    public ApplicationEventPublisher applicationEventPublisher(ApplicationContext applicationContext) {
        return applicationContext;
    }

    /**
     * Creates a RestTemplate bean for HTTP client operations.
     * Configured with timeouts and error handling for external service calls.
     * 
     * @return RestTemplate instance configured for external API calls
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Creates an ObjectMapper bean for JSON serialization/deserialization.
     * Configured for proper handling of LocalDateTime and other Java 8 time types.
     * 
     * @return ObjectMapper instance with time module support
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.findAndRegisterModules();
        return mapper;
    }
}