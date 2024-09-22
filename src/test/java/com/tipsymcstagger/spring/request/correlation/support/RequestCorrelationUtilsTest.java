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

import static org.assertj.core.api.Assertions.assertThat;

import com.tipsymcstagger.spring.request.correlation.filter.DefaultRequestCorrelation;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Tests the {@link RequestCorrelationUtils} class.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationUtilsTest {

  @BeforeEach
  public void setUp() {

    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @AfterEach
  public void tearDown() {

    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void shouldNotRetrieveRequestId() {

    // given
    RequestContextHolder.resetRequestAttributes();

    // when
    final String currentSessionId = RequestCorrelationUtils.getCurrentSessionId();
    final String currentRequestId = RequestCorrelationUtils.getCurrentRequestId();

    // then
    assertThat(currentSessionId).isNull();
    assertThat(currentRequestId).isNull();
  }

  @Test
  public void shouldRetrieveCorrelatingIds() {

    // given
    final String sessionId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();
    RequestContextHolder.getRequestAttributes()
        .setAttribute(
            RequestCorrelationConsts.ATTRIBUTE_NAME,
            new DefaultRequestCorrelation(sessionId, requestId),
            RequestAttributes.SCOPE_REQUEST);

    // when
    final String currentSessionId = RequestCorrelationUtils.getCurrentSessionId();
    final String currentRequestId = RequestCorrelationUtils.getCurrentRequestId();

    // then
    assertThat(sessionId).isEqualTo(currentSessionId);
    assertThat(requestId).isEqualTo(currentRequestId);
  }
}
