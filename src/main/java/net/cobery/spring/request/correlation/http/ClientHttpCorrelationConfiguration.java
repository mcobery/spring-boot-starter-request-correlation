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
package net.cobery.spring.request.correlation.http;

import java.util.ArrayList;
import java.util.List;
import net.cobery.spring.request.correlation.support.RequestCorrelationProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.web.client.RestTemplate;

/**
 * Configures any {@link RestTemplate} bean by adding a request interceptor to any {@link
 * RestTemplate} beans if it finds the RestTemplate in the class path, and we haven't disabled it in
 * the properties.
 *
 * @author Jakub Narloch
 */
@Configuration
@ConditionalOnClass(InterceptingHttpAccessor.class)
@ConditionalOnProperty(value = "request.correlation.client.http.enabled", matchIfMissing = true)
public class ClientHttpCorrelationConfiguration {

  @Autowired(required = false)
  private List<InterceptingHttpAccessor> clients = new ArrayList<>();

  @Bean
  public InitializingBean clientsCorrelationInitializer(
      final RequestCorrelationProperties properties) {
    // InitializingBean is a functional interface, so we can just return a lambda implementation
    // of its afterPropertiesSet() function.
    return () -> {
      if (clients != null) {
        for (InterceptingHttpAccessor client : clients) {
          final List<ClientHttpRequestInterceptor> interceptors =
              new ArrayList<>(client.getInterceptors());
          interceptors.add(new ClientHttpRequestCorrelationInterceptor(properties));
          client.setInterceptors(interceptors);
        }
      }
    };
  }
}
