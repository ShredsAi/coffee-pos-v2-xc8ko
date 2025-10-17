package ai.shreds.product_management_availability_shred_g5ds.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRetry
public class InfrastructureRestClientConfig {

    @Value("${rest.client.connection.timeout:5000}")
    private int connectionTimeout;
    
    @Value("${rest.client.read.timeout:10000}")
    private int readTimeout;
    
    @Value("${rest.client.max.connections:100}")
    private int maxConnections;
    
    @Value("${rest.client.max.per.route:20}")
    private int maxConnectionsPerRoute;
    
    @Value("${circuit.breaker.failure.threshold:50}")
    private float failureRateThreshold;
    
    @Value("${circuit.breaker.wait.duration:60000}")
    private long waitDurationInOpenState;
    
    @Value("${circuit.breaker.sliding.window.size:10}")
    private int slidingWindowSize;
    
    @Value("${retry.max.attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${retry.backoff.delay:2000}")
    private long retryBackoffDelay;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Configure HTTP client with connection pooling and timeouts
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(createHttpClient());
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        
        restTemplate.setRequestFactory(factory);
        
        return restTemplate;
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(failureRateThreshold)
            .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))
            .slidingWindowSize(slidingWindowSize)
            .minimumNumberOfCalls(5)
            .build();
        
        return CircuitBreaker.of("inventoryServiceCircuitBreaker", config);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // Configure exceptions that should trigger retry
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(org.springframework.web.client.ResourceAccessException.class, true);
        retryableExceptions.put(org.springframework.web.client.HttpServerErrorException.class, true);
        retryableExceptions.put(java.net.SocketTimeoutException.class, true);
        retryableExceptions.put(java.net.ConnectException.class, true);
        
        // Create retry policy with retryable exceptions in constructor
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxRetryAttempts, retryableExceptions);
        
        // Configure backoff policy
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryBackoffDelay);
        
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        return retryTemplate;
    }

    @Bean
    public HttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        return connectionManager;
    }
    
    private CloseableHttpClient createHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(connectionTimeout)
            .setSocketTimeout(readTimeout)
            .setConnectionRequestTimeout(connectionTimeout)
            .build();
        
        return HttpClients.custom()
            .setConnectionManager(httpClientConnectionManager())
            .setDefaultRequestConfig(requestConfig)
            .build();
    }
}