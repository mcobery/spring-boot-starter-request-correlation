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

/**
 * An interceptor that can be used by applications to receive callbacks when correlating ids are
 * generated. This is most useful when an application wants to use the ids to, for example, include
 * the ids in application logs.
 *
 * <p>To use {@code RequestCorrelationInterceptor}, applications simply need to declare a bean that
 * implements this interface.
 *
 * @author Jakub Narloch
 */
public interface RequestCorrelationInterceptor {

  /**
   * Callback method called whenever the correlation ids have been assigned for the current request,
   * whether they have been set from the request header new ids have been generated for the incoming
   * request.
   *
   * @param sessionId the session id
   * @param requestId the request id
   */
  void afterCorrelationIdSet(String sessionId, String requestId);

  /**
   * Callback method called after filter chain has completed.
   *
   * @param sessionId the session id
   * @param requestId the request id
   */
  void cleanUp(String sessionId, String requestId);
}
