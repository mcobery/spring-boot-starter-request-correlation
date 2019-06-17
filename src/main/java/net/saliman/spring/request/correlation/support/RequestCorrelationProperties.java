/*
 * Copyright (c) 2017 the original author or authors
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
package net.saliman.spring.request.correlation.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * The request correlation properties.
 *
 * @author Jakub Narloch
 * @author Stven C. Saliman
 */
@ConfigurationProperties(prefix = "request.correlation")
public class RequestCorrelationProperties {
    /**
     * The priority order of the filter.  Defaults to Ordered.HIGHEST_PRECEDENCE,
     * so the correlating id is set very early in the process, but you may need
     * to override this.  For example, if you use shared sessions via the
     * spring-session project, you need to make sure the request correlation
     * filter comes AFTER the Spring Session filter, or the request correlation
     * filter won't get the right request object and your ids will be wrong.
     */
    private int filterOrder = Ordered.HIGHEST_PRECEDENCE;

    /**
     * The starting point of the filter order property.  This value will be
     * added to the filter-order property to get the actual order.  Defaults to
     * "zero" so that using filter-order by itself gives predictable results.
     * If you want the filter order to be an offset from the Highest Precedence,
     * set the filter-order to the offset, and filter-order-from to
     * "highest_precedence"
     */
    private FilterOrderOffset filterOrderFrom = FilterOrderOffset.ZERO;


    /**
     * Header name for the session id.  Defaults to "X-Session-Id"
     */
    private String sessionHeaderName = RequestCorrelationConsts.SESSION_HEADER_NAME;
    /**
     * Header name for the request id. Defaults to "X-Request-Id"
     */
    private String requestHeaderName = RequestCorrelationConsts.REQUEST_HEADER_NAME;

    /**
     * Creates new instance of {@link RequestCorrelationProperties} class.
     */
    public RequestCorrelationProperties() {
    }

    /**
     * @return the current ordering of the request correlation filter.
     */
    public int getFilterOrder() {
        return filterOrder;
    }

    /**
     * @param filterOrder the filter order to use.
     */
    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    /**
     * Retrieves the header names.
     *
     * @return the header names
     */
    public String getSessionHeaderName() {
        return sessionHeaderName;
    }

    /**
     * Sets the header names.
     *
     * @param headerName the header names
     */
    public void setSessionHeaderName(String headerName) {
        this.sessionHeaderName = headerName;
    }

    /**
     * Retrieves the header names.
     *
     * @return the header names
     */
    public String getRequestHeaderName() {
        return requestHeaderName;
    }

    /**
     * Sets the header names.
     *
     * @param headerName the header names
     */
    public void setRequestHeaderName(String headerName) {
        this.requestHeaderName = headerName;
    }

    public FilterOrderOffset getFilterOrderFrom() {
        return filterOrderFrom;
    }

    public void setFilterOrderFrom(FilterOrderOffset filterOrderFrom) {
        this.filterOrderFrom = filterOrderFrom;
    }
}
