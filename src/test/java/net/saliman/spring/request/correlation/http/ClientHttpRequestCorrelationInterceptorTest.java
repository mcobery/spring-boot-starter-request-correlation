/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.saliman.spring.request.correlation.http;

import net.saliman.spring.request.correlation.CorrelationTestUtils;
import net.saliman.spring.request.correlation.support.RequestCorrelationConsts;
import net.saliman.spring.request.correlation.support.RequestCorrelationProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the {@link ClientHttpRequestCorrelationInterceptor} class.
 *
 * @author Jakub Narloch
 */
public class ClientHttpRequestCorrelationInterceptorTest {

    private ClientHttpRequestCorrelationInterceptor instance;

    @Before
    public void setUp() throws Exception {
        instance = new ClientHttpRequestCorrelationInterceptor(new RequestCorrelationProperties());

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @After
    public void tearDown() throws Exception {

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldSetHeader() throws IOException {

        // given
        final String sessionId = UUID.randomUUID().toString();
        final String requestId = UUID.randomUUID().toString();
        CorrelationTestUtils.setCorrelatingIds(sessionId, requestId);

        final HttpRequest request = mock(HttpRequest.class);
        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        final byte[] body = new byte[0];

        when(request.getHeaders()).thenReturn(new HttpHeaders());

        // when
        instance.intercept(request, body, execution);

        // then
        assertTrue(request.getHeaders().containsKey(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertEquals(sessionId, request.getHeaders().getFirst(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertTrue(request.getHeaders().containsKey(RequestCorrelationConsts.REQUEST_HEADER_NAME));
        assertEquals(requestId, request.getHeaders().getFirst(RequestCorrelationConsts.REQUEST_HEADER_NAME));
        verify(execution).execute(request, body);
    }

    @Test
    public void shouldNotSetHeader() throws IOException {

        // given
        final HttpRequest request = mock(HttpRequest.class);
        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        final byte[] body = new byte[0];

        when(request.getHeaders()).thenReturn(new HttpHeaders());

        // when
        instance.intercept(request, body, execution);

        // then
        assertFalse(request.getHeaders().containsKey(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertFalse(request.getHeaders().containsKey(RequestCorrelationConsts.REQUEST_HEADER_NAME));
        verify(execution).execute(request, body);
    }
}