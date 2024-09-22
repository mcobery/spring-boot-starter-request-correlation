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
package com.tipsymcstagger.spring.request.correlation.feign;

import static org.assertj.core.api.Assertions.assertThat;

import com.tipsymcstagger.spring.request.correlation.CorrelationTestUtils;
import com.tipsymcstagger.spring.request.correlation.support.RequestCorrelationConsts;
import com.tipsymcstagger.spring.request.correlation.support.RequestCorrelationProperties;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Tests the {@link FeignCorrelationInterceptor} class.
 *
 * @author Jakub Narloch
 * @author Steven C. Saliman
 */
public class FeignCorrelationInterceptorTest {
  private static final String SESSION_ID = "TEST_SESSION_ID";
  private static final String REQUEST_ID = "TEST_REQUEST_ID";
  private RequestCorrelationProperties properties = new RequestCorrelationProperties();
  private FeignCorrelationInterceptor instance;

  @BeforeEach
  public void setUp() {
    instance = new FeignCorrelationInterceptor(properties);
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @AfterEach
  public void tearDown() {

    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void shouldSetHeader() {

    // given
    CorrelationTestUtils.setCorrelatingIds(SESSION_ID, REQUEST_ID);
    final RequestTemplate request = new RequestTemplate();

    // when
    instance.apply(request);

    // then
    assertThat(request.headers())
        .containsKeys(
            RequestCorrelationConsts.SESSION_HEADER_NAME,
            RequestCorrelationConsts.REQUEST_HEADER_NAME);
    assertThat(request.headers().get(RequestCorrelationConsts.SESSION_HEADER_NAME))
        .isNotEmpty()
        .hasSize(1)
        .first()
        .isEqualTo(SESSION_ID);
    assertThat(request.headers().get(RequestCorrelationConsts.REQUEST_HEADER_NAME))
        .isNotEmpty()
        .hasSize(1)
        .first()
        .isEqualTo(REQUEST_ID);
  }

  @Test
  public void shouldSetCustomHeader() {

    // given
    final String customSessionHeader = "My-Session";
    final String customRequestHeader = "My-Request";
    properties.setSessionHeaderName(customSessionHeader);
    properties.setRequestHeaderName(customRequestHeader);
    CorrelationTestUtils.setCorrelatingIds(SESSION_ID, REQUEST_ID);
    final RequestTemplate request = new RequestTemplate();

    // when
    instance.apply(request);

    // then
    assertThat(request.headers())
        .doesNotContainKeys(
            RequestCorrelationConsts.SESSION_HEADER_NAME,
            RequestCorrelationConsts.REQUEST_HEADER_NAME)
        .containsKeys(customSessionHeader, customRequestHeader);
    assertThat(request.headers().get(customSessionHeader))
        .isNotEmpty()
        .hasSize(1)
        .first()
        .isEqualTo(SESSION_ID);
    assertThat(request.headers().get(customRequestHeader))
        .isNotEmpty()
        .hasSize(1)
        .first()
        .isEqualTo(REQUEST_ID);
  }

  @Test
  public void shouldNotSetHeader() {

    // given
    final RequestTemplate request = new RequestTemplate();

    // when
    instance.apply(request);

    // then
    assertThat(request.headers())
        .doesNotContainKeys(
            RequestCorrelationConsts.SESSION_HEADER_NAME,
            RequestCorrelationConsts.REQUEST_HEADER_NAME);
  }
}
