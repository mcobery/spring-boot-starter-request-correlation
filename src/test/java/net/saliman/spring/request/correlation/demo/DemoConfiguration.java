package net.saliman.spring.request.correlation.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the Demo application.  It declares the Rest Template bean because
 * we can't declare it and use it in the same class.
 *
 * @author Steven C. Saliman
 */
@Configuration
public class DemoConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
