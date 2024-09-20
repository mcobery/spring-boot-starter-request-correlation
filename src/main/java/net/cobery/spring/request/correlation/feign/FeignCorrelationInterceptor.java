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
package net.cobery.spring.request.correlation.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import net.cobery.spring.request.correlation.support.RequestCorrelationProperties;
import net.cobery.spring.request.correlation.support.RequestCorrelationUtils;
import org.springframework.util.Assert;

/**
 * Feign request correlation interceptor.
 *
 * @author Jakub Narloch
 */
public class FeignCorrelationInterceptor implements RequestInterceptor {

  /** The correlation properties. */
  private final RequestCorrelationProperties properties;

  /**
   * Creates new instance of {@link FeignCorrelationInterceptor}.
   *
   * @param properties the correlation properties
   * @throws IllegalArgumentException if {@code properties} is {@code null}
   */
  public FeignCorrelationInterceptor(RequestCorrelationProperties properties) {
    Assert.notNull(properties, "Parameter 'properties' can not be null");

    this.properties = properties;
  }

  /** {@inheritDoc} */
  @Override
  public void apply(RequestTemplate template) {

    final String sessionId = RequestCorrelationUtils.getCurrentSessionId();
    if (sessionId != null) {
      template.header(properties.getSessionHeaderName(), sessionId);
    }

    final String requestId = RequestCorrelationUtils.getCurrentRequestId();
    if (requestId != null) {
      template.header(properties.getRequestHeaderName(), requestId);
    }
  }
}
