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
package com.tipsymcstagger.spring.request.correlation.generator;

import com.tipsymcstagger.spring.request.correlation.api.CorrelationIdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Default implementation of the {@link CorrelationIdGenerator} that uses the HTTP session id for
 * session ids and a {@link UUID#randomUUID()} for generating new requests ids.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 */
public class DefaultIdGenerator implements CorrelationIdGenerator {

  /**
   * Generates a new session id from the session's id.
   *
   * @return The session id.
   */
  @Override
  public String generateSessionId(HttpServletRequest request) {
    HttpSession session = request.getSession();
    return session.getId();
  }

  /**
   * Generates a new request id as a random UUID.
   *
   * @return random uuid
   */
  @Override
  public String generateRequestId(HttpServletRequest request) {
    return UUID.randomUUID().toString();
  }
}
