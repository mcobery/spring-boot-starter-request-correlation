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
package net.saliman.spring.request.correlation;

import net.saliman.spring.request.correlation.filter.DefaultRequestCorrelation;
import net.saliman.spring.request.correlation.support.RequestCorrelationConsts;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * An convenient utility class.
 *
 * @author Jakub Narloch
 */
public class CorrelationTestUtils {

    /**
     * Sets the request correlation id.
     *
     * @param requestId the request id
     */
    public static void setCorrelatingIds(String sessionId, String requestId) {
        RequestContextHolder.getRequestAttributes().setAttribute(RequestCorrelationConsts.ATTRIBUTE_NAME,
                new DefaultRequestCorrelation(sessionId, requestId), RequestAttributes.SCOPE_REQUEST);
    }
}
