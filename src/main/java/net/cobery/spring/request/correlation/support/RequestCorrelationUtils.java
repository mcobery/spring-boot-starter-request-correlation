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
package net.cobery.spring.request.correlation.support;

import net.cobery.spring.request.correlation.api.RequestCorrelation;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * A utility class for retrieving the currently bound request correlation ids.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 */
public class RequestCorrelationUtils {

  /**
   * Retrieves the current correlation session id from the request attributes if present.
   *
   * @return the correlation id or {@code null}
   */
  public static String getCurrentSessionId() {
    final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      Object correlationId =
          requestAttributes.getAttribute(
              RequestCorrelationConsts.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);

      if (correlationId instanceof RequestCorrelation) {
        return ((RequestCorrelation) correlationId).getSessionId();
      }
    }
    return null;
  }

  /**
   * Retrieves the current correlation request id from the request attributes if present.
   *
   * @return the correlation id or {@code null}
   */
  public static String getCurrentRequestId() {

    final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      Object correlationId =
          requestAttributes.getAttribute(
              RequestCorrelationConsts.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);

      if (correlationId instanceof RequestCorrelation) {
        return ((RequestCorrelation) correlationId).getRequestId();
      }
    }
    return null;
  }
}
