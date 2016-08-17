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

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link FeignCorrelationInterceptor} class.
 *
 * @author Jakub Narloch
 */
public class FeignCorrelationInterceptorTest {

    private FeignCorrelationInterceptor instance;

    @Before
    public void setUp() throws Exception {

        instance = new FeignCorrelationInterceptor(new RequestCorrelationProperties());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @After
    public void tearDown() throws Exception {

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldSetHeader() {

        // given
        final String sessionId = "TEST_SESSION_ID";
        final String requestId = "TEST_REQUEST_ID";
        CorrelationTestUtils.setCorrelatingIds(sessionId, requestId);
        final RequestTemplate request = new RequestTemplate();

        // when
        instance.apply(request);

        // then
        assertTrue(request.headers().containsKey(RequestCorrelationConsts.SESSION_HEADER_NAME));
        assertEquals(1, request.headers().get(RequestCorrelationConsts.SESSION_HEADER_NAME).size());
        assertEquals(sessionId, request.headers().get(RequestCorrelationConsts.SESSION_HEADER_NAME).iterator().next());
        assertTrue(request.headers().containsKey(RequestCorrelationConsts.REQUEST_HEADER_NAME));
        assertEquals(1, request.headers().get(RequestCorrelationConsts.REQUEST_HEADER_NAME).size());
        assertEquals(requestId, request.headers().get(RequestCorrelationConsts.REQUEST_HEADER_NAME).iterator().next());
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