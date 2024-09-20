/*
 * Copyright (c) 2015-2024 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing permissions and limitations
 * under the License.
 */
package net.cobery.spring.request.correlation.filter;

import jakarta.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.cobery.spring.request.correlation.api.CorrelationIdGenerator;
import net.cobery.spring.request.correlation.api.EnableRequestCorrelation;
import net.cobery.spring.request.correlation.api.RequestCorrelationInterceptor;
import net.cobery.spring.request.correlation.generator.DefaultIdGenerator;
import net.cobery.spring.request.correlation.support.RequestCorrelationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the {@link RequestCorrelationFilter} to assign correlating ids to all incoming
 * requests.
 *
 * @author Jakub Narloch
 * @see EnableRequestCorrelation
 */
@Configuration
@EnableConfigurationProperties(RequestCorrelationProperties.class)
public class RequestCorrelationConfiguration {
  /** The properties to use when configuring the filter. */
  @Autowired private RequestCorrelationProperties properties;

  /** a list of {@link RequestCorrelationInterceptor}s to call when ids get set. */
  @Autowired(required = false)
  private List<RequestCorrelationInterceptor> interceptors = new ArrayList<>();

  /**
   * Define a default {@link CorrelationIdGenerator} if the application hasn't defined one of its
   * own.
   *
   * @return an instance of the {@link DefaultIdGenerator} to use for id generation.
   */
  @Bean
  @ConditionalOnMissingBean(CorrelationIdGenerator.class)
  public CorrelationIdGenerator requestIdGenerator() {
    return new DefaultIdGenerator();
  }

  /**
   * Define a {@link RequestCorrelationFilter} bean that will be added to the application's filter
   * chain.
   *
   * @param generator the generator to use for creating correlating ids.
   * @param properties the properties to use when configuring the filter.
   * @return a {@link RequestCorrelationFilter} bean.
   */
  @Bean
  public RequestCorrelationFilter requestCorrelationFilter(
      CorrelationIdGenerator generator, RequestCorrelationProperties properties) {
    return new RequestCorrelationFilter(generator, interceptors, properties);
  }

  /**
   * Create a {@link FilterRegistrationBean} that registers the {@link RequestCorrelationFilter}
   * with Spring's filter chain.
   *
   * @param correlationFilter the filter to use.
   * @return the Filter Registration bean.
   */
  @Bean
  public FilterRegistrationBean<RequestCorrelationFilter> requestCorrelationFilterBean(
      RequestCorrelationFilter correlationFilter) {
    final FilterRegistrationBean<RequestCorrelationFilter> filterRegistration =
        new FilterRegistrationBean<>();
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
