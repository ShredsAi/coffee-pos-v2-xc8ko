package ai.shreds.product_management_availability_shred_g5ds.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@EnableAsync
public class InfrastructureSchedulingConfig {

    @Value("${scheduling.task-executor.core-pool-size:5}")
    private int taskExecutorCorePoolSize;
    
    @Value("${scheduling.task-executor.max-pool-size:10}")
    private int taskExecutorMaxPoolSize;
    
    @Value("${scheduling.task-executor.queue-capacity:25}")
    private int taskExecutorQueueCapacity;
    
    @Value("${scheduling.task-executor.thread-name-prefix:AsyncTask-}")
    private String taskExecutorThreadNamePrefix;
    
    @Value("${scheduling.task-scheduler.pool-size:5}")
    private int taskSchedulerPoolSize;
    
    @Value("${scheduling.task-scheduler.thread-name-prefix:ScheduledTask-}")
    private String taskSchedulerThreadNamePrefix;
    
    @Value("${scheduling.task-scheduler.await-termination-seconds:60}")
    private int awaitTerminationSeconds;
    
    @Value("${scheduling.task-scheduler.wait-for-tasks-to-complete-on-shutdown:true}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core thread pool settings
        executor.setCorePoolSize(taskExecutorCorePoolSize);
        executor.setMaxPoolSize(taskExecutorMaxPoolSize);
        executor.setQueueCapacity(taskExecutorQueueCapacity);
        
        // Thread naming
        executor.setThreadNamePrefix(taskExecutorThreadNamePrefix);
        
        // Shutdown settings
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        
        // Rejection policy - caller runs the task if queue is full
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // Allow core threads to timeout
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(60);
        
        executor.initialize();
        
        return executor;
    }

    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // Pool size configuration
        scheduler.setPoolSize(taskSchedulerPoolSize);
        
        // Thread naming
        scheduler.setThreadNamePrefix(taskSchedulerThreadNamePrefix);
        
        // Shutdown settings
        scheduler.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        
        // Rejection policy
        scheduler.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // Remove cancelled tasks from the queue
        scheduler.setRemoveOnCancelPolicy(true);
        
        scheduler.initialize();
        
        return scheduler;
    }
}