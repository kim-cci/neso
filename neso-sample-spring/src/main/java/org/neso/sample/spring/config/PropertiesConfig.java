package org.neso.sample.spring.config;

import java.io.IOException;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Properties
 * 
 */
@Configuration
public class PropertiesConfig {
	
	public static final String PROJECT_PROPERTY_PATH = "classpath*:properties/**/*";
    
	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
		PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
		propertyConfigurer.setLocations(getProjectResources());
		return propertyConfigurer;
	}
	
	public static Resource[] getProjectResources() throws IOException {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] commResource = patternResolver.getResources(PROJECT_PROPERTY_PATH);
        return commResource;
	}
	

    /**
     * Necessary to make the Value annotations work.
     * 
     * @return
   
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
      */
}
