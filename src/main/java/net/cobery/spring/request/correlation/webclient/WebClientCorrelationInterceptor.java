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
import net.cobery.spring.request.correlation.support.RequestCorrelationUtils;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Rest template http interceptor, that propagates the currents thread bound request identifier to
 * the outgoing request, through 'X-Request-Id' header.
 *
 * @author Jakub Narloch
 */
public class WebClientCorrelationInterceptor implements WebClientCustomizer {

  /** The correlation properties. */
  private final RequestCorrelationProperties properties;

  /**
   * Creates new instance of {@link WebClientCorrelationInterceptor}.
   *
   * @param properties the properties
   * @throws IllegalArgumentException if {@code properties} is {@code null}
   */
  public WebClientCorrelationInterceptor(RequestCorrelationProperties properties) {
    Assert.notNull(properties, "Parameter 'properties' can not be null");

    this.properties = properties;
  }

  /**
   * Callback to customize a {@link WebClient.Builder WebClient.Builder} instance.
   *
   * @param webClientBuilder the client builder to customize
   */
  @Override
  public void customize(WebClient.Builder webClientBuilder) {
    webClientBuilder.filter(addCorrelationHeaders());
  }

  /**
   * Private helper method that is the meat of the customization. This defines a filter function to
   * be applied to all WebClients created from a Spring managed WebClient.Builder. The filter will
   * add our request correlation ids to the requests.
   *
   * @return an {@link ExchangeFilterFunction} that can add correlation ids to request headers.
   */
  private ExchangeFilterFunction addCorrelationHeaders() {
    return (clientRequest, next) -> {
      ClientRequest.Builder newRequest = ClientRequest.from(clientRequest);
      // sets the correlation session id
      final String sessionId = RequestCorrelationUtils.getCurrentSessionId();
      if (sessionId != null) {
        newRequest.header(properties.getSessionHeaderName(), sessionId);
      }

      // sets the correlation request id
      final String requestId = RequestCorrelationUtils.getCurrentRequestId();
      if (requestId != null) {
        newRequest.header(properties.getRequestHeaderName(), requestId);
      }

      return next.exchange(newRequest.build());
    };
  }
}
