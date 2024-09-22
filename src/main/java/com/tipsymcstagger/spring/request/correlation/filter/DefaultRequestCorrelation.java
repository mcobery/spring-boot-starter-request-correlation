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
package com.tipsymcstagger.spring.request.correlation.filter;

import com.tipsymcstagger.spring.request.correlation.api.RequestCorrelation;

/**
 * Base implementation of {@link RequestCorrelation}.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 */
public final class DefaultRequestCorrelation implements RequestCorrelation {

  /** The actual correlation session id. */
  private final String sessionId;

  /** The actual correlation request id. */
  private final String requestId;

  /**
   * Creates new instance of {@link DefaultRequestCorrelation} class.
   *
   * @param sessionId the session id
   * @param requestId the request id
   */
  public DefaultRequestCorrelation(String sessionId, String requestId) {
    this.sessionId = sessionId;
    this.requestId = requestId;
  }

  /**
   * Retrieves the session identifier.
   *
   * @return the session identifier
   */
  @Override
  public String getSessionId() {
    return sessionId;
  }

  /**
   * Retrieves the request identifier.
   *
   * @return the request identifier
   */
  @Override
  public String getRequestId() {
    return requestId;
  }
}
