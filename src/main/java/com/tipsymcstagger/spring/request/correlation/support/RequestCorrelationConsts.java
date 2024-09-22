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
package com.tipsymcstagger.spring.request.correlation.support;

/**
 * Some helpful constants used by this starter.
 *
 * @author Jakub Narloch
 */
public interface RequestCorrelationConsts {

  /** The correlation session id header name. */
  String SESSION_HEADER_NAME = "X-Session-Id";

  /** The correlation request id header name. */
  String REQUEST_HEADER_NAME = "X-Request-Id";

  /** The request attribute name for storing the ids, separate from the headers. */
  String ATTRIBUTE_NAME = "RequestCorrelation.ATTRIBUTE";
}
