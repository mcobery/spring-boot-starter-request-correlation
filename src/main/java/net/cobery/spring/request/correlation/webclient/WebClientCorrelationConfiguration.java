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
package net.cobery.spring.request.correlation.webclient;

import net.cobery.spring.request.correlation.support.RequestCorrelationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Add a {@link WebClientCustomizer} for propagating the correlation ids to WebClient requests if
 * the {@link WebClient} class is in the classpath, and we haven't disabled web client correlation
 * in the properties.
 *
 * @author Steven C. Saliman
 */
@Configuration
@ConditionalOnClass(WebClient.class)
@ConditionalOnProperty(value = "request.correlation.web.client.enabled", matchIfMissing = true)
public class WebClientCorrelationConfiguration {

  /**
   * Define a {@link WebClientCustomizer} that will be used in any Spring {@link WebClient.Builder}
   * beans declared in an application.
   *
   * @param properties the properties to use when configuring the customizer.
   * @return a WebClientCustomizer bean.
   */
  @Bean
  public WebClientCustomizer webClientCorrelationInitializer(
      final RequestCorrelationProperties properties) {
    return new WebClientCorrelationInterceptor(properties);
  }
}
