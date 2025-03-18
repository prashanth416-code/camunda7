package com.stackpuz.example;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.impl.DefaultProcessEngineConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;

@Configuration
public class CamundaConfig extends DefaultProcessEngineConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.camunda")
	public DataSource camundaDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setTransactionIsolation("TRANSACTION_READ_COMMITTED"); // Set transaction isolation level
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager camundaTransactionManager(DataSource camundaDataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(camundaDataSource);
		return transactionManager;
	}

	@Bean
	public ProcessEngineConfigurationImpl processEngineConfiguration(DataSource camundaDataSource) throws IOException {
	    SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
	    configuration.setDataSource(camundaDataSource);
	    configuration.setTransactionManager(camundaTransactionManager(camundaDataSource));
	    configuration.setDatabaseSchemaUpdate("true");
	    configuration.setHistory("full");
	    configuration.setEnforceHistoryTimeToLive(false);
	    configuration.setSkipHistoryOptimisticLockingExceptions(true);
	    configuration.setSkipIsolationLevelCheck(true); // Ensure this property is set to true
	    configuration.setJobExecutorActivate(true);

	    // Load BPMN files from the classpath
	    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	        Resource[] resources = resolver.getResources("classpath:/processes/*.bpmn");
	        configuration.setDeploymentResources(resources);

	    return configuration;
	}

}
