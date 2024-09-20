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
package net.cobery.spring.request.correlation.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.cobery.spring.request.correlation.api.CorrelationIdGenerator;
import net.cobery.spring.request.correlation.api.RequestCorrelation;
import net.cobery.spring.request.correlation.api.RequestCorrelationInterceptor;
import net.cobery.spring.request.correlation.generator.DefaultIdGenerator;
import net.cobery.spring.request.correlation.support.RequestCorrelationConsts;
import net.cobery.spring.request.correlation.support.RequestCorrelationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests the {@link RequestCorrelationFilter} class.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationFilterTest {

  private RequestCorrelationFilter instance;

  private CorrelationIdGenerator generator = new DefaultIdGenerator();

  private List<RequestCorrelationInterceptor> interceptors = new ArrayList<>();

  private RequestCorrelationProperties properties = new RequestCorrelationProperties();

  @BeforeEach
  public void setUp() {

    instance = new RequestCorrelationFilter(generator, interceptors, properties);
  }

  @Test
  public void shouldInitiateCorrelationId() throws IOException, ServletException {

    // given
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockFilterChain chain = new MockFilterChain();

    // when
    instance.doFilter(request, response, chain);

    // then
    assertThat(request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME)).isNotNull();
    assertThat(
            ((HttpServletRequest) chain.getRequest())
                .getHeader(RequestCorrelationConsts.SESSION_HEADER_NAME))
        .isNotNull();
    assertThat(
            ((HttpServletRequest) chain.getRequest())
                .getHeader(RequestCorrelationConsts.REQUEST_HEADER_NAME))
        .isNotNull();
  }

  @Test
  public void shouldUseExistingCorrelationId() throws IOException, ServletException {

    // given
    final String sessionId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockFilterChain chain = new MockFilterChain();

    request.addHeader(RequestCorrelationConsts.SESSION_HEADER_NAME, sessionId);
    request.addHeader(RequestCorrelationConsts.REQUEST_HEADER_NAME, requestId);

    // when
    instance.doFilter(request, response, chain);

    // then
    final Object requestCorrelation = request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME);
    assertThat(requestCorrelation).isNotNull();
    assertThat(requestId).isEqualTo(((RequestCorrelation) requestCorrelation).getRequestId());

    String header =
        ((HttpServletRequest) chain.getRequest())
            .getHeader(RequestCorrelationConsts.SESSION_HEADER_NAME);
    assertThat(header).isNotNull();
    assertThat(sessionId).isEqualTo(header);
    header =
        ((HttpServletRequest) chain.getRequest())
            .getHeader(RequestCorrelationConsts.REQUEST_HEADER_NAME);
    assertThat(header).isNotNull();
    assertThat(requestId).isEqualTo(header);
  }

  @Test
  public void shouldUseCustomHeader() throws IOException, ServletException {

    // given
    final String sessionHeaderName = "X-SessionTraceId";
    final String requestHeaderName = "X-RequestTraceId";
    final String sessionId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockFilterChain chain = new MockFilterChain();

    request.addHeader(sessionHeaderName, sessionId);
    request.addHeader(requestHeaderName, requestId);
    properties.setSessionHeaderName(sessionHeaderName);
    properties.setRequestHeaderName(requestHeaderName);

    // when
    instance.doFilter(request, response, chain);

    // then
    final Object requestCorrelation = request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME);
    assertThat(requestCorrelation).isNotNull();
    assertThat(requestId).isEqualTo(((RequestCorrelation) requestCorrelation).getRequestId());

    String header = ((HttpServletRequest) chain.getRequest()).getHeader(sessionHeaderName);
    assertThat(header).isNotNull();
    assertThat(sessionId).isEqualTo(header);
    header = ((HttpServletRequest) chain.getRequest()).getHeader(requestHeaderName);
    assertThat(header).isNotNull();
    assertThat(requestId).isEqualTo(header);
  }

  @Test
  public void shouldInvokeInterceptor() throws IOException, ServletException {

    // given
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockFilterChain chain = new MockFilterChain();

    final RequestCorrelationInterceptor interceptor = mock(RequestCorrelationInterceptor.class);
    interceptors.add(interceptor);

    // when
    instance.doFilter(request, response, chain);

    // then
    final String sessionId =
        ((HttpServletRequest) chain.getRequest())
            .getHeader(RequestCorrelationConsts.SESSION_HEADER_NAME);
    final String requestId =
        ((HttpServletRequest) chain.getRequest())
            .getHeader(RequestCorrelationConsts.REQUEST_HEADER_NAME);
    final RequestCorrelation correlationIds =
        (RequestCorrelation) request.getAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME);
    assertThat(sessionId).isNotNull();
    assertThat(requestId).isNotNull();
    assertThat(correlationIds).isNotNull();
    assertThat(sessionId).isEqualTo(correlationIds.getSessionId());
    assertThat(requestId).isEqualTo(correlationIds.getRequestId());

    verify(interceptor).afterCorrelationIdSet(sessionId, requestId);
    verify(interceptor).cleanUp(sessionId, requestId);
  }
}
