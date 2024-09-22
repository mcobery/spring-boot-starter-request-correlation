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
package com.tipsymcstagger.spring.request.correlation.feign;

import com.tipsymcstagger.spring.request.correlation.support.RequestCorrelationProperties;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds a Feign {@link RequestInterceptor} for propagating the correlation id if Feign is detected
 * in the classpath, and we haven't disabled feign in the properties.
 *
 * @author Jakub Narloch
 */
@Configuration
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(value = "request.correlation.client.feign.enabled", matchIfMissing = true)
public class FeignCorrelationConfiguration {

  /**
   * Create a Feign {@link RequestInterceptor} bean that will propagate correlation ids to Feign
   * clients.
   *
   * @param properties the Request Correlation properties to use when configuring the bean.
   * @return a RequestInterceptor for Feign.
   */
  @Bean
  public RequestInterceptor feignCorrelationInterceptor(RequestCorrelationProperties properties) {
    return new FeignCorrelationInterceptor(properties);
  }
}
