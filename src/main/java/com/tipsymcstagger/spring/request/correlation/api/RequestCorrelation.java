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
package com.tipsymcstagger.spring.request.correlation.api;

/**
 * Holder for the correlation ids.
 *
 * @author Jakub Narloch
 */
public interface RequestCorrelation {

  /**
   * Returns the correlation session id.
   *
   * @return the correlation session id
   */
  String getSessionId();

  /**
   * Returns the correlation request id.
   *
   * @return the correlation request id
   */
  String getRequestId();
}
