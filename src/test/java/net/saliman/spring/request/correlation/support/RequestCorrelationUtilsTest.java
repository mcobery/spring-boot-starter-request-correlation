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
package net.saliman.spring.request.correlation.support;

import net.saliman.spring.request.correlation.filter.DefaultRequestCorrelation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link RequestCorrelationUtils} class.
 *
 * @author Jakub Narloch
 */
public class RequestCorrelationUtilsTest {

    @Before
    public void setUp() throws Exception {

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @After
    public void tearDown() throws Exception {

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
        assertNull(currentSessionId);
        assertNull(currentRequestId);
    }

    @Test
    public void shouldRetrieveCorrelatingIds() {

        // given
        final String sessionId = UUID.randomUUID().toString();
        final String requestId = UUID.randomUUID().toString();
        RequestContextHolder.getRequestAttributes().setAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME,
                new DefaultRequestCorrelation(sessionId, requestId), RequestAttributes.SCOPE_REQUEST);

        // when
        final String currentSessionId = RequestCorrelationUtils.getCurrentSessionId();
        final String currentRequestId = RequestCorrelationUtils.getCurrentRequestId();

        // then
        assertEquals(sessionId, currentSessionId);
        assertEquals(requestId, currentRequestId);
    }
}
