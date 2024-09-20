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
package net.cobery.spring.request.correlation.api;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Request id generation abstraction, allows users to implement different strategies for id
 * generation.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 */
public interface CorrelationIdGenerator {

  /**
   * Generates a session id.
   *
   * @param request the request object
   * @return the generated session id
   */
  String generateSessionId(HttpServletRequest request);

  /**
   * Generates a request id.
   *
   * @param request the request object
   * @return the generated request id
   */
  String generateRequestId(HttpServletRequest request);
}
