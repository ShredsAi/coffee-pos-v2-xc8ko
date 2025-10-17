package ai.shreds.product_management_availability_shred_g5ds.infrastructure.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureLoggerConfig {

    @Value("${logging.file.path:./logs}")
    private String logFilePath;
    
    @Value("${logging.file.name:product-management-availability}")
    private String logFileName;
    
    @Value("${logging.level.audit:INFO}")
    private String auditLogLevel;
    
    @Value("${logging.pattern.console:%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n}")
    private String consolePattern;
    
    @Value("${logging.pattern.file:%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n}")
    private String filePattern;
    
    @Value("${logging.file.max-history:30}")
    private int maxHistory;
    
    @Value("${logging.file.max-size:100MB}")
    private String maxFileSize;

    @Bean
    public LoggerContext loggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    @Bean
    public Appender customAppender() {
        LoggerContext context = loggerContext();
        
        // Create rolling file appender for application logs
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(context);
        fileAppender.setName("APPLICATION_FILE");
        fileAppender.setFile(logFilePath + "/" + logFileName + ".log");
        
        // Configure rolling policy
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(logFilePath + "/" + logFileName + ".%d{yyyy-MM-dd}.%i.log.gz");
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.start();
        
        fileAppender.setRollingPolicy(rollingPolicy);
        
        // Configure encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(filePattern);
        encoder.start();
        
        fileAppender.setEncoder(encoder);
        fileAppender.start();
        
        return fileAppender;
    }

    @Bean
    public Logger auditLogger() {
        LoggerContext context = loggerContext();
        
        // Create dedicated audit logger
        ch.qos.logback.classic.Logger auditLogger = context.getLogger("AUDIT");
        auditLogger.setAdditive(false);
        
        // Create audit file appender
        RollingFileAppender auditAppender = new RollingFileAppender();
        auditAppender.setContext(context);
        auditAppender.setName("AUDIT_FILE");
        auditAppender.setFile(logFilePath + "/audit.log");
        
        // Configure rolling policy for audit logs
        TimeBasedRollingPolicy auditRollingPolicy = new TimeBasedRollingPolicy();
        auditRollingPolicy.setContext(context);
        auditRollingPolicy.setParent(auditAppender);
        auditRollingPolicy.setFileNamePattern(logFilePath + "/audit.%d{yyyy-MM-dd}.%i.log.gz");
        auditRollingPolicy.setMaxHistory(365); // Keep audit logs for 1 year
        auditRollingPolicy.start();
        
        auditAppender.setRollingPolicy(auditRollingPolicy);
        
        // Configure audit encoder with structured format
        PatternLayoutEncoder auditEncoder = new PatternLayoutEncoder();
        auditEncoder.setContext(context);
        auditEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [AUDIT] - %msg%n");
        auditEncoder.start();
        
        auditAppender.setEncoder(auditEncoder);
        auditAppender.start();
        
        auditLogger.addAppender(auditAppender);
        auditLogger.setLevel(ch.qos.logback.classic.Level.valueOf(auditLogLevel));
        
        return auditLogger;
    }
    
    @Bean
    public Appender consoleAppender() {
        LoggerContext context = loggerContext();
        
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE");
        
        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setPattern(consolePattern);
        consoleEncoder.start();
        
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();
        
        return consoleAppender;
    }
}