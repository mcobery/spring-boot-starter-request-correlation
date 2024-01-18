/*
 * Copyright (c) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saliman.spring.request.correlation.support;

import jakarta.servlet.DispatcherType;
import net.saliman.spring.request.correlation.api.CorrelationIdGenerator;
import net.saliman.spring.request.correlation.api.EnableRequestCorrelation;
import net.saliman.spring.request.correlation.api.RequestCorrelationInterceptor;
import net.saliman.spring.request.correlation.filter.RequestCorrelationFilter;
import net.saliman.spring.request.correlation.generator.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Configures the request correlation filter.
 *
 * @author Jakub Narloch
 * @see EnableRequestCorrelation
 */
@Configuration
@EnableConfigurationProperties(RequestCorrelationProperties.class)
public class RequestCorrelationConfiguration {
    @Autowired
    private RequestCorrelationProperties properties;

    @Autowired(required = false)
    private List<RequestCorrelationInterceptor> interceptors = new ArrayList<>();


    @Bean
    @ConditionalOnMissingBean(CorrelationIdGenerator.class)
    public CorrelationIdGenerator requestIdGenerator() {
        return new UuidGenerator();
    }

    @Bean
    public RequestCorrelationFilter requestCorrelationFilter(CorrelationIdGenerator generator, RequestCorrelationProperties properties) {

        return new RequestCorrelationFilter(generator, interceptors, properties);
    }

    @Bean
    public FilterRegistrationBean requestCorrelationFilterBean(RequestCorrelationFilter correlationFilter) {
        final FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(correlationFilter);
        filterRegistration.setMatchAfter(false);
        filterRegistration.setDispatcherTypes(
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC));
        filterRegistration.setAsyncSupported(true);
        int filterOrder = properties.getFilterOrderFrom().offset + properties.getFilterOrder();
        filterRegistration.setOrder(filterOrder);
        return filterRegistration;
    }
}
