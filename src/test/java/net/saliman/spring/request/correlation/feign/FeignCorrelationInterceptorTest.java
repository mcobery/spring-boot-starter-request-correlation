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
package net.saliman.spring.request.correlation.feign;

import feign.RequestTemplate;
import net.saliman.spring.request.correlation.CorrelationTestUtils;
import net.saliman.spring.request.correlation.support.RequestCorrelationConsts;
import net.saliman.spring.request.correlation.support.RequestCorrelationProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Before
    public void setUp() throws Exception {
        instance = new FeignCorrelationInterceptor(properties);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @After
    public void tearDown() throws Exception {

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
        assertTrue(request.headers().containsKey(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertEquals(1, request.headers().get(RequestCorrelationConsts.SESSION_HEADER_NAME).size());
        assertEquals(SESSION_ID, request.headers().get(RequestCorrelationConsts.SESSION_HEADER_NAME).iterator().next());
        assertTrue(request.headers().containsKey(RequestCorrelationConsts.REQUEST_HEADER_NAME));
        assertEquals(1, request.headers().get(RequestCorrelationConsts.REQUEST_HEADER_NAME).size());
        assertEquals(REQUEST_ID, request.headers().get(RequestCorrelationConsts.REQUEST_HEADER_NAME).iterator().next());
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
        assertFalse(request.headers().containsKey(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertTrue(request.headers().containsKey(customSessionHeader));
        assertEquals(1, request.headers().get(customSessionHeader).size());
        assertEquals(SESSION_ID, request.headers().get(customSessionHeader).iterator().next());
        assertFalse(request.headers().containsKey(RequestCorrelationConsts.REQUEST_HEADER_NAME));
        assertTrue(request.headers().containsKey(customRequestHeader));
        assertEquals(1, request.headers().get(customRequestHeader).size());
        assertEquals(REQUEST_ID, request.headers().get(customRequestHeader).iterator().next());
    }

    @Test
    public void shouldNotSetHeader() {

        // given
        final RequestTemplate request = new RequestTemplate();

        // when
        instance.apply(request);

        // then
        assertFalse(request.headers().containsKey(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertFalse(request.headers().containsKey(RequestCorrelationConsts.REQUEST_HEADER_NAME));
    }

}
